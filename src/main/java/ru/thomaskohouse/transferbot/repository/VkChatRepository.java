package ru.thomaskohouse.transferbot.repository;

import org.springframework.data.repository.CrudRepository;
import ru.thomaskohouse.transferbot.entity.VkChat;

import java.util.Optional;

public interface VkChatRepository extends CrudRepository<VkChat, Long> {

    Optional<VkChat> findVkChatByChatId(Long chatId);
    Optional<VkChat> findVkChatByName(String chatName);
}
