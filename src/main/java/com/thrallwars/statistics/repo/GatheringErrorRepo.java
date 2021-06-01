package com.thrallwars.statistics.repo;

import com.thrallwars.statistics.entity.ClanBankerWallet;
import com.thrallwars.statistics.entity.GatheringError;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GatheringErrorRepo extends MongoRepository<GatheringError, String> {
}
