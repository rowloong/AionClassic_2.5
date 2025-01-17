package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PortalCooldownItem;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class PortalCooldownsDAO
{
	private static final Logger log = LoggerFactory.getLogger(PortalCooldownsDAO.class);

	public static final String INSERT_QUERY = "INSERT INTO `portal_cooldowns` (`player_id`, `world_id`, `reuse_time`, `entry_count`) VALUES (?,?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `portal_cooldowns` WHERE `player_id`=?";
	public static final String SELECT_QUERY = "SELECT `world_id`, `reuse_time`, `entry_count` FROM `portal_cooldowns` WHERE `player_id`=?";

	/**
	 * @param player Player
	 */
	public static void loadPortalCooldowns(final Player player)
	{
		Connection con = null;
		FastMap<Integer, PortalCooldownItem> portalCoolDowns = new FastMap<Integer, PortalCooldownItem>();
		PreparedStatement stmt = null;
		try {
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(SELECT_QUERY);

			stmt.setInt(1, player.getObjectId());
			ResultSet rset = stmt.executeQuery();

			while (rset.next()) {
				int worldId = rset.getInt("world_id");
				long reuseTime = rset.getLong("reuse_time");
				int entryCount = rset.getInt("entry_count");
				if (reuseTime > System.currentTimeMillis()) {
					portalCoolDowns.put(worldId, new PortalCooldownItem(worldId, entryCount, reuseTime));
				}
			}
			player.getPortalCooldownList().setPortalCoolDowns(portalCoolDowns);
			rset.close();
		} catch (SQLException e) {
			log.error("LoadPortalCooldowns", e);
		} finally {
			DatabaseFactory.close(stmt, con);
		}
	}

	/**
	 * @param player Player
	 */
	public static void storePortalCooldowns(final Player player)
	{
		deletePortalCooldowns(player);
		Map<Integer, PortalCooldownItem> portalCoolDowns = player.getPortalCooldownList().getPortalCoolDowns();

		if (portalCoolDowns == null)
			return;

		for (Map.Entry<Integer, PortalCooldownItem> entry : portalCoolDowns.entrySet()) {
			final int worldId = entry.getKey();
			final long reuseTime = entry.getValue().getCooldown();
			final int entryCount = entry.getValue().getEntryCount();

			if (reuseTime < System.currentTimeMillis())
				continue;

			Connection con = null;

			PreparedStatement stmt = null;
			try {
				con = DatabaseFactory.getConnection();
				stmt = con.prepareStatement(INSERT_QUERY);

				stmt.setInt(1, player.getObjectId());
				stmt.setInt(2, worldId);
				stmt.setLong(3, reuseTime);
				stmt.setInt(4, entryCount);
				stmt.execute();
			} catch (SQLException e) {
				log.error("storePortalCooldowns", e);
			} finally {
				DatabaseFactory.close(stmt, con);
			}
		}
	}

	/**
	 * @param player Player
	 */
	private static void deletePortalCooldowns(final Player player)
	{
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(DELETE_QUERY);

			stmt.setInt(1, player.getObjectId());
			stmt.execute();
		} catch (SQLException e) {
			log.error("deletePortalCooldowns", e);
		} finally {
			DatabaseFactory.close(stmt, con);
		}
	}

}
