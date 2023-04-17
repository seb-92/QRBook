package com.example.backend.service;

import static com.example.backend.utlis.ConstantUtils.AVAILABLE_BOOK_MESSAGE;
import static com.example.backend.utlis.ConstantUtils.GIVEN_LOGIN_EXISTS_MESSAGE;
import static com.example.backend.utlis.ConstantUtils.GIVEN_USER_NOT_EXISTS_MESSAGE;
import static com.example.backend.utlis.ConstantUtils.NOT_EXISTED_BOOK_MESSAGE;
import static com.example.backend.utlis.ConstantUtils.NOT_YOUR_BOOK_MESSAGE;
import static com.example.backend.utlis.ConstantUtils.ORDER_REJECTED_MESSAGE;
import static com.example.backend.utlis.ConstantUtils.REQUIRED_FIELDS_MESSAGE;
import static com.example.backend.utlis.ConstantUtils.RETURN_REJECTED_MESSAGE;
import static com.example.backend.utlis.ConstantUtils.UNAVAILABLE_BOOK_MESSAGE;
import static com.example.backend.utlis.ConstantUtils.WRONG_DATA_MESSAGE;
import static com.example.backend.utlis.ConstantUtils.WRONG_TYPE_EXCEPTION;
import static com.example.backend.utlis.TypeUtils.ORDER_TYPE;
import static com.example.backend.utlis.TypeUtils.RETURN_TYPE;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.backend.dto.BookDto;
import com.example.backend.dto.BookOrderHistoryDto;
import com.example.backend.dto.OrderDto;
import com.example.backend.dto.TcpInfoDto;
import com.example.backend.dto.UserDto;
import com.example.backend.entity.Book;
import com.example.backend.entity.BookOrderHistory;
import com.example.backend.entity.User;
import com.example.backend.exceptions.AvailableBookException;
import com.example.backend.exceptions.OrderRejectedException;
import com.example.backend.exceptions.RequiredFieldException;
import com.example.backend.exceptions.UnavailableBookException;
import com.example.backend.exceptions.ValidationDataException;
import com.example.backend.service.dao.BookOrderHistoryRepository;
import com.example.backend.service.dao.BookRepository;
import com.example.backend.service.dao.UserRepository;
import com.example.backend.tcpClient.TcpClient;
import com.example.backend.utlis.TypeUtils;

@Service
public class DataService
{
    private final Logger logger = LoggerFactory.getLogger(DataService.class);
    private final BookRepository bookRepository;
    private final BookOrderHistoryRepository bookOrderHistoryRepository;
    private final UserRepository userRepository;

    public DataService(BookOrderHistoryRepository bookOrderHistoryRepository, BookRepository bookRepository,
                       UserRepository aUserRepository)
    {
        this.bookOrderHistoryRepository = bookOrderHistoryRepository;
        this.bookRepository = bookRepository;
        userRepository = aUserRepository;
    }

    public void saveNewBook( BookDto aBook )
    {
        var book = new Book( aBook );
        logger.info("Dodano nową książkę!");
        bookRepository.save( book );
    }

    public BookDto getBookById( Long aBookId )
    {
        logger.info("Pobrano książkę o id : ".concat(aBookId.toString()));
        return getExternalBookFromInternal( bookRepository.getBookById( aBookId ) );
    }

    public void orderBook( OrderDto aOrderDto )
    {

        var book = bookRepository.getBookById( aOrderDto.getBookIdToOrder() );
        var user = userRepository.getUserById( aOrderDto.getUserId() );
        if( book == null ){
            logger.info("Dana książka o id : ".concat(aOrderDto.getBookIdToOrder().toString()).concat(" nie istnieje !"));
            throw new UnavailableBookException( NOT_EXISTED_BOOK_MESSAGE );
        }
        if( !book.getAvailable() ){
            logger.info("Dana książka o id : ".concat(aOrderDto.getBookIdToOrder().toString()).concat(" nie jest dostępna !"));
            throw new UnavailableBookException( UNAVAILABLE_BOOK_MESSAGE );
        }
        BookOrderHistory newOrder = new BookOrderHistory( Instant.now(), user, ORDER_TYPE, book );
        var tcpInfoDto = new TcpInfoDto(user.getFirstName(), user.getLastName(), user.getAddress(),
                newOrder.getOrderType(), book.getYearOfPublish(), book.getTitle(), book.getAuthor(),
                book.getBookId());
        TcpClient.sendActionOrderToLibrarian(tcpInfoDto);
        if(TcpClient.readResponseFromLibrarian()){
            logger.info("Wypożyczenie zostało zatwierdzone!");
            bookOrderHistoryRepository.save( newOrder );
            book.setCurrentlyOrderedBy( user );
            book.setAvailable( false );
            bookRepository.save( book );   
        } else {
            logger.info("Wypożyczenie nie zostało zatwierdzone!");
            throw new OrderRejectedException(ORDER_REJECTED_MESSAGE);
        }
    }

