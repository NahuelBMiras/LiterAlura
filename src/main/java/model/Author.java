package com.desafio.literalura.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authors") // Define la tabla "authors" en la base de datos
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // La clave primaria se genera automáticamente
    private Long id;

    @Column(unique = true) // El nombre debe ser único en la base de datos
    private String name;

    private Integer birthYear; // Año de nacimiento del autor
    private Integer deathYear; // Año de fallecimiento del autor

    // Relación uno a muchos entre Author y Book (un autor puede tener varios libros)
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Book> books = new HashSet<>(); // Conjunto de libros asociados al autor

    // Métodos getter y setter para los atributos

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public Integer getDeathYear() {
        return deathYear;
    }

    public void setDeathYear(Integer deathYear) {
        this.deathYear = deathYear;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }

    // Métodos para agregar y eliminar libros de la colección

    public void addBook(Book book) {
        books.add(book); // Agregar el libro al conjunto de libros del autor
        book.setAuthor(this); // Establecer al autor del libro
    }

    public void removeBook(Book book) {
        books.remove(book); // Eliminar el libro del conjunto de libros del autor
        book.setAuthor(null); // Desvincular el autor del libro
    }
}
