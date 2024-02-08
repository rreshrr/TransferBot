package ru.thomaskohouse.transferbot.utils;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.thomaskohouse.transferbot.service.VkChatService;
import ru.thomaskohouse.transferbot.vk.VkBotProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Service
public class TransferUtils {

    private final VkChatService vkChatService;
    private final Logger logger = LoggerFactory.getLogger(TransferUtils.class);
    private Long vkChatId;

    public TransferUtils(@Autowired VkChatService vkChatService, @Autowired TransferProperties transferProperties) {
        this.vkChatService = vkChatService;
        this.vkChatId = transferProperties.getVkChatId();
    }


    public String changeVkDestiantion(String chatName) {
        String infoMessageText = null;
        Long chatId = vkChatService.getChatId(chatName);
        if (chatId != null){
            setVkChatId(chatId);
            logger.info("Чат {} ({}) был выбран активным", chatName, chatId);
            infoMessageText = "Чат " + chatName + " выбран активным! Любое отправленное сообщение улетит прямиком туда";
        } else {
            infoMessageText = "Нет такого чата как " + chatName;
        }
        return infoMessageText;
    }
}
