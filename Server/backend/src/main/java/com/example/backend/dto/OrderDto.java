package com.example.backend.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties( ignoreUnknown = true )
public class OrderDto
{
    private Long userId;
    private Instant timeOfOrder;
    private Long bookIdToOrder;
}
