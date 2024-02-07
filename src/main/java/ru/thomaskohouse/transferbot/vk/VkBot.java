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
import ru.thomaskohouse.transferbot.service.VkChatService;
import ru.thomaskohouse.transferbot.telegram.TelegramBot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class VkBot {
    private final VkBotProperties vkBotProperties;
    public VkBot(@Autowired VkBotProperties vkBotProperties, @Autowired TelegramBot telegramBot,
                 @Autowired VkChatService vkChatService) throws  ClientException, ApiException {
        this.vkBotProperties = vkBotProperties;
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
                    Integer ts = 2500;
                    while (true) {
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        String url = serverUrl + "?act=a_check&key=" + key + "&ts=" + ts + "&wait=25";
                        String json = null;
                        try {
                            json = loadJson(url);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        JsonObject messageObject = gs.fromJson(json, JsonObject.class);
                        ts = messageObject.get("ts").getAsInt();
                        JsonArray msg_array = messageObject.getAsJsonArray("updates");
                        for (JsonElement ob: msg_array) {
                            //System.out.println(ob.getAsJsonObject());
                            JsonObject message = ob.getAsJsonObject().get("object").getAsJsonObject().get("message").getAsJsonObject();
                            String res = null;
                            try {
                                res = getUsername(removeQuotes(message.get("from_id").toString()));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            String chatName = message.get("peer_id").toString();
                            chatName = vkChatService.getChatName(Long.parseLong(chatName));
                            res += "\n" + "из чатика " + chatName;
                            res += "\n" + getStringDateTimeFromUnixTime(removeQuotes(message.get("date").toString()));
                            String messageText = removeQuotes(message.get("text").toString());
                            if (message.has("reply_message")){
                                JsonObject replyMessage = message.get("reply_message").getAsJsonObject();
                                String[] replyLines = removeQuotes(replyMessage.get("text").toString()).split("\n");
                                for (String line: replyLines) {
                                    messageText += "\t\n>" + line;
                                }
                            } else {
                                //System.out.println("Reply is empty");
                            }

                            if (message.has("attachments")) {


                                var attachments = message.getAsJsonArray("attachments");
                                if (!attachments.isEmpty())
                                    messageText += "\nВложил:\n";
                                for (JsonElement attachment : attachments) {
                                    String attachmentType = removeQuotes(attachment.getAsJsonObject().get("type").toString());
                                    switch (attachmentType){
                                        case "photo":
                                            messageText += "фотку: ";
                                            var sizes = attachment.getAsJsonObject().get("photo").getAsJsonObject().getAsJsonArray("sizes");
                                            int maxHeight = 0;
                                            String imgUrl = "none";
                                            for (JsonElement size : sizes) {
                                                int locHeight = Integer.parseInt(size.getAsJsonObject().get("height").toString());
                                                if (locHeight > maxHeight){
                                                    imgUrl = size.getAsJsonObject().get("url").toString();
                                                }
                                            }
                                            messageText += imgUrl;
                                            break;
                                        case "wall":
                                            messageText += "какой-то пост";
                                            break;
                                        default:
                                            messageText += "чето с типом " + attachmentType;
                                    }
                                    messageText += "; ";
                                }
                            }
                            res += "\n\n" + messageText;

                            //System.out.println(res);
                            telegramBot.sendTextMessage(res);
                        }
                    }
                }
        );
        thread.start();
    }

    private static String removeQuotes(String string){
        return string.replaceAll("\"", "");
    }

    private static String getStringDateTimeFromUnixTime(String unixTimestamp){
        Long unixTime = Long.parseLong(unixTimestamp);
        Date date = new Date(unixTime*1000L);
        SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return jdf.format(date);
    }

    private static String loadJson(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    private String getUsername(String userId) throws IOException {
        Gson gs = new Gson();
        String url = "https://api.vk.com/method/users.get?user_ids=" + userId +
                "&fields=about&name_case=nom&from_group_id=" +vkBotProperties.getGroupId()
                +"&access_token="+ vkBotProperties.getClientSecret()+"&v=5.199";
        String response = loadJson(url);
        JsonObject responseJson = gs.fromJson(response, JsonObject.class);
        JsonObject firstObject = responseJson.getAsJsonArray("response").get(0).getAsJsonObject();
        return removeQuotes(firstObject.get("first_name").toString()) + " " + removeQuotes(firstObject.get("last_name").toString());
    }

}
