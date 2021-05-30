package com.thrallwars.statistics.repo;

import com.thrallwars.statistics.entity.ClanBankerWallet;
import com.thrallwars.statistics.entity.PlayerBankerWallet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClanBankerWalletRepo extends MongoRepository<ClanBankerWallet, String> {
}
