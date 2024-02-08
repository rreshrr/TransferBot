package ru.thomaskohouse.transferbot.telegram.command;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.thomaskohouse.transferbot.service.VkChatService;

@RequiredArgsConstructor
@Service
public class AddCommand implements Command{
    private final VkChatService vkChatService;
    private final Logger logger = LoggerFactory.getLogger(AddCommand.class);
    @Override
    public SendMessage apply(Update update) {
        String infoMessageText = null;
        if (update.hasMessage() && update.getMessage().getText() != ""){
            String messageText = update.getMessage().getText();
            String[] args = messageText.split(" ");
            Long chatId = Long.parseLong(args[1]);
            String chatName = args[2];
            vkChatService.addChat(chatId, chatName);
            logger.info("Был добавлен чат: {} {}", chatName, chatId);
            infoMessageText = "Чат " + chatId + " успешно добавлен как " + chatName;
        } else {
            infoMessageText = "Всё плохо с параметрами команды /add";
        }
        return new SendMessage(update.getMessage().getChatId().toString(), infoMessageText);
    }
}
