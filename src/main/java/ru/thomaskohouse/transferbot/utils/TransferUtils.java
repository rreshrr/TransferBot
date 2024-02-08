package ru.thomaskohouse.transferbot.utils;

import com.google.gson.Gson;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
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

@Setter
@Getter
@Service
public class TransferUtils {

    public TransferUtils(@Autowired TransferProperties transferProperties) {
        this.vkChatId = transferProperties.getVkChatId();
    }

    private Long vkChatId;

}
