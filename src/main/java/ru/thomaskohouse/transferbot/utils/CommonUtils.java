package ru.thomaskohouse.transferbot.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.thomaskohouse.transferbot.service.VkChatService;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@AllArgsConstructor
public class CommonUtils {
    private final NetworkUtils networkUtils;
    private final VkChatService vkChatService;
    private final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    public String getStringDateTimeFromUnixTime(String unixTimestamp) {
        long unixTime = Long.parseLong(unixTimestamp);
        Date date = new Date(unixTime * 1000L);
        SimpleDateFormat jdf = new SimpleDateFormat("dd.MM HH:mm");
        return jdf.format(date);
    }

    public String parseStringMessageForTgFromJsonMessageVk(String jsonString) {
        Gson gs = new Gson();
        JsonObject messageObject = gs.fromJson(jsonString, JsonObject.class);
        logger.info("\nПолучили из вк {}", messageObject);
        JsonArray msg_array = messageObject.getAsJsonArray("updates");
        StringBuilder messageText = new StringBuilder();
        for (JsonElement ob : msg_array) {
            JsonObject message = ob.getAsJsonObject().get("object").getAsJsonObject().get("message").getAsJsonObject();

            String chatName = vkChatService.getChatName(Long.parseLong(message.get("peer_id").toString()));
            messageText.append("[#").append(chatName).append("]\n");

            messageText.append(networkUtils.getUsername(message.get("from_id").getAsString()));

            messageText.append(" (").append(getStringDateTimeFromUnixTime(
                    message.get("date").getAsString())).append(")");

            StringBuilder mainMessageText = new StringBuilder();
            mainMessageText.append(message.get("text").getAsString());
            if (message.has("reply_message")) {
                JsonObject replyMessage = message.get("reply_message").getAsJsonObject();
                String[] replyLines = replyMessage.get("text").getAsString().split("\n");
                for (String line : replyLines) {
                    mainMessageText.append("\t\n>").append(line);
                }
            }
            if (message.has("attachments")) {
                var attachments = message.getAsJsonArray("attachments");
                if (!attachments.isEmpty()){
                    mainMessageText.append("\nВложения:\n");
                    int attachmentInd = 0;
                    for (JsonElement attachment : attachments) {
                        attachmentInd++;
                        String attachmentType = attachment.getAsJsonObject().get("type").getAsString();
                        switch (attachmentType) {
                            case "photo":
                                mainMessageText.append(attachmentInd).append(". Фотка: ");
                                var sizes = attachment.getAsJsonObject().get("photo").getAsJsonObject().getAsJsonArray("sizes");
                                int maxHeight = 0;
                                String imgUrl = "none";
                                for (JsonElement size : sizes) {
                                    int locHeight = size.getAsJsonObject().get("height").getAsInt();
                                    if (locHeight > maxHeight) {
                                        imgUrl = size.getAsJsonObject().get("url").getAsString();
                                    }
                                }
                                mainMessageText.append(imgUrl);
                                break;
                            case "wall":
                                mainMessageText.append(attachmentInd).append(". Какой-то пост");
                                break;
                            default:
                                mainMessageText.append(attachmentInd).append(". Что-то неизвестное с типом ").append(attachmentType);
                        }
                        mainMessageText.append("\n");
                    }
                }
            }
            messageText.append("\n\n").append(mainMessageText);
        }
        return String.valueOf(messageText);
    }

}