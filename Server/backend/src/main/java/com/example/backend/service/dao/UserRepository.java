package com.example.backend.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.entity.User;

/**
 * TODO: Describe this class (The first line - until the first dot - will interpret as the brief description).
 */
@Repository
public interface UserRepository extends JpaRepository< User, Long >
{
    @Query( "select u from User u where u.login = :login" )
    User getUserByLogin( @Param( "login" ) String login );

    @Query( "select u from User u where u.userId = :userId" )
    User getUserById( @Param( "userId" ) Long userId );

    @Query( "select u from User u where u.login = :login and u.password = :password" )
    User getUserByLoginAndPassword( @Param( "login" ) String login, @Param( "password" ) String password );
}
