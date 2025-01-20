package com.desafio.literalura.service;

import com.desafio.literalura.model.Author;
import com.desafio.literalura.model.Book;
import com.desafio.literalura.repository.AuthorRepository;
import com.desafio.literalura.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service // Anotación que marca la clase como un servicio que gestiona la lógica de negocio
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository; // Repositorio para interactuar con la base de datos de autores

    @Autowired
    private BookRepository bookRepository; // Repositorio para interactuar con la base de datos de libros

    @Autowired
    private BookService bookService; // Servicio para gestionar operaciones relacionadas con libros

    // Método para obtener todos los autores
    @Transactional(readOnly = true) // Anotación para indicar que este método no modificará la base de datos
    public List<Author> getAllAuthors() {
        return authorRepository.findAll(); // Retorna todos los autores de la base de datos
    }

    // Método para obtener autores que estaban vivos en un año específico
    @Transactional(readOnly = true)
    public List<Author> getAuthorsAliveInYear(int year) {
        return authorRepository.findAll().stream() // Obtiene todos los autores y filtra los vivos en ese año
                .filter(author -> (author.getBirthYear() == null || author.getBirthYear() <= year) && // Autor nacido antes o en el año
                        (author.getDeathYear() == null || author.getDeathYear() >= year)) // Autor fallecido después o en el año
                .collect(Collectors.toList()); // Convierte el resultado en una lista
    }

    // Método para obtener autores según el género literario de sus libros
    @Transactional(readOnly = true)
    public List<Author> getAuthorsByBookGenre(String genre) {
        List<Book> books = bookService.getBooksByGenre(genre); // Obtiene los libros del género especificado
        return books.stream()
                .map(Book::getAuthor) // Extrae los autores de los libros
                .filter(author -> author != null) // Filtra los libros que tienen un autor no nulo
                .distinct() // Elimina autores duplicados
                .collect(Collectors.toList()); // Convierte el resultado en una lista
    }
}
