package com.example.backend.exceptions;

/**
 * TODO: Describe this class (The first line - until the first dot - will interpret as the brief description).
 */
public class UnavailableBookException extends RuntimeException
{
    public UnavailableBookException( String message )
    {
        super( message );
    }
}
