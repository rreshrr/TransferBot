package ru.thomaskohouse.transferbot.telegram.command;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.thomaskohouse.transferbot.service.VkChatService;
import ru.thomaskohouse.transferbot.utils.TransferUtils;

import java.util.Map;

@Component
@AllArgsConstructor
public class ChatsCommand implements Command{

    private final VkChatService vkChatService;
    private final TransferUtils transferUtils;
    @Override
    public SendMessage apply(Update update) {
        Long currentChatId = transferUtils.getVkChatId();
        int chatInd = 0;
        StringBuilder textMessage = new StringBuilder();
        for (Map.Entry<Long, String> chatEntry : vkChatService.getAllChats().entrySet()) {
            chatInd++;
            textMessage.append(chatInd).append(". #").append(chatEntry.getValue());
            if (chatEntry.getKey().equals(currentChatId)) {
                textMessage.append(" \uD83D\uDFE2");
            }
            textMessage.append("\n");
        };
        return new SendMessage(update.getMessage().getChatId().toString(), textMessage.toString());
    }
}
