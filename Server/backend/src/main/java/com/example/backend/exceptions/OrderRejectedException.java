package com.example.backend.exceptions;

/**
 * TODO: Describe this class (The first line - until the first dot - will interpret as the brief description).
 */
public class OrderRejectedException extends RuntimeException
{
    public OrderRejectedException( String message )
    {
        super( message );
    }
}
