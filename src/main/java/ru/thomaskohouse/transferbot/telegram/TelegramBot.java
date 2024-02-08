package ru.thomaskohouse.transferbot.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.thomaskohouse.transferbot.utils.NetworkUtils;
import ru.thomaskohouse.transferbot.utils.TransferProperties;
import ru.thomaskohouse.transferbot.utils.TransferUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramBotProperties telegramBotProperties;
    private final CommandsHandler commandsHandler;
    private final TransferProperties transferProperties;
    private final NetworkUtils networkUtils;
    @Override
    public String getBotUsername() {
        return telegramBotProperties.getName();
    }
    @Override
    public String getBotToken() {
        return telegramBotProperties.getToken();
    }
    private void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка при отправке сообщения в Telegram" + e.getMessage());
        }
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (!update.getMessage().getFrom().getId().equals(telegramBotProperties.getOwnerId())){
                sendMessage(new SendMessage(update.getMessage().getChatId().toString(), "прости, дорогой, я работаю только для @rreshrr <3\nНапиши ему и он тебе поможет!"));
            } else if (update.getMessage().getText().startsWith("/")) {
                sendMessage(commandsHandler.handleCommands(update));
            } else {

                try {
                    String messageText = update.getMessage().getText();
                    if (update.getMessage().isReply()){
                       messageText += "\n";
                       String[] replyLines = update.getMessage().getReplyToMessage().getText().split("\n");
                        for (String line: replyLines) {
                            messageText += "\t\n>" + line;
                        }
                    }
                    networkUtils.sendToVk(messageText);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (update.hasCallbackQuery()) {
            System.err.println("Пока не орабатываем каллбеки...");
        }
    }

    public void sendTextMessage(String text){
        sendMessage(new SendMessage(transferProperties.getTgChatId(), text));
    }
}
