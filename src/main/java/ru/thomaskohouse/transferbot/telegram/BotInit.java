package ru.thomaskohouse.transferbot.telegram;

import org.springframework.stereotype.Component;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInit {
    private final TelegramBot tgBot;

    public BotInit(@Autowired TelegramBot tgBot) {
        this.tgBot = tgBot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi tgBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            tgBotsApi.registerBot((LongPollingBot) tgBot);
            System.out.println("Register success");
        }
        catch (TelegramApiException e){

            System.out.println("Register failed");
        }
    }

}