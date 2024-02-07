package ru.thomaskohouse.transferbot.vk;

import com.google.gson.*;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.responses.GetLongPollServerResponse;
import com.vk.api.sdk.queries.groups.GroupsGetLongPollServerQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.thomaskohouse.transferbot.CommonUtils;
import ru.thomaskohouse.transferbot.NetworkUtils;
import ru.thomaskohouse.transferbot.service.VkChatService;
import ru.thomaskohouse.transferbot.telegram.TelegramBot;

import java.io.IOException;


@Component
public class VkBot {
    private final VkBotProperties vkBotProperties;
    private final CommonUtils commonUtils;

    private final NetworkUtils networkUtils;
    public VkBot(@Autowired VkBotProperties vkBotProperties, @Autowired TelegramBot telegramBot,
                 @Autowired VkChatService vkChatService, @Autowired NetworkUtils networkUtils,
                 @Autowired CommonUtils commonUtils) throws  ClientException, ApiException {
        this.vkBotProperties = vkBotProperties;
        this.networkUtils = networkUtils;
        this.commonUtils = commonUtils;

        Gson gs = new Gson();
        TransportClient transportClient = new HttpTransportClient();

        VkApiClient vk = new VkApiClient(transportClient);
        GroupActor actor = new GroupActor(vkBotProperties.getGroupId(), vkBotProperties.getClientSecret());
        GroupsGetLongPollServerQuery serverQuery = vk.groups().getLongPollServer(actor, vkBotProperties.getGroupId());
        GetLongPollServerResponse response = serverQuery.execute();
        String key = response.getKey();
        String serverUrl = response.getServer().toString();

        Thread thread = new Thread(
                () -> {
                    int ts = 250;
                    while (true) {
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            System.err.println("Ошибка при паузе потока, во время слушания сообщения из ВК");
                            throw new RuntimeException(e);
                        }
                        String url = serverUrl + "?act=a_check&key=" + key + "&ts=" + ts + "&wait=25";
                        String json = networkUtils.httpGet(url);
                        String messageText = commonUtils.parseStringMessageForTgFromJsonMessageVk(json);
                        ts = gs.fromJson(json, JsonObject.class).get("ts").getAsInt();
                        telegramBot.sendTextMessage(messageText);
                    }
                }
        );
        thread.start();
    }





}
