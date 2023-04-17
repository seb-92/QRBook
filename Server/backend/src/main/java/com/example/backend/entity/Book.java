package com.example.backend.entity;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.example.backend.dto.BookDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Table( name = "book" )
@Entity
public class Book
{
    @Id
    @Column( name = "id" )
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long bookId;

    @Column( name = "publish_year", nullable = false )
    private Integer yearOfPublish;

    @Column( name = "title", nullable = false )
    private String title;

    @Column( name = "author", nullable = false )
    private String author;

    @Column( name = "amount_of_pages" )
    private Integer amountOfPages;

    @Column( name = "available" )
    private Boolean available;

    @ManyToOne( cascade =
    { PERSIST, MERGE, REFRESH, DETACH }, fetch = FetchType.LAZY )
    @JoinColumn( name = "currently_ordered_by", referencedColumnName = "id" )
    private User currentlyOrderedBy;

    @OneToMany( targetEntity = BookOrderHistory.class, cascade =
    { PERSIST, MERGE, REFRESH, DETACH }, fetch = FetchType.LAZY, mappedBy = "book" )
    private List< BookOrderHistory > bookOrderHistories;

    public Book( BookDto aBookDto )
    {
        this.amountOfPages = aBookDto.getAmountOfPages();
        this.yearOfPublish = aBookDto.getYearOfPublish();
        this.title = aBookDto.getTitle();
        this.author = aBookDto.getAuthor();
        this.available = aBookDto.getAvailable();
    }

    public static BookDto convertToDto( Book aBook )
    {
        return new BookDto( aBook.getYearOfPublish(), aBook.getTitle(), aBook.getAuthor(),
            aBook.getAmountOfPages(), aBook.getAvailable(), aBook.getBookId() );
    }
}
