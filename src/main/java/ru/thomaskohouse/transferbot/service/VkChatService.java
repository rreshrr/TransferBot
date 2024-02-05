package ru.thomaskohouse.transferbot.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.thomaskohouse.transferbot.entity.VkChat;
import ru.thomaskohouse.transferbot.repository.VkChatRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class VkChatService {
    private final VkChatRepository vkChatRepository;

    public void addChat(Long chatId, String chatName){
        VkChat vkChat = new VkChat();
        vkChat.setChatId(chatId);
        vkChat.setName(chatName);
        vkChatRepository.save(vkChat);
    }

    public String getChatName(Long chatId){
        Optional<VkChat> vkChat = vkChatRepository.findVkChatByChatId(chatId);
        return vkChat.isPresent() ? vkChat.get().getName() : chatId.toString();
    }

    public Long getChatId(String chatName){
        Optional<VkChat> vkChat = vkChatRepository.findVkChatByName(chatName);
        return vkChat.isPresent() ? vkChat.get().getChatId() : null;
    }

}
