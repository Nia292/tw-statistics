package com.thrallwars.statistics.repo;

import com.thrallwars.statistics.entity.PlayerBankerWallet;
import com.thrallwars.statistics.entity.PlayerWallet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlayerBankerWalletRepo extends MongoRepository<PlayerBankerWallet, String> {
}
