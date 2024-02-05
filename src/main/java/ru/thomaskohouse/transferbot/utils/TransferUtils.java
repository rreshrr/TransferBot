package ru.thomaskohouse.transferbot.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import ru.thomaskohouse.transferbot.telegram.TelegramBot;

@Service
public class TransferUtils {

    public TransferUtils(@Autowired TelegramBot telegramBot, @Autowired TransferProperties transferProperties) {
        this.telegramBot = telegramBot;
        this.transferProperties = transferProperties;
        this.vkChatId = transferProperties.getVkChatId();
        this.tgChatId = transferProperties.getTgChatId();
    }
    private final TelegramBot telegramBot;
    private final TransferProperties transferProperties;
    private final Long vkChatId;
    private final String tgChatId;

    public void resendFromVkToTg(String resendText){
        telegramBot.sendMessage(
                new SendMessage(tgChatId, resendText)
        );
    }

    public void resendFromTgToVk(String resendText){

    }



}
