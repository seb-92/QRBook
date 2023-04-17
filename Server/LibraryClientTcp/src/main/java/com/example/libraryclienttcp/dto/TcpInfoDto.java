package com.example.libraryclienttcp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TODO: Describe this class (The first line - until the first dot - will interpret as line brief description).
 */
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class TcpInfoDto {
    private String firstName;
    private String lastName;
    private String address;
    private String orderType;
    private Integer yearOfPublish;
    private String title;
    private String author;
    private Long bookId;
}
