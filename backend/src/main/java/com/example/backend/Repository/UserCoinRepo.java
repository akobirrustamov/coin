package com.example.backend.Repository;

import com.example.backend.Entity.UserCoin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCoinRepo extends JpaRepository<UserCoin,Integer> {

}
