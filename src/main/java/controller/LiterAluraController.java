package com.desafio.literalura.controller;

import com.desafio.literalura.model.Book;
import com.desafio.literalura.model.Author;
import com.desafio.literalura.service.BookService;
import com.desafio.literalura.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Controller
public class LiterAluraController {

    // Servicios inyectados para interactuar con libros y autores
    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    // Scanner para leer la entrada del usuario
    private Scanner scanner = new Scanner(System.in);

    // Ruta principal que responde con un mensaje de bienvenida
    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "Bienvenido a LiterAlura";
    }

    // Ruta para manejar la interacción con el usuario y ofrecer opciones
    @PostMapping("/interact")
    @ResponseBody
    public String interact() {
        while (true) {
            // Menú de opciones para el usuario
            System.out.println("\nBienvenido a LiterAlura. Elige una opción:");
            System.out.println("1. Buscar libros por título");
            System.out.println("2. Listar libros por género literario");
            System.out.println("3. Listar autores por género literario");
            System.out.println("4. Listar autores vivos en un año específico");
            System.out.println("5. Mostrar cantidad de libros por idioma");
            System.out.println("6. Libros más buscados");
            System.out.println("7. Salir");

            // Lectura de la opción seleccionada
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            // Llamar al método correspondiente según la opción seleccionada
            switch (choice) {
                case 1:
                    searchBookByTitle();
                    break;
                case 2:
                    listBooksByGenre();
                    break;
                case 3:
                    listAuthorsByGenre();
                    break;
                case 4:
                    listAuthorsAliveInYear();
                    break;
                case 5:
                    showBookCountByLanguage();
                    break;
                case 6:
                    showMostSearchedBooks();
                    break;
                case 7:
                    return "Gracias por usar LiterAlura. ¡Hasta pronto!";
                default:
                    System.out.println("Opción no válida. Por favor, intenta de nuevo.");
            }
        }
    }

    // Método para buscar libros por título
    private void searchBookByTitle() {
        System.out.println("Introduce el título del libro:");
        String title = scanner.nextLine();
        // Llamar al servicio de libros para buscar por título
        Book book = bookService.searchBookByTitle(title);
        if (book != null) {
            // Mostrar la información del libro encontrado
            System.out.println("Nombre del libro: " + book.getTitle());
            System.out.println("Autor: " + (book.getAuthor() != null ? book.getAuthor().getName() : "Desconocido"));
            System.out.println("Género literario: " + book.getGenre());
            System.out.println("Idiomas: " + String.join(", ", book.getLanguages()));
            bookService.incrementSearchCount(book); // Incrementar el contador de búsquedas del libro
        } else {
            System.out.println("No se encontró el libro.");
        }
    }

    // Método para listar libros según un género literario
    private void listBooksByGenre() {
        System.out.println("Introduce el género literario:");
        String genre = scanner.nextLine();
        // Llamar al servicio para obtener los títulos de libros por género
        List<String> bookTitles = bookService.getBookTitlesByGenre(genre);
        if (!bookTitles.isEmpty()) {
            // Mostrar los títulos de libros encontrados
            System.out.println("Libros encontrados:");
            bookTitles.forEach(System.out::println);
        } else {
            System.out.println("No se encontraron libros para el género especificado.");
        }
    }

    // Método para listar autores según un género literario
    private void listAuthorsByGenre() {
        System.out.println("Introduce el género literario:");
        String genre = scanner.nextLine();
        // Llamar al servicio para obtener los autores por género
        List<Author> authors = authorService.getAuthorsByBookGenre(genre);
        if (!authors.isEmpty()) {
            // Mostrar los nombres de los autores encontrados
            System.out.println("Autores encontrados:");
            authors.forEach(author -> System.out.println(author.getName()));
        } else {
            System.out.println("No se encontraron autores para el género especificado.");
        }
    }

    // Método para listar autores vivos en un año específico
    private void listAuthorsAliveInYear() {
        System.out.println("Introduce el año:");
        int year = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        // Llamar al servicio para obtener los autores vivos en ese año
        List<Author> authors = authorService.getAuthorsAliveInYear(year);
        if (!authors.isEmpty()) {
            // Mostrar los autores vivos en el año especificado
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
    private void showBookCountByLanguage() {
        System.out.println("Introduce el código de idioma (ej. en, es):");
        String language = scanner.nextLine().toLowerCase();
        // Llamar al servicio para obtener el conteo de libros por idioma
        Map<String, Long> bookCount = bookService.getBookCountByLanguage(language);
        if (!bookCount.isEmpty()) {
            // Mostrar la cantidad de libros en el idioma especificado
            System.out.println("Cantidad de libros en " + language + ": " + bookCount.get(language));
        } else {
            System.out.println("No se encontró información para el idioma especificado.");
        }
    }

    // Método para mostrar los libros más buscados
    private void showMostSearchedBooks() {
        System.out.println("Introduce el número de libros a mostrar:");
        int limit = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        // Llamar al servicio para obtener los libros más buscados
        List<Book> mostSearchedBooks = bookService.getMostSearchedBooks(limit);
        if (!mostSearchedBooks.isEmpty()) {
            // Mostrar los libros más buscados
            System.out.println("Los libros más buscados son:");
            for (Book book : mostSearchedBooks) {
                System.out.println(book.getTitle() + " - Búsquedas: " + book.getSearchCount());
            }
        } else {
            System.out.println("No hay información de libros buscados.");
        }
    }
}
