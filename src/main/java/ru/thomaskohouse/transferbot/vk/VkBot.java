package ru.thomaskohouse.transferbot.vk;

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
import ru.thomaskohouse.transferbot.utils.CommonUtils;
import ru.thomaskohouse.transferbot.utils.NetworkUtils;
import ru.thomaskohouse.transferbot.telegram.TelegramBot;


@Component
public class VkBot {

    public VkBot(@Autowired VkBotProperties vkBotProperties, @Autowired TelegramBot telegramBot,
                 @Autowired NetworkUtils networkUtils, @Autowired CommonUtils commonUtils) {

        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient);
        GroupActor actor = new GroupActor(vkBotProperties.getGroupId(), vkBotProperties.getClientSecret());
        GroupsGetLongPollServerQuery serverQuery = vk.groups().getLongPollServer(actor, vkBotProperties.getGroupId());

        GetLongPollServerResponse response = null;
        try {
            response = serverQuery.execute();
        } catch (ApiException | ClientException e) {
            System.err.println("Ошибка! При выполнении запроса от лица группы к серверу...");
            throw new RuntimeException(e);
        }

        String key = response.getKey();
        Integer ts = Integer.parseInt(response.getTs());

        String serverUrl = response.getServer().toString();
        System.out.println("VkBot - Init success");

        Thread thread = new Thread(new VkBotMessageListener(serverUrl, key, ts, networkUtils, commonUtils, telegramBot));
        thread.start();
    }

}
