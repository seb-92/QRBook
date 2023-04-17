package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookDto
{
    private Integer yearOfPublish;
    private String title;
    private String author;
    private Integer amountOfPages;
    private Boolean available;
    private Long bookId;
}
