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
package quest.eltnen;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _1300Orders_From_Telemachus extends QuestHandler
{
	private final static int questId = 1300;
	private final static int[] npcs = {203901};
	
	public _1300Orders_From_Telemachus() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
        for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        if (player == null || env == null) {
            return false;
        } if (player.getRace() == Race.ELYOS && player.getLevel() >= 20 && player.getLevel() <= 55) {
            QuestService.startQuest(env);
            return true;
        }
        return false;
    }
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 203901) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
					} case SELECT_REWARD: {
						qs.setQuestVarById(0, 1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
                        return sendQuestDialog(env, 5);
					}
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203901) {
                if (env.getDialog() == QuestDialog.SELECT_NO_REWARD) {
                    int[] eltnenMission = {1031, 1032, 1033, 1034, 1035, 1036, 1037, 1038, 1039, 1040, 1041, 1042, 1043};
                    for (int mission: eltnenMission) {
                        QuestEngine.getInstance().onEnterZoneMissionEnd(new QuestEnv(env.getVisibleObject(), env.getPlayer(), mission, env.getDialogId()));
                    }
                }
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}