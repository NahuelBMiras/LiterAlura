package com.desafio.literalura.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books") // Define la tabla "books" en la base de datos
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // La clave primaria se genera automáticamente
    private Long id;

    private String title; // Título del libro

    // Relación muchos a uno entre Book y Author (un libro tiene un solo autor)
    @ManyToOne(fetch = FetchType.LAZY) // Carga perezosa para la relación
    @JoinColumn(name = "author_id") // Establece la columna "author_id" como clave foránea
    private Author author;

    private Long downloadCount; // Contador de descargas del libro
    private Integer yearWritten; // Año en que fue escrito el libro
    private String genre; // Género literario del libro
    private Long searchCount; // Contador de búsquedas del libro

    // Relación de colección de elementos para almacenar los idiomas en los que está disponible el libro
    @ElementCollection
    @CollectionTable(name = "book_languages", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "language") // Columna que almacena los idiomas en la tabla "book_languages"
    private List<String> languages = new ArrayList<>(); // Lista de idiomas del libro

    // Métodos getter y setter para los atributos

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Long getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Long downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Integer getYearWritten() {
        return yearWritten;
    }

    public void setYearWritten(Integer yearWritten) {
        this.yearWritten = yearWritten;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Long getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(Long searchCount) {
        this.searchCount = searchCount;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }
}
