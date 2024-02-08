package ru.thomaskohouse.transferbot.telegram;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.thomaskohouse.transferbot.service.VkChatService;
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
    private final TransferUtils transferUtils;
    private final VkChatService vkChatService;
    Logger logger = LoggerFactory.getLogger(TelegramBot.class);
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
            logger.info("Отправили в телеграм {}", sendMessage);
        } catch (TelegramApiException e) {
            logger.error("Ошибка при отправке сообщения в Telegram" + e.getMessage());
        }
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            logger.info("Получили из Telegram {}", update.getMessage());
            if (!update.getMessage().getFrom().getId().equals(telegramBotProperties.getOwnerId())){
                sendMessage(new SendMessage(update.getMessage().getChatId().toString(), "прости, дорогой, я работаю только для @rreshrr <3\nНапиши ему и он тебе поможет!"));
            } else if (update.getMessage().getText().startsWith("/")) {
                sendMessage(commandsHandler.handleCommands(update));
            } else if (update.getMessage().getText().startsWith("#")) {
                sendTextMessage(transferUtils.changeVkDestiantion(update.getMessage().getText().replace("#", "")));
            }
            else {
                try {
                    StringBuilder messageText = new StringBuilder(update.getMessage().getText());
                    if (update.getMessage().isReply()){
                       messageText.append("\n");
                       String[] replyLines = update.getMessage().getReplyToMessage().getText().split("\n");
                        for (String line: replyLines) {
                            messageText.append("\t\n>").append(line);
                        }
                    }
                    networkUtils.sendToVk(messageText.toString());

                } catch (IOException e) {
                    logger.error("ошибка при отправке смс в тг");
                    throw new RuntimeException(e);
                }
            }
        } else if (update.hasCallbackQuery()) {
            logger.error("Пока не орабатываем каллбеки...");
        }
    }

    public void sendTextMessage(String text){
        sendMessage(new SendMessage(transferProperties.getTgChatId(), text));
    }
}
