package com.example.backend.Repository;

import com.example.backend.Entity.UserCoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface UserCoinRepo extends JpaRepository<UserCoin,Integer> {


//    @Query(value = "select * from user_coin where  ")
//    UserCoin getUserCoinByUserId(UUID id);
}
