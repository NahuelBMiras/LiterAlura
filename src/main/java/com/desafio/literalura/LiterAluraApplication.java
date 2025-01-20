package com.desafio.literalura;

import com.desafio.literalura.model.Author;
import com.desafio.literalura.model.Book;
import com.desafio.literalura.service.AuthorService;
import com.desafio.literalura.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

@SpringBootApplication
public class LiterAluraApplication {

    // Servicios de libros y autores inyectados automáticamente por Spring
    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    // Punto de entrada principal de la aplicación
    public static void main(String[] args) {
        SpringApplication.run(LiterAluraApplication.class, args);
    }

    // Componente CommandLineRunner para ejecutar lógica cuando la aplicación inicie
    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Scanner scanner = new Scanner(System.in); // Scanner para leer la entrada del usuario

            // Bucle principal para interactuar con el usuario
            while (true) {
                System.out.println("\nBienvenido a LiterAlura. Elige una opción:");
                System.out.println("1. Buscar libros por título");
                System.out.println("2. Listar libros por género literario");
                System.out.println("3. Listar autores por género literario");
                System.out.println("4. Listar autores vivos en un año específico");
                System.out.println("5. Mostrar cantidad de libros por idioma");
                System.out.println("6. Libros más buscados");
                System.out.println("7. Salir");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consumir el salto de línea

                // Evaluar la opción elegida por el usuario
                switch (choice) {
                    case 1 -> searchBookByTitle(scanner); // Buscar libros por título
                    case 2 -> listBooksByGenre(scanner); // Listar libros por género
                    case 3 -> listAuthorsByGenre(scanner); // Listar autores por género literario
                    case 4 -> listAuthorsAliveInYear(scanner); // Autores vivos en un año específico
                    case 5 -> showBookCountByLanguage(scanner); // Cantidad de libros por idioma
                    case 6 -> showMostSearchedBooks(scanner); // Libros más buscados
                    case 7 -> {
                        System.out.println("Gracias por usar LiterAlura. ¡Hasta pronto!");
                        return; // Salir del programa
                    }
                    default -> System.out.println("Opción no válida. Por favor, intenta de nuevo.");
                }
            }
        };
    }

    // Método para buscar libros por título
    private void searchBookByTitle(Scanner scanner) {
        System.out.println("Introduce el título del libro:");
        String title = scanner.nextLine();
        Book book = bookService.searchBookByTitle(title); // Buscar libro por título
        if (book != null) {
            System.out.println("Nombre del libro: " + book.getTitle());
            System.out.println("Autor: " + (book.getAuthor() != null ? book.getAuthor().getName() : "Desconocido"));
            System.out.println("Género literario: " + book.getGenre());
            System.out.println("Idiomas: " + String.join(", ", book.getLanguages()));
            bookService.incrementSearchCount(book); // Incrementar el contador de búsquedas del libro
        } else {
            System.out.println("No se encontró el libro.");
        }
    }

    // Método para listar libros según un género literario específico
    private void listBooksByGenre(Scanner scanner) {
        System.out.println("Introduce el género literario:");
        String genre = scanner.nextLine();
        List<String> bookTitles = bookService.getBookTitlesByGenre(genre); // Obtener títulos por género
        if (!bookTitles.isEmpty()) {
            System.out.println("Libros encontrados:");
            bookTitles.forEach(System.out::println); // Imprimir títulos encontrados
        } else {
            System.out.println("No se encontraron libros para el género especificado.");
        }
    }

    // Método para listar autores que escriben en un género específico
    private void listAuthorsByGenre(Scanner scanner) {
        System.out.println("Introduce el género literario:");
        String genre = scanner.nextLine();
        List<Author> authors = authorService.getAuthorsByBookGenre(genre); // Obtener autores por género
        if (!authors.isEmpty()) {
            System.out.println("Autores encontrados:");
            authors.forEach(author -> System.out.println(author.getName())); // Imprimir nombres de los autores
        } else {
            System.out.println("No se encontraron autores para el género especificado.");
        }
    }

    // Método para listar autores que estaban vivos en un año específico
    private void listAuthorsAliveInYear(Scanner scanner) {
        System.out.println("Introduce el año:");
        int year = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        List<Author> authors = authorService.getAuthorsAliveInYear(year); // Obtener autores vivos en un año
        if (!authors.isEmpty()) {
            System.out.println("Autores vivos en el año " + year + ":");
            for (Author author : authors) {
                System.out.println(author.getName() + " (Nacimiento: " + author.getBirthYear() +
                        ", Fallecimiento: " + (author.getDeathYear() != null ? author.getDeathYear() : "N/A") + ")");
            }
        } else {
            System.out.println("No se encontraron autores vivos en el año especificado.");
        }
    }

    // Método para mostrar la cantidad de libros en un idioma específico
    private void showBookCountByLanguage(Scanner scanner) {
        System.out.println("Introduce el código de idioma (ej. en, es):");
        String language = scanner.nextLine().toLowerCase();
        Map<String, Long> bookCount = bookService.getBookCountByLanguage(language); // Obtener cantidad de libros por idioma
        if (!bookCount.isEmpty()) {
            System.out.println("Cantidad de libros en " + language + ": " + bookCount.get(language));
        } else {
            System.out.println("No se encontró información para el idioma especificado.");
        }
    }

    // Método para mostrar los libros más buscados
    private void showMostSearchedBooks(Scanner scanner) {
        System.out.println("Introduce el número de libros a mostrar:");
        int limit = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        List<Book> mostSearchedBooks = bookService.getMostSearchedBooks(limit); // Obtener libros más buscados
        if (!mostSearchedBooks.isEmpty()) {
            System.out.println("Los libros más buscados son:");
            for (Book book : mostSearchedBooks) {
                System.out.println(book.getTitle() + " - Búsquedas: " + book.getSearchCount());
            }
        } else {
            System.out.println("No hay información de libros buscados.");
        }
    }
}
