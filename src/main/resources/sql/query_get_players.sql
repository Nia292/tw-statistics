select c.playerId, c.char_name, c.level, c.guild, c.lastTimeOnline, g.name as clanName
from characters c
         left join guilds g on c.guild = g.guildId
limit :limit
offset :offset;
