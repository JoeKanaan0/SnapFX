package com.ar.project.controller;

import com.ar.project.model.Chat;
import com.ar.project.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/get/{userId1}/{userId2}")
    public ResponseEntity<List<Chat>> getChatsBetweenUsers(@PathVariable("userId1") Long userId1,
                                                           @PathVariable("userId2") Long userId2) {
        List<Chat> chats = chatService.getChatsBetweenUsers(userId1, userId2);
        return new ResponseEntity<>(chats, HttpStatus.OK);
    }

    @PostMapping
    public Chat createChat(@RequestBody Chat chatRequest) {
        return chatService.createChat(chatRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long id) {
        chatService.deleteChat(id);
        return ResponseEntity.noContent().build();
    }

}
