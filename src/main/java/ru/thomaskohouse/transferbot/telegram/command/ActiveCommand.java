package ru.thomaskohouse.transferbot.telegram.command;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.thomaskohouse.transferbot.service.VkChatService;
import ru.thomaskohouse.transferbot.utils.TransferUtils;

@RequiredArgsConstructor
@Service
public class ActiveCommand implements Command{

    private final TransferUtils transferUtils;
    private final Logger logger = LoggerFactory.getLogger(ActiveCommand.class);
    @Override
    public SendMessage apply(Update update) {
        String infoMessageText = null;
        if (update.hasMessage() && !update.getMessage().getText().isEmpty()){
            String messageText = update.getMessage().getText();
            String[] args = messageText.split(" ");
            String chatName = args[1];
            infoMessageText = transferUtils.changeVkDestiantion(chatName);
        } else {
            infoMessageText = "Всё плохо с параметрами команды /active";
        }
        return new SendMessage(update.getMessage().getChatId().toString(), infoMessageText);
    }
}
