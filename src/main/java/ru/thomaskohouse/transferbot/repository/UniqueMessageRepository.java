package ru.thomaskohouse.transferbot.repository;

import org.springframework.data.repository.CrudRepository;
import ru.thomaskohouse.transferbot.entity.UniqueMessage;

public interface UniqueMessageRepository extends CrudRepository<UniqueMessage, Long> {
    UniqueMessage findUniqueMessageByTelegramId(String tgId);
}
