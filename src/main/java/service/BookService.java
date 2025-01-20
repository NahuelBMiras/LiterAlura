package com.desafio.literalura.service;

import com.desafio.literalura.model.Book;
import com.desafio.literalura.model.Author;
import com.desafio.literalura.repository.BookRepository;
import com.desafio.literalura.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service // Marca la clase como un servicio de Spring para manejar la lógica de negocio
public class BookService {

    @Autowired
    private RestTemplate restTemplate; // Para hacer peticiones HTTP a la API externa

    @Autowired
    private ObjectMapper objectMapper; // Para convertir respuestas JSON en objetos Java

    @Autowired
    private BookRepository bookRepository; // Repositorio para manejar la base de datos de libros

    @Autowired
    private AuthorRepository authorRepository; // Repositorio para manejar la base de datos de autores

    private static final String API_URL = "https://gutendex.com/books"; // URL base de la API externa

    private Map<String, String> genreTranslationMap = new HashMap<>(); // Mapa para traducir géneros de inglés a español
    private Map<String, String> titleTranslationMap = new HashMap<>(); // Mapa para traducir títulos de libros

    // Constructor que inicializa los mapas de traducción
    public BookService() {
        initializeTranslationMaps();
    }

    // Método para inicializar los mapas de traducción de géneros y títulos
    private void initializeTranslationMaps() {
        genreTranslationMap.put("Fiction", "Ficción");
        genreTranslationMap.put("Drama", "Drama");
        genreTranslationMap.put("Comedy", "Comedia");
        genreTranslationMap.put("Humor", "Humor");
        genreTranslationMap.put("Other", "Otro");

        titleTranslationMap.put("pride and prejudice", "orgullo y prejuicio");
        titleTranslationMap.put("romeo and juliet", "romeo y julieta");
        titleTranslationMap.put("the scarlet letter", "la letra escarlata");
        // Agregar más traducciones de títulos según sea necesario
    }

