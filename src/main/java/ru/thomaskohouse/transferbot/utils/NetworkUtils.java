package ru.thomaskohouse.transferbot.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import ru.thomaskohouse.transferbot.vk.VkBotProperties;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class NetworkUtils {
    private final VkBotProperties vkBotProperties;
    private  final TransferUtils transferUtils;
    public String httpGet (String url) {
        String responseString = null;
        HttpGet httpGet = new HttpGet(url);
        try (CloseableHttpClient client =
                     HttpClients.custom().setDefaultRequestConfig(
                             RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                     .build()) {
             CloseableHttpResponse response = client
                     .execute(httpGet);
            responseString = EntityUtils.toString(response.getEntity());
        } catch (Exception e){
            System.err.println("Что-то пошло не так во время отправки GET-запроса " + url);
        }
        return responseString;
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

    public void sendToVk(String text) throws IOException {
        String url = "https://api.vk.com/method/messages.send";

        CloseableHttpClient httpClient =
                HttpClients.custom().setDefaultRequestConfig(
                                RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                        .build();
        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> params = new ArrayList<NameValuePair>(5);
        params.add(new BasicNameValuePair("random_id", "0"));
        params.add(new BasicNameValuePair("peer_id", transferUtils.getVkChatId().toString()));
        params.add(new BasicNameValuePair("message", text));
        params.add(new BasicNameValuePair("access_token", vkBotProperties.getClientSecret()));
        params.add(new BasicNameValuePair("v", "5.199"));

        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        httpClient.execute(httpPost);
        httpClient.close();
    }
}
