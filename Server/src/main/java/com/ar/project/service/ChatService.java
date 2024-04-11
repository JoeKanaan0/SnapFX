package com.ar.project.service;

import com.ar.project.model.Chat;
import com.ar.project.model.User;
import com.ar.project.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserService userService;

    public List<Chat> getChatsBetweenUsers(Long userId1, Long userId2) {

        // Use the ids to get both users
        User user1 = userService.getUserById(userId1);
        User user2 = userService.getUserById(userId2);

        // Grab all chats where the sender is user1 and the receiver is user2
        List<Chat> chatsFromUser1ToUser2 = chatRepository.findBySenderAndReceiver(user1, user2);

        // Grab all chats where the sender is user2 and the receiver is user1
        List<Chat> chatsFromUser2ToUser1 = chatRepository.findBySenderAndReceiver(user2, user1);

        // Create a new List that will contain all the chats
        List<Chat> allChats = new ArrayList<>();
        allChats.addAll(chatsFromUser1ToUser2);
        allChats.addAll(chatsFromUser2ToUser1);

        // Sort the merged list by Chat's id in ascending order
        allChats.sort(Comparator.comparing(Chat::getId));

        return allChats;
    }

    public Chat createChat(Chat chatRequest) {
        User sender = userService.getUserById(chatRequest.getSender().getId());
        User receiver = userService.getUserById(chatRequest.getReceiver().getId());

        if (sender == null || receiver == null) {
            // Handle the case when either sender or receiver is not found
            return null;
        }

        Chat chat = new Chat();
        chat.setSender(sender);
        chat.setReceiver(receiver);
        chat.setText(chatRequest.getText());

        return chatRepository.save(chat);
    }

    public void deleteChat(Long id) {
        chatRepository.deleteById(id);
    }
}
