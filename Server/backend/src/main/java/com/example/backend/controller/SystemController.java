package com.example.backend.controller;

import static com.example.backend.utlis.ConstantUtils.BOOK_ADDED_MESSAGE;
import static com.example.backend.utlis.ConstantUtils.BOOK_ORDER_MESSAGE;
import static com.example.backend.utlis.ConstantUtils.BOOK_RETURN_MESSAGE;
import static com.example.backend.utlis.ConstantUtils.REGISTER_SUCCESS_MESSAGE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.BookDto;
import com.example.backend.dto.MessageResponse;
import com.example.backend.dto.OrderDto;
import com.example.backend.dto.UserDto;
import com.example.backend.service.DataService;

import io.swagger.annotations.ApiOperation;

import javax.swing.*;
import java.awt.*;

@RequestMapping( "/system" )
@RestController( "SystemController" )
@CrossOrigin( origins = "*" )
public class SystemController
{
    private final DataService dataService;

    @Autowired
    public SystemController( DataService aDataService )
    {
        dataService = aDataService;
    }

    @PostMapping( "/book" )
    @ApiOperation( value = "Save new book (mainly for input some data before liquibase script was added, in future we can use it to create new books as a library guy)" )
    public ResponseEntity< Object > saveNewBook( @RequestBody BookDto aBook )
    {
        dataService.saveNewBook( aBook );
        return new ResponseEntity<>( new MessageResponse( BOOK_ADDED_MESSAGE ), HttpStatus.OK );
    }

    @GetMapping( "/book/id/{bookId}" )
    @ApiOperation( value = "Get book by id" )
    public ResponseEntity< Object > getBookById( @PathVariable Long bookId )
    {
        var book = dataService.getBookById( bookId );
        return new ResponseEntity<>( book, HttpStatus.OK );
    }

    @PostMapping( "/book/order" )
    @ApiOperation( value = "Order book (need to pass user and book ids)." )
    public ResponseEntity< Object > orderBook( @RequestBody OrderDto aOrderDto )
    {
        dataService.orderBook( aOrderDto );
        return new ResponseEntity<>( new MessageResponse( BOOK_ORDER_MESSAGE ), HttpStatus.OK );
    }

    @PostMapping( "/book/return" )
    @ApiOperation( value = "Return book (need to pass user and book ids)." )
    public ResponseEntity< Object > returnBook( @RequestBody OrderDto aOrderDto )
    {
        dataService.returnBook( aOrderDto );
        return new ResponseEntity<>( new MessageResponse( BOOK_RETURN_MESSAGE ), HttpStatus.OK );
    }

    @GetMapping( "/book/current/{userId}" )
    @ApiOperation( value = "Return currently ordered books by user." )
    public ResponseEntity< Object > getCurrentlyOrderedBooksByUser( @PathVariable Long userId )
    {
        var books = dataService.getCurrentlyOrderedBooksByUser( userId );
        return new ResponseEntity<>( books, HttpStatus.OK );
    }

    @GetMapping( "/book/available" )
    @ApiOperation( value = "Returns all currently available books in library." )
    public ResponseEntity< Object > getAvailableBooks()
    {
        var books = dataService.getAvailableBooks();
        return new ResponseEntity<>( books, HttpStatus.OK );
    }

    @GetMapping( "/book/unavailable" )
    @ApiOperation( value = "Returns all currently unavailable books in library." )
    public ResponseEntity< Object > getUnAvailableBooks()
    {
        var books = dataService.getUnAvailableBooks();
        return new ResponseEntity<>( books, HttpStatus.OK );
    }

    @GetMapping( "/book/title/{title}" )
    @ApiOperation( value = "Returns all books which are similar in title (if we pass 'Zem' we can get books like 'zemsta' or 'zem wojna' size of letters doesn't matter)." )
    public ResponseEntity< Object > getBookByTitle( @PathVariable String title )
    {
        var book = dataService.getBooksByTitle( title );
        return new ResponseEntity<>( book, HttpStatus.OK );
    }

    @GetMapping( "/book/author/{author}" )
    @ApiOperation( value = "Same as title but if it comes to author." )
    public ResponseEntity< Object > getBookByAuthor( @PathVariable String author )
    {
        var book = dataService.getBooksByAuthor( author );
        return new ResponseEntity<>( book, HttpStatus.OK );
    }

    @GetMapping( "/book/yearOfPublish/{yearOfPublish}" )
    @ApiOperation( value = "Returns all books by the given year." )
    public ResponseEntity< Object > getBookByYear( @PathVariable Integer yearOfPublish )
    {
        var book = dataService.getBooksByYearOfPublish( yearOfPublish );
        return new ResponseEntity<>( book, HttpStatus.OK );
    }

    @PostMapping( "/user/register" )
    @ApiOperation( value = "Register new user (need to pass all required fields)." )
    public ResponseEntity< Object > registerNewUser( @RequestBody UserDto aUserDto )
    {
        dataService.registerNewUser( aUserDto );
        return new ResponseEntity<>( new MessageResponse( REGISTER_SUCCESS_MESSAGE ), HttpStatus.OK );
    }

    @GetMapping( "/user/{login}/{password}" )
    @ApiOperation( value = "Returns a user object which contains all private data like user id, address etc. if we pass correct login and password." )
    public ResponseEntity< Object > loginToSystem( @PathVariable String login, @PathVariable String password )
    {
        var user = dataService.loginIntoSystem( login, password );
        return new ResponseEntity<>( user, HttpStatus.OK );
    }

    @GetMapping( value =
    { "/userHistory/{id}", "/userHistory/{id}/{type}" } )
    @ApiOperation( value = "Get user history by type (order, return), we can get both of them if we pass null." )
    public ResponseEntity< Object > getUserHistory( @PathVariable Long id,
        @PathVariable( required = false ) String type )
    {
        var user = dataService.getHistoryOfUser( id, type );
        return new ResponseEntity<>( user, HttpStatus.OK );
    }
}
