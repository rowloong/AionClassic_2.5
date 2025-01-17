package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.controllers.movement.MovementMask;
import com.aionemu.gameserver.controllers.movement.PlayerMoveController;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_MOVE_NEW;
import com.aionemu.gameserver.taskmanager.tasks.TeamMoveUpdater;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

public class CM_MOVE extends AionClientPacket
{
	private byte type;
	private byte heading;
	private float x;
	private float y;
	private float z;
	private float x2;
	private float y2;
	private float z2;
	private float vehicleX;
	private float vehicleY;
	private float vehicleZ;
	private float vectorX;
	private float vectorY;
	private float vectorZ;
	private byte glideFlag;
	private int unk1;
	private int unk2;
	
	public CM_MOVE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null || !player.isSpawned()) {
			return;
		}
		x = readF();
		y = readF();
		z = readF();
		heading = (byte) readC();
		type = (byte) readC();
		if ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE) {
			if ((type & MovementMask.MOUSE) == 0) {
				vectorX = readF();
				vectorY = readF();
				vectorZ = readF();
				x2 = vectorX + x;
				y2 = vectorY + y;
				z2 = vectorZ + z;
			} else {
				x2 = readF();
				y2 = readF();
				z2 = readF();
			}
		} if ((type & MovementMask.GLIDE) == MovementMask.GLIDE) {
			glideFlag = (byte) readC();
		} if ((type & MovementMask.VEHICLE) == MovementMask.VEHICLE) {
			unk1 = readD();
			unk2 = readD();
			vehicleX = readF();
			vehicleY = readF();
			vehicleZ = readF();
		}
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player.getLifeStats().isAlreadyDead())
			return;
		if (player.getEffectController().isUnderFear())
			return;
		PlayerMoveController m = player.getMoveController();
		m.movementMask = type;
		if (player.getAdminTeleportation() && ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE) && ((type & MovementMask.MOUSE) == MovementMask.MOUSE)) {
			m.setNewDirection(x2, y2, z2);
			World.getInstance().updatePosition(player, x2, y2, z2, heading);
			PacketSendUtility.broadcastPacketAndReceive(player, new S_MOVE_NEW(player));
		}
		float speed = player.getGameStats().getMovementSpeedFloat();
		if ((type & MovementMask.GLIDE) == MovementMask.GLIDE) {
			m.glideFlag = glideFlag;
			player.getFlyController().switchToGliding();
		} else {
			player.getFlyController().onStopGliding(false);
		} if (type == 0) {
			player.getController().onStopMove();
			player.getFlyController().onStopGliding(false);
		} else if ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE) {
			if ((type & MovementMask.MOUSE) == 0) {
				speed = player.getGameStats().getMovementSpeedFloat();
				m.vectorX = vectorX;
				m.vectorY = vectorY;
				m.vectorZ = vectorZ;
			}
			player.getMoveController().setNewDirection(x2, y2, z2, heading);
			player.getController().onStartMove();
		} else {
			player.getController().onMove();
			if ((type & MovementMask.MOUSE) == 0) {
				speed = player.getGameStats().getMovementSpeedFloat();
				player.getMoveController().setNewDirection(x + m.vectorX * speed * 1.5f, y + m.vectorY * speed * 1.5f, z + m.vectorZ * speed * 1.5f, heading);
			}
		} if ((type & MovementMask.VEHICLE) == MovementMask.VEHICLE) {
			m.unk1 = unk1;
			m.unk2 = unk2;
			m.vehicleX = vehicleX;
			m.vehicleY = vehicleY;
			m.vehicleZ = vehicleZ;
		}
		World.getInstance().updatePosition(player, x, y, z, heading);
		m.updateLastMove();
		if (player.isInGroup2() || player.isInAlliance2()) {
			TeamMoveUpdater.getInstance().startTask(player);
		} if ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE || type == 0) {
			PacketSendUtility.broadcastPacket(player, new S_MOVE_NEW(player));
		} if ((type & MovementMask.FALL) == MovementMask.FALL) {
			m.updateFalling(z);
		} else {
			m.stopFalling();
		} if (type != 0 && player.isProtectionActive()) {
			player.getController().stopProtectionActiveTask();
		}
	}
}