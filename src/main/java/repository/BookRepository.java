package com.desafio.literalura.repository;

import com.desafio.literalura.model.Book;
import com.desafio.literalura.model.Author;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findAllByOrderBySearchCountDesc(Pageable pageable);
    List<Book> findByGenreIgnoreCase(String genre);
    List<Book> findByTitleAndAuthor(String title, Author author);
    List<Book> findByLanguagesContaining(String language);
}