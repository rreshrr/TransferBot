package ru.thomaskohouse.transferbot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.thomaskohouse.transferbot.MessageDirection;

@Entity
@Table(name = "messages", schema = "transfer_bot")
@NoArgsConstructor
public class UniqueMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Getter
    @Setter
    String vkId;

    @Getter
    @Setter
    String telegramId;

    @Getter
    @Setter
    MessageDirection direction;


}
