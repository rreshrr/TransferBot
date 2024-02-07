package ru.thomaskohouse.transferbot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thomaskohouse.transferbot.entity.VkChat;

import java.util.Optional;

@Repository
public interface VkChatRepository extends CrudRepository<VkChat, Long> {

    Optional<VkChat> findVkChatByChatId(Long chatId);
    Optional<VkChat> findVkChatByName(String chatName);
}
