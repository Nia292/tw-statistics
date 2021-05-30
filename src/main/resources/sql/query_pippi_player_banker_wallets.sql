--- all bankers owned by players
select b.owner_id,
       c.char_name,
       hex(SUBSTR(props.value, 0x4A, 4)) as gold,
       hex(SUBSTR(props.value, 0x95, 4)) as silver,
       hex(SUBSTR(props.value, 0xE0, 4)) as bronze
from actor_position actor
    join properties props on props.object_id = actor.id
    join buildings b on props.object_id = b.object_id
    join characters c on b.owner_id = c.id
where actor.class = '/Game/Mods/Pippi/Pippi_Mob.Pippi_Mob_C'
and props.name = 'Pippi_WalletComponent_C.walletAmount';
