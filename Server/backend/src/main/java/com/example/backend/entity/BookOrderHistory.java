package com.example.backend.entity;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.backend.dto.BookOrderHistoryDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Table( name = "book_order_history" )
@Entity
public class BookOrderHistory
{
    @Id
    @Column( name = "id" )
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long orderId;

    @Column( name = "date_of_order", nullable = false )
    private Instant dateOfOrder;
    @Column( name = "order_type" )
    private String orderType;

    @ManyToOne( cascade =
    { PERSIST, MERGE, REFRESH, DETACH }, fetch = FetchType.LAZY )
    @JoinColumn( name = "book", referencedColumnName = "id", nullable = false )
    private Book book;

    @ManyToOne( cascade =
    { PERSIST, MERGE, REFRESH, DETACH }, fetch = FetchType.LAZY )
    @JoinColumn( name = "userId", referencedColumnName = "id", nullable = false )
    private User user;

    public BookOrderHistory( Instant aDateOfOrder, User aUser, String aOrderType, Book aBook )
    {
        dateOfOrder = aDateOfOrder;
        user = aUser;
        orderType = aOrderType;
        book = aBook;
    }

    public static BookOrderHistoryDto convertToDto( BookOrderHistory aBookOrderHistory )
    {
        return new BookOrderHistoryDto( aBookOrderHistory.getDateOfOrder(), aBookOrderHistory.getOrderType(),
            Book.convertToDto( aBookOrderHistory.getBook() ) );
    }
}
