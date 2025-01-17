package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.ProvokeTarget;
import com.aionemu.gameserver.utils.MathUtil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvokerEffect")
public class ProvokerEffect extends ShieldEffect
{
	@XmlAttribute(name = "provoke_target")
	protected ProvokeTarget provokeTarget;
	
	@XmlAttribute(name = "skill_id")
	protected int skillId;
	
	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}
	
	@Override
	public void startEffect(Effect effect) {
		ActionObserver observer = null;
		final Creature effector = effect.getEffector();
		final float prob2 = this.hitTypeProb / 10;
		final int radius = this.radius;
		switch (this.hitType) {
			case NMLATK:
				observer = new ActionObserver(ObserverType.ATTACK) {
					@Override
					public void attack(Creature creature) {
						if (Rnd.get(0, 100) <= prob2) {
							Creature target = getProvokeTarget(provokeTarget, effector, creature);
							createProvokedEffect(effector, target);
						}
					}
				};
			break;
			case EVERYHIT:
				observer = new ActionObserver(ObserverType.ATTACKED) {
					@Override
					public void attacked(Creature creature) {
						if (radius > 0) {
							if (!MathUtil.isIn3dRange(effector, creature, radius)) {
								return;
							}
						} if (Rnd.get(0, 100) <= prob2) {
							Creature target = getProvokeTarget(provokeTarget, effector, creature);
							createProvokedEffect(effector, target);
						}
					}
				};
			break;
			case BACKATK:
				observer = new ActionObserver(ObserverType.ATTACKED) {
                    @Override
                    public void attacked(Creature creature) {
                        if (Rnd.get(0, 100) <= prob2) {
                            Creature target = getProvokeTarget(provokeTarget, effector, creature);
                            createProvokedEffect(effector, target);
                        }
                    }
                };
			break;
			case PHHIT:
                observer = new ActionObserver(ObserverType.ATTACKED) {
                    @Override
                    public void attacked(Creature creature) {
						int physical = creature.getGameStats().getMainHandPAttack().getBase();
						int magical = creature.getGameStats().getMainHandMAttack().getBase();
						if (Rnd.get(0, 100) <= prob2) {
						   if (physical > magical){
							    Creature target = getProvokeTarget(provokeTarget, effector, creature);
								createProvokedEffect(effector, target);
						   }
                        }
                    }
                };
            break;
			case MAHIT:
                observer = new ActionObserver(ObserverType.ATTACKED) {
                    @Override
                    public void attacked(Creature creature) {
						int physical = creature.getGameStats().getMainHandPAttack().getBase();
						int magical = creature.getGameStats().getMainHandMAttack().getBase();
						if (Rnd.get(0, 100) <= prob2) {
						   if (physical < magical){
							    Creature target = getProvokeTarget(provokeTarget, effector, creature);
								createProvokedEffect(effector, target);
						   }
                        }
                    }
                };
            break;
		} if (observer == null) {
			return;
		}
		effect.setActionObserver(observer, position);
		effect.getEffected().getObserveController().addObserver(observer);
	}
	
	private void createProvokedEffect(Creature effector, Creature target) {
		SkillEngine.getInstance().applyEffectDirectly(skillId, effector, target, 0);
	}
	
	private Creature getProvokeTarget(ProvokeTarget provokeTarget, Creature effector, Creature target) {
		switch (provokeTarget) {
			case ME:
				return effector;
			case OPPONENT:
				return target;
		}
		throw new IllegalArgumentException("Provoker target is invalid " + provokeTarget);
	}
	
	@Override
	public void endEffect(Effect effect) {
		ActionObserver observer = effect.getActionObserver(position);
		if (observer != null) {
			effect.getEffected().getObserveController().removeObserver(observer);
		}
	}
}