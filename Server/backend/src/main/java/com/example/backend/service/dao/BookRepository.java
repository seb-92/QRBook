package com.example.backend.service.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.entity.Book;

@Repository
public interface BookRepository extends JpaRepository< Book, Long >
{
    @Query( "select distinct b from Book b where b.available = true" )
    List< Book > getAvailableBookTitles();

    @Query( "select distinct b from Book b where b.available = false " )
    List< Book > getUnavailableBookTitles();

    @Query( "select b from Book b where b.bookId = :bookId" )
    Book getBookById( @Param( "bookId" ) Long bookId );

    @Query( "select b from Book b where b.currentlyOrderedBy.userId = :userId" )
    List< Book > getBooksCurrentlyOrderedByUser( @Param( "userId" ) Long userId );

    @Query( "select b from Book b where lower(b.author) like concat(:author, '%')" )
    List< Book > getBooksByAuthor( @Param( "author" ) String author );

    @Query( "select b from Book b where lower(b.title) like concat(:title, '%')" )
    List< Book > getBooksByTitle( @Param( "title" ) String title );

    @Query( "select b from Book b where b.yearOfPublish = :yearOfPublish" )
    List< Book > getBooksByYearOfPublish( @Param( "yearOfPublish" ) Integer yearOfPublish );
}
