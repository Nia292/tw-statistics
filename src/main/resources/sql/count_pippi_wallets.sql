select count(char.id)
from characters char
         join properties props on char.id = props.object_id
where props.name = 'Pippi_WalletComponent_C.walletAmount';
