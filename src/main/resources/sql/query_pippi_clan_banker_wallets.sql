--- all bankers owned by clans
select b.owner_id,
       g.guildId,
       hex(SUBSTR(props.value, 0x4A, 4)) as gold,
       hex(SUBSTR(props.value, 0x95, 4)) as silver,
       hex(SUBSTR(props.value, 0xE0, 4)) as bronze
from actor_position actor
         join properties props on props.object_id = actor.id
         join buildings b on props.object_id = b.object_id
         join guilds g on b.owner_id = g.guildId
where actor.class = '/Game/Mods/Pippi/Pippi_Mob.Pippi_Mob_C'
  and props.name = 'Pippi_WalletComponent_C.walletAmount';
