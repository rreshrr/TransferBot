package ru.thomaskohouse.transferbot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity()
@Table(name = "chats", schema = "transfer_bot")
@NoArgsConstructor
@Getter
@Setter
public class VkChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long chatId;

    String name;

}
