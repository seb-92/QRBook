package com.example.backend.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.example.backend.dto.UserDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Table( name = "users" )
@Entity
public class User
{
    @Id
    @Column( name = "id" )
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long userId;
    @Column( name = "first_name", nullable = false )
    private String firstName;
    @Column( name = "last_name", nullable = false )
    private String lastName;

    @Column( name = "address", nullable = false )
    private String address;
    @Column( name = "login", nullable = false, unique = true )
    private String login;

    @Column( name = "password", nullable = false )
    private String password;

    public User( UserDto aUserDto )
    {
        this.firstName = aUserDto.getFirstName();
        this.lastName = aUserDto.getLastName();
        this.login = aUserDto.getLogin();
        this.password = aUserDto.getPassword();
        this.address = aUserDto.getAddress();
    }

    public static UserDto convertToDto( User aUser )
    {
        return new UserDto( aUser.getUserId(), aUser.getFirstName(), aUser.getLastName(), aUser.getAddress(),
            aUser.getLogin(), aUser.getPassword() );
    }
}
