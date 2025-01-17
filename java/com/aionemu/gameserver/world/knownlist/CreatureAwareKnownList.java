/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package com.aionemu.gameserver.world.knownlist;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;

/**
 * @author ATracer
 */
public class CreatureAwareKnownList extends KnownList {

	public CreatureAwareKnownList(VisibleObject owner) {
		super(owner);
	}

	@Override
	protected final boolean isAwareOf(VisibleObject newObject) {
		return newObject instanceof Creature;
	}
}
