package ru.thomaskohouse.transferbot.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.thomaskohouse.transferbot.MessageDirection;
import ru.thomaskohouse.transferbot.entity.UniqueMessage;
import ru.thomaskohouse.transferbot.repository.UniqueMessageRepository;

@Service
@AllArgsConstructor
public class UniqueMessageService {

    private final UniqueMessageRepository uniqueMessageRepository;
    public void addMessage(String vkId, String tgId, MessageDirection messageDirection){
        UniqueMessage newMessage = new UniqueMessage();
        newMessage.setVkId(vkId);
        newMessage.setTelegramId(tgId);
        newMessage.setDirection(messageDirection);
        uniqueMessageRepository.save(newMessage);
    }

    public String getVkId(String telegramId){
        return  uniqueMessageRepository.findUniqueMessageByTelegramId(telegramId).getVkId();
    }
}
