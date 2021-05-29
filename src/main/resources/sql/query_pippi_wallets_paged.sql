-- What's up with these hex(substr()) operations?
-- a property is, in essence, a serialized game object
-- Through a complex process (try and error...), I found out which bytes to read to get
-- the integer values from the blob
-- To transmit them over rcon, they are converted into a hex string.
select char.id,
       char.char_name,
       char.guild,
       hex(SUBSTR(props.value, 0x4A, 4)) as gold,
       hex(SUBSTR(props.value, 0x95, 4)) as silver,
       hex(SUBSTR(props.value, 0xE0, 4)) as bronze
from characters char
         join properties props on char.id = props.object_id
where props.name = 'Pippi_WalletComponent_C.walletAmount'
limit :limit
offset :offset;
