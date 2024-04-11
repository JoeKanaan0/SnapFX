package com.ar.project.repository;

import com.ar.project.model.Chat;
import com.ar.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findBySenderAndReceiver(User sender, User receiver);

}
