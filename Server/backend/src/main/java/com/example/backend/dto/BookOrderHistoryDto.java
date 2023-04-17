package com.example.backend.dto;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class BookOrderHistoryDto
{
    private String dateOfOrder;
    private String orderType;
    private BookDto book;

    public BookOrderHistoryDto(Instant aDateOfOrder, String aOrderType, BookDto aBook) {
        Date tempDate = Date.from(aDateOfOrder);
        SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
        this.dateOfOrder = formatter.format(tempDate);
        orderType = aOrderType;
        book = aBook;
    }

    public void setDateOfOrder(Instant dateOfOrder) {
        Date tempDate = Date.from(dateOfOrder);
        SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
        this.dateOfOrder = formatter.format(tempDate);
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public void setBook(BookDto book) {
        this.book = book;
    }
}