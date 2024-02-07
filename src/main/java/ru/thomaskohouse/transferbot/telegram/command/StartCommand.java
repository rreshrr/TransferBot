package ru.thomaskohouse.transferbot.telegram.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.thomaskohouse.transferbot.telegram.command.Command;

@RequiredArgsConstructor
@Service
public class StartCommand implements Command {
    @Override
    public SendMessage apply(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String text = "ON START COMMAND: " + update.getMessage().getText();
        return new SendMessage(chatId, text);
    }
}
