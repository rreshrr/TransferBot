package ru.thomaskohouse.transferbot.telegram;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.thomaskohouse.transferbot.telegram.command.*;

import java.util.Map;

@Component
public class CommandsHandler {

    private final Map<String, Command> commands;

    public CommandsHandler(@Autowired StartCommand startCommand, @Autowired AddCommand addCommand,
                           @Autowired ActiveCommand activeCommand, @Autowired ChatsCommand chatsCommand) {
        this.commands = Map.of(
                "/start", startCommand,
                "/add", addCommand,
                "/active", activeCommand,
                "/chats", chatsCommand
        );
    }

    public SendMessage handleCommands(Update update) {
        String messageText = update.getMessage().getText();
        String command = messageText.split(" ")[0];
        long chatId = update.getMessage().getChatId();
        Command commandHandler = commands.get(command);
        if (commandHandler != null) {
            return commandHandler.apply(update);
        } else {
            return new SendMessage(String.valueOf(chatId), "Эта команда неизвестна в этих краях");
        }
    }

}
