package com.example.backend.exceptions;

/**
 * TODO: Describe this class (The first line - until the first dot - will interpret as the brief description).
 */
public class RequiredFieldException extends RuntimeException
{
    public RequiredFieldException( String message )
    {
        super( message );
    }
}
