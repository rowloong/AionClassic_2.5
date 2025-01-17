/*
 * This file is part of aion-unique <www.aion-unique.com>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PrivateStore;
import com.aionemu.gameserver.model.trade.TradePSItem;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob;

import java.util.LinkedHashMap;

/**
 * @author Simple
 */
public class S_SHOP_SELL_LIST extends AionServerPacket {

	private Player player;
	/** Private store Information **/
	private PrivateStore store;

	public S_SHOP_SELL_LIST(PrivateStore store, Player player) {
		this.player = player;
		this.store = store;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		if (store != null) {
			Player storePlayer = store.getOwner();
			LinkedHashMap<Integer, TradePSItem> soldItems = store.getSoldItems();

			writeD(storePlayer.getObjectId());
			writeH(soldItems.size());
			for (Integer itemObjId : soldItems.keySet()) {
				Item item = storePlayer.getInventory().getItemByObjId(itemObjId);
				TradePSItem tradeItem = store.getTradeItemByObjId(itemObjId);
				long price = tradeItem.getPrice();
				writeD(itemObjId);
				writeD(item.getItemTemplate().getTemplateId());
				writeH((int) tradeItem.getCount());
				writeD((int) price);

				ItemInfoBlob itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
				itemInfoBlob.writeMe(getBuf());
			}
		}
	}
}
