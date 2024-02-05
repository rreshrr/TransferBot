package ru.thomaskohouse.transferbot.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "transfer") //  префикс
@Data // lombok
@PropertySource("classpath:application.properties")
public class TransferProperties {
    private Long vkChatId; //чат куда шлём вк
    private String tgChatId; //чат куда шлем в тг
}
