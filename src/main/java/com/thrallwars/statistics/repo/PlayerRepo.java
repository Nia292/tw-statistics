package com.thrallwars.statistics.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlayerRepo extends MongoRepository<Player, String> {
}
