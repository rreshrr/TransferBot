package ru.thomaskohouse.transferbot.vk;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "vk.bot") //  префикс
@Data // lombok
@PropertySource("classpath:application.properties")
public class VkBotProperties {
    private Long groupId;
    private String clientSecret;
    private String apiVersion;

}
