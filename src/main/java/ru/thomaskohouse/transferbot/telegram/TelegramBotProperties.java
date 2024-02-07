package ru.thomaskohouse.transferbot.telegram;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bot") //  префикс
@Data // lombok
@PropertySource("classpath:application.properties")
public class TelegramBotProperties {
    private String name;
    private String token;
    private Long ownerId;
}
