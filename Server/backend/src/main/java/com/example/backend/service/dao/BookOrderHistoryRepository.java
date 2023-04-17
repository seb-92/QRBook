package com.example.backend.service.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.entity.BookOrderHistory;

/**
 * TODO: Describe this class (The first line - until the first dot - will interpret as the brief description).
 */
@Repository
public interface BookOrderHistoryRepository extends JpaRepository< BookOrderHistory, Long >
{
    @Query( "select h from BookOrderHistory h where h.user.userId = :userId" )
    List< BookOrderHistory > getUserHistory( @Param( "userId" ) Long userId );

    @Query( "select h from BookOrderHistory h where h.user.userId = :userId and h.orderType = :orderType" )
    List< BookOrderHistory > getUserHistory( @Param( "userId" ) Long userId,
        @Param( "orderType" ) String orderType );
}