    public void returnBook( OrderDto aOrderDto )
    {
        var book = bookRepository.getBookById( aOrderDto.getBookIdToOrder() );
        var user = userRepository.getUserById( aOrderDto.getUserId() );
        if( user == null )
        {
            logger.info("Użytkownik o id : ".concat(aOrderDto.getUserId().toString()).concat(" nie istnieje !"));
            throw new ValidationDataException( GIVEN_USER_NOT_EXISTS_MESSAGE );
        }
        if( book == null )
        {
            logger.info("Dana książka o id : ".concat(aOrderDto.getBookIdToOrder().toString()).concat(" nie istnieje !"));
            throw new UnavailableBookException( UNAVAILABLE_BOOK_MESSAGE );
        }
        if( book.getAvailable() )
        {
            logger.info("Dana książka o id : ".concat(aOrderDto.getBookIdToOrder().toString()).concat(" nie jest wypożyczona !"));
            throw new AvailableBookException( AVAILABLE_BOOK_MESSAGE );
        }

        if( !book.getCurrentlyOrderedBy()
            .equals( user ) )
        {
            logger.info(NOT_YOUR_BOOK_MESSAGE);
            throw new AvailableBookException( NOT_YOUR_BOOK_MESSAGE );

        }
        BookOrderHistory newOrder = new BookOrderHistory( Instant.now(), user, RETURN_TYPE, book );
        var tcpInfoDto = new TcpInfoDto(user.getFirstName(), user.getLastName(), user.getAddress(),
                newOrder.getOrderType(), book.getYearOfPublish(), book.getTitle(), book.getAuthor(),
                book.getBookId());
        TcpClient.sendActionOrderToLibrarian(tcpInfoDto);
        if(TcpClient.readResponseFromLibrarian()){
            bookOrderHistoryRepository.save( newOrder );
            book.setAvailable( true );
            book.setCurrentlyOrderedBy( null );
            bookRepository.save( book );
            logger.info("Zwrot został zatwierdzony!");
        } else {
            logger.info("Zwrot nie został zatwierdzony!");
            throw new OrderRejectedException(RETURN_REJECTED_MESSAGE);
        }
    }

    public List< BookDto > getAvailableBooks()
    {
        var books = bookRepository.getAvailableBookTitles();
        return getExternalBookFromInternal( books );
    }

    public List< BookDto > getUnAvailableBooks()
    {
        var books = bookRepository.getUnavailableBookTitles();
        return getExternalBookFromInternal( books );
    }

    public List< BookDto > getCurrentlyOrderedBooksByUser( Long userId )
    {
        var books = bookRepository.getBooksCurrentlyOrderedByUser( userId );
        return getExternalBookFromInternal( books );
    }

    public List< BookDto > getBooksByAuthor( String author )
    {
        var books = bookRepository.getBooksByAuthor( author );
        return getExternalBookFromInternal( books );
    }

    public List< BookDto > getBooksByTitle( String title )
    {
        var books = bookRepository.getBooksByTitle( title );
        return getExternalBookFromInternal( books );
    }

    public List< BookDto > getBooksByYearOfPublish( Integer yearOfPublish )
    {
        var books = bookRepository.getBooksByYearOfPublish( yearOfPublish );
        return getExternalBookFromInternal( books );
    }

    public List< BookDto > getExternalBookFromInternal( List< Book > aBookList )
    {
        return aBookList.stream()
            .map( Book::convertToDto )
            .collect( Collectors.toList() );
    }

    public BookDto getExternalBookFromInternal( Book aBook )
    {
        return Book.convertToDto( aBook );
    }

    public List< UserDto > getExternalUserFromInternal( List< User > aUserList )
    {
        return aUserList.stream()
            .map( User::convertToDto )
            .collect( Collectors.toList() );
    }

    public UserDto getExternalUserFromInternal( User aUser )
    {
        return User.convertToDto( aUser );
    }

    public void registerNewUser( UserDto aUserDto )
    {
        try
        {
            var user = userRepository.getUserByLogin( aUserDto.getLogin() );
            if( user != null )
            {
                logger.info(GIVEN_LOGIN_EXISTS_MESSAGE);
                throw new ValidationDataException( GIVEN_LOGIN_EXISTS_MESSAGE );
            }
            user = new User( aUserDto );
            userRepository.save( user );
            logger.info("Użytkownik został zarejestrowany poprawnie");
        }
        catch( DataIntegrityViolationException aE )
        {
            logger.info(REQUIRED_FIELDS_MESSAGE);
            throw new RequiredFieldException( REQUIRED_FIELDS_MESSAGE );
        }
    }

    public UserDto loginIntoSystem( String aLogin, String aPassword )
    {
        var user = userRepository.getUserByLoginAndPassword( aLogin, aPassword );
        if( user == null )
        {
            logger.info(WRONG_DATA_MESSAGE);
            throw new ValidationDataException( WRONG_DATA_MESSAGE );
        }
        return getExternalUserFromInternal( user );
    }

    public List< BookOrderHistoryDto > getHistoryOfUser( Long userId, String type )
    {
        List< BookOrderHistory > historyOfUser = null;
        if( type == null )
        {
            historyOfUser = bookOrderHistoryRepository.getUserHistory( userId );
        }
        else
        {
            if( TypeUtils.listOfTypes.contains( type ) )
            {
                historyOfUser = bookOrderHistoryRepository.getUserHistory( userId, type );
            }
            else
            {
                throw new ValidationDataException( WRONG_TYPE_EXCEPTION );
            }
        }
        return getExternalBookHistoryFromInternal( historyOfUser );
    }

    public List< BookOrderHistoryDto > getExternalBookHistoryFromInternal(
        List< BookOrderHistory > aBookOrderHistories )
    {
        return aBookOrderHistories.stream()
            .map( BookOrderHistory::convertToDto )
            .collect( Collectors.toList() );
    }

    public BookOrderHistoryDto getExternalBookHistoryFromInternal( BookOrderHistory aBookOrderHistories )
    {
        return BookOrderHistory.convertToDto( aBookOrderHistories );
    }
}
