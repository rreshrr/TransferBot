package ru.thomaskohouse.transferbot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.thomaskohouse.transferbot.vk.VkBotProperties;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@AllArgsConstructor
public class NetworkUtils {
    private final VkBotProperties vkBotProperties;
    public String httpGet (String url) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();
            HttpResponse<String> httpResponse = HttpClient.newBuilder().build()
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return httpResponse.body();
        } catch (URISyntaxException e) {
            System.err.println("Ошибка при передачи URI вк для загрузки JSON с инфой о новых сообщениях");
            throw new RuntimeException(e);
        } catch (InterruptedException | IOException e) {
            System.err.println("Ошибка при выполнении GET запроса в вк");
            throw new RuntimeException(e);
        }
    }

    public String getUsername(String userId) {
        Gson gs = new Gson();
        String url = "https://api.vk.com/method/users.get?user_ids=" + userId +
                "&fields=about&name_case=nom&from_group_id=" +vkBotProperties.getGroupId()
                +"&access_token="+ vkBotProperties.getClientSecret()+"&v=5.199";
        String response = httpGet(url);
        JsonObject responseJson = gs.fromJson(response, JsonObject.class);
        JsonObject firstObject = responseJson.getAsJsonArray("response").get(0).getAsJsonObject();
        return firstObject.get("first_name").toString() + " " + firstObject.get("last_name").toString();
    }


}
