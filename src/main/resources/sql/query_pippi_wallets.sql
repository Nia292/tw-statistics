select char.id,
       char.char_name,
       char.guild,
       props.value,
       hex(SUBSTR(props.value, 0x4A, 4)) as gold,
       hex(SUBSTR(props.value, 0x95, 4)) as silver,
       hex(SUBSTR(props.value, 0xE0, 4)) as bronze
from characters char
         join properties props on char.id = props.object_id
where props.name = 'Pippi_WalletComponent_C.walletAmount'