    // Método para buscar un libro por su título en la API externa
    @Transactional
    public Book searchBookByTitle(String title) {
        try {
            String searchTitle = translateTitleToEnglish(title.toLowerCase()); // Traducir el título a inglés si es necesario
            String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                    .queryParam("search", searchTitle) // Agregar parámetro de búsqueda al URL
                    .build()
                    .toUriString();

            // Hacer la solicitud a la API externa y obtener la respuesta JSON
            JsonNode response = objectMapper.readTree(restTemplate.getForObject(url, String.class));
            JsonNode results = response.get("results");

            // Procesar los resultados de la API y verificar si el título coincide
            if (results.isArray() && results.size() > 0) {
                for (JsonNode bookData : results) {
                    String bookTitle = bookData.get("title").asText();
                    if (bookTitle.toLowerCase().contains(searchTitle.toLowerCase())) {
                        Book book = createBookFromJsonNode(bookData); // Crear el libro a partir de los datos JSON
                        translateBookToSpanish(book); // Traducir el libro al español
                        return saveBook(book); // Guardar el libro en la base de datos
                    }
                }
            }
            System.out.println("No se encontró el libro con título: " + title);
        } catch (Exception e) {
            System.err.println("Error al buscar el libro en la API: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Método para traducir un título del español al inglés
    private String translateTitleToEnglish(String spanishTitle) {
        return titleTranslationMap.entrySet().stream()
                .filter(entry -> spanishTitle.contains(entry.getValue()))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(spanishTitle);
    }

    // Método para obtener libros por género desde la API externa
    @Transactional
    public List<Book> getBooksByGenre(String genre) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                    .queryParam("topic", translateToEnglish(genre)) // Traducir el género al inglés
                    .build()
                    .toUriString();

            // Hacer la solicitud a la API externa y obtener la respuesta JSON
            JsonNode response = objectMapper.readTree(restTemplate.getForObject(url, String.class));
            JsonNode results = response.get("results");

            List<Book> books = new ArrayList<>();
            if (results.isArray()) {
                for (JsonNode bookData : results) {
                    Book book = createBookFromJsonNode(bookData); // Crear el libro a partir de los datos JSON
                    translateBookToSpanish(book); // Traducir el libro al español
                    books.add(saveBook(book)); // Guardar el libro en la base de datos
                }
            }
            if (books.isEmpty()) {
                System.out.println("No se encontraron libros del género: " + genre);
            }
            return books;
        } catch (Exception e) {
            System.err.println("Error al obtener libros por género: " + e.getMessage());
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    // Método para crear un libro a partir de los datos JSON de la API
    private Book createBookFromJsonNode(JsonNode bookData) {
        Book book = new Book();
        book.setTitle(bookData.get("title").asText());
        book.setDownloadCount(bookData.get("download_count").asLong());
        book.setYearWritten(bookData.has("copyright") ? bookData.get("copyright").asInt() : null);
        book.setGenre(determineGenre(bookData.get("subjects")));
        book.setSearchCount(0L);

        // Procesar los autores del libro
        JsonNode authors = bookData.get("authors");
        if (authors.isArray() && authors.size() > 0) {
            JsonNode authorData = authors.get(0);
            Author author = new Author();
            author.setName(authorData.get("name").asText());
            author.setBirthYear(authorData.has("birth_year") ? authorData.get("birth_year").asInt() : null);
            author.setDeathYear(authorData.has("death_year") ? authorData.get("death_year").asInt() : null);
            book.setAuthor(findOrCreateAuthor(author)); // Verificar o crear el autor
        }

        // Procesar los idiomas del libro
        JsonNode languages = bookData.get("languages");
        if (languages.isArray()) {
            book.setLanguages(new ArrayList<>());
            for (JsonNode lang : languages) {
                book.getLanguages().add(lang.asText());
            }
        }

        return book;
    }

    // Método para determinar el género del libro a partir de los temas
    private String determineGenre(JsonNode subjects) {
        if (subjects.isArray()) {
            for (JsonNode subject : subjects) {
                String subjectStr = subject.asText().toLowerCase();
                if (subjectStr.contains("fiction")) {
                    return "Fiction";
                } else if (subjectStr.contains("drama")) {
                    return "Drama";
                } else if (subjectStr.contains("comedy")) {
                    return "Comedy";
                } else if (subjectStr.contains("humor")) {
                    return "Humor";
                }
            }
        }
        return "Other";
    }

    // Método para traducir el libro al español
    private void translateBookToSpanish(Book book) {
        book.setTitle(translateTitleToSpanish(book.getTitle()));
        book.setGenre(genreTranslationMap.getOrDefault(book.getGenre(), book.getGenre()));
        if (book.getAuthor() != null) {
            book.getAuthor().setName(translateAuthorNameToSpanish(book.getAuthor().getName()));
        }
    }

    // Método para traducir el título del libro al español
    private String translateTitleToSpanish(String englishTitle) {
        return titleTranslationMap.entrySet().stream()
                .filter(entry -> englishTitle.toLowerCase().contains(entry.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(englishTitle);
    }

    // Método para traducir el nombre del autor al español (si es necesario)
    private String translateAuthorNameToSpanish(String authorName) {
        return authorName; // Implementar la lógica de traducción si es necesario
    }

    // Método para traducir el género al inglés
    private String translateToEnglish(String genre) {
        for (Map.Entry<String, String> entry : genreTranslationMap.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(genre)) {
                return entry.getKey();
            }
        }
        return genre;
    }

    // Método para guardar un libro en la base de datos
    @Transactional
    public Book saveBook(Book book) {
        try {
            if (book.getAuthor() != null) {
                Author existingAuthor = findOrCreateAuthor(book.getAuthor());
                book.setAuthor(existingAuthor);
            }

            // Verificar si el libro ya existe en la base de datos
            List<Book> existingBooks = bookRepository.findByTitleAndAuthor(book.getTitle(), book.getAuthor());
            if (!existingBooks.isEmpty()) {
                Book existingBook = existingBooks.get(0);
                existingBook.setDownloadCount(book.getDownloadCount());
                existingBook.setYearWritten(book.getYearWritten());
                existingBook.setGenre(book.getGenre());
                existingBook.setLanguages(book.getLanguages());
                return bookRepository.save(existingBook);
            } else {
                return bookRepository.save(book);
            }
        } catch (Exception e) {
            System.err.println("Error al guardar el libro: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Método para verificar o crear un autor en la base de datos
    @Transactional
    public Author findOrCreateAuthor(Author author) {
        return authorRepository.findByName(author.getName())
                .orElseGet(() -> {
                    try {
                        return authorRepository.save(author);
                    } catch (Exception e) {
                        System.err.println("Error al guardar el autor: " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    }
                });
    }

    // Método para obtener todos los libros desde la base de datos
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Método para obtener los títulos de los libros por género
    @Transactional
    public List<String> getBookTitlesByGenre(String genre) {
        return getBooksByGenre(genre).stream()
                .map(Book::getTitle)
                .distinct()
                .collect(Collectors.toList());
    }

    // Método para obtener el conteo de libros por idioma
    @Transactional(readOnly = true)
    public Map<String, Long> getBookCountByLanguage(String language) {
        String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("languages", language)
                .build()
                .toUriString();

        try {
            JsonNode response = objectMapper.readTree(restTemplate.getForObject(url, String.class));
            long count = response.get("count").asLong();
            return Map.of(language, count);
        } catch (Exception e) {
            System.err.println("Error al obtener el conteo de libros por idioma: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    // Método para obtener los libros más buscados
    @Transactional(readOnly = true)
    public List<Book> getMostSearchedBooks(int limit) {
        return bookRepository.findAllByOrderBySearchCountDesc(PageRequest.of(0, limit));
    }

    // Método para incrementar el contador de búsquedas de un libro
    @Transactional
    public void incrementSearchCount(Book book) {
        book.setSearchCount(book.getSearchCount() + 1);
        bookRepository.save(book);
    }
}
