package ru.thomaskohouse.transferbot.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class TelegramBotInit {
    private final TelegramBot tgBot;
    private final Logger logger = LoggerFactory.getLogger(TelegramBotInit.class);

    public TelegramBotInit(@Autowired TelegramBot tgBot) {
        this.tgBot = tgBot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi tgBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            tgBotsApi.registerBot(tgBot);
            logger.info("TgBot - Register success");
        }
        catch (TelegramApiException e){
           logger.error("Register failed");
        }
    }

}