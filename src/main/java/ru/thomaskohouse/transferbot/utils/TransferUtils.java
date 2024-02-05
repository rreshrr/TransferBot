package ru.thomaskohouse.transferbot.utils;

import com.google.gson.Gson;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.thomaskohouse.transferbot.vk.VkBotProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransferUtils {

    public TransferUtils(@Autowired TransferProperties transferProperties, @Autowired VkBotProperties vkBotProperties) {
        this.vkChatId = transferProperties.getVkChatId();
        this.vkBotProperties = vkBotProperties;
    }

    private final VkBotProperties vkBotProperties;
    private final Long vkChatId;

    public void sendToVk(String text) throws IOException {
        Gson gs = new Gson();
        String url = "https://api.vk.com/method/messages.send";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> params = new ArrayList<NameValuePair>(5);
        params.add(new BasicNameValuePair("random_id", "0"));
        params.add(new BasicNameValuePair("peer_id", vkChatId.toString()));
        params.add(new BasicNameValuePair("message", text));
        params.add(new BasicNameValuePair("access_token", vkBotProperties.getClientSecret()));
        params.add(new BasicNameValuePair("v", "5.199"));

        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        httpClient.execute(httpPost);
        httpClient.close();
    }

}
