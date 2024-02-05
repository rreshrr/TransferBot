package ru.thomaskohouse.transferbot.telegram;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataEntity {
    @Id
    @GeneratedValue
    Long id;
    String data;
}
