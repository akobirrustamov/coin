package com.example.backend.Repository;

import com.example.backend.Entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramUserRepo extends JpaRepository<TelegramUser,Long> {
}
