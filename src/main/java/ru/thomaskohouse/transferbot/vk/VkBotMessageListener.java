package ru.thomaskohouse.transferbot.vk;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.thomaskohouse.transferbot.utils.CommonUtils;
import ru.thomaskohouse.transferbot.utils.NetworkUtils;
import ru.thomaskohouse.transferbot.telegram.TelegramBot;


@AllArgsConstructor
public class VkBotMessageListener implements Runnable{
    private final String serverUrl;
    private final String key;
    private final Integer init_ts;
    private final NetworkUtils networkUtils;
    private final CommonUtils commonUtils;
    private final TelegramBot telegramBot;
    private final Logger logger = LoggerFactory.getLogger(VkBotMessageListener.class);
    @Override
    public void run() {
        Integer ts = init_ts;
        Gson gs = new Gson();
        while (true) {
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                logger.error("Ошибка при паузе потока, во время слушания сообщения из ВК");
                throw new RuntimeException(e);
            }
            String url = serverUrl + "?act=a_check&key=" + key + "&ts=" + ts + "&wait=25";
            String json = networkUtils.httpGet(url);
            ts = gs.fromJson(json, JsonObject.class).get("ts").getAsInt();
            String messageText = commonUtils.parseStringMessageForTgFromJsonMessageVk(json);
            String vkMessageId = commonUtils.parseVkMessageIdForTgFromJsonMessageVk(json);
            if (!messageText.isEmpty()) {
                telegramBot.sendMessage(messageText, vkMessageId);
            }
        }
    }
}
