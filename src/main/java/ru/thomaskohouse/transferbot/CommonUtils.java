package ru.thomaskohouse.transferbot;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.thomaskohouse.transferbot.service.VkChatService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@AllArgsConstructor
public class CommonUtils {
    private final NetworkUtils networkUtils;
    private final VkChatService vkChatService;

    public String removeQuotes(String string) {
        return string.replaceAll("\"", "");
    }

    public String getStringDateTimeFromUnixTime(String unixTimestamp) {
        long unixTime = Long.parseLong(unixTimestamp);
        Date date = new Date(unixTime * 1000L);
        SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return jdf.format(date);
    }

    public String parseStringMessageForTgFromJsonMessageVk(String jsonString) {
        Gson gs = new Gson();
        JsonObject messageObject = gs.fromJson(jsonString, JsonObject.class);
        int ts = messageObject.get("ts").getAsInt();
        JsonArray msg_array = messageObject.getAsJsonArray("updates");
        StringBuilder messageText = new StringBuilder();
        for (JsonElement ob : msg_array) {
            JsonObject message = ob.getAsJsonObject().get("object").getAsJsonObject().get("message").getAsJsonObject();
            messageText.append(removeQuotes(networkUtils.getUsername(removeQuotes(message.get("from_id").toString()))));
            String chatName = vkChatService.getChatName(Long.parseLong(message.get("peer_id").toString()));
            messageText.append("\nиз чатика #").append(chatName);
            messageText.append("\n").append(getStringDateTimeFromUnixTime(
                    removeQuotes(message.get("date").toString())));
            StringBuilder mainMessageText = new StringBuilder();
            mainMessageText.append(removeQuotes(message.get("text").toString()));
            if (message.has("reply_message")) {
                JsonObject replyMessage = message.get("reply_message").getAsJsonObject();
                String[] replyLines = removeQuotes(replyMessage.get("text").toString()).split("\n");
                for (String line : replyLines) {
                    mainMessageText.append("\t\n>").append(line);
                }
            }
            if (message.has("attachments")) {
                var attachments = message.getAsJsonArray("attachments");
                if (!attachments.isEmpty())
                    mainMessageText.append("\nВложил:\n");
                for (JsonElement attachment : attachments) {
                    String attachmentType = removeQuotes(attachment.getAsJsonObject().get("type").toString());
                    switch (attachmentType) {
                        case "photo":
                            mainMessageText.append("фотку: ");
                            var sizes = attachment.getAsJsonObject().get("photo").getAsJsonObject().getAsJsonArray("sizes");
                            int maxHeight = 0;
                            String imgUrl = "none";
                            for (JsonElement size : sizes) {
                                int locHeight = Integer.parseInt(size.getAsJsonObject().get("height").toString());
                                if (locHeight > maxHeight) {
                                    imgUrl = size.getAsJsonObject().get("url").toString();
                                }
                            }
                            mainMessageText.append(imgUrl);
                            break;
                        case "wall":
                            mainMessageText.append("какой-то пост");
                            break;
                        default:
                            mainMessageText.append("чето с типом ").append(attachmentType);
                    }
                    mainMessageText.append("; ");
                }
            }
            messageText.append("\n\n").append(mainMessageText);
        }
        return String.valueOf(messageText);
    }
}