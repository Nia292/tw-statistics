package com.thrallwars.statistics.repo;

import com.thrallwars.statistics.entity.OnlinePlayer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OnlinePlayersRepo  extends MongoRepository<OnlinePlayer, String> {
}
