package com.aionemu.gameserver.model.templates.world;

import com.aionemu.gameserver.configs.main.WorldConfig;
import com.aionemu.gameserver.world.WorldDropType;
import com.aionemu.gameserver.world.WorldType;
import com.aionemu.gameserver.world.zone.ZoneAttributes;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "map")
@XmlAccessorType(XmlAccessType.NONE)
public class WorldMapTemplate
{
	@XmlAttribute(name = "name")
	protected String name = "";

	@XmlAttribute(name = "id", required = true)
	protected Integer mapId;

	@XmlAttribute(name = "name_id", required = true)
	protected Integer nameId;

	@XmlAttribute(name = "twin_count")
	protected int twinCount;
	
	@XmlAttribute(name = "beginner_twin_count")
	protected int beginnerTwinCount;

	@XmlAttribute(name = "max_user")
	protected int maxUser;

	@XmlAttribute(name = "prison")
	protected boolean prison = false;

	@XmlAttribute(name = "instance")
	protected boolean instance = false;

	@XmlAttribute(name = "death_level", required = true)
	protected int deathlevel = 0;

	@XmlAttribute(name = "water_level", required = true)
	protected int waterlevel = 16;

	@XmlAttribute(name = "world_type")
	protected WorldType worldType = WorldType.NONE;

	@XmlAttribute(name = "world_size")
	protected int worldSize;

	@XmlElement(name = "ai_info")
	protected AiInfo aiInfo = AiInfo.DEFAULT;

	@XmlAttribute(name = "except_buff")
	protected boolean exceptBuff = false;

	@XmlAttribute(name = "flags")
	protected List<ZoneAttributes> flagValues;
	
	@XmlAttribute(name = "drop_type")
	protected WorldDropType dropWorldType = WorldDropType.NONE;

	@XmlTransient
	protected Integer flags;

	public String getName() {
		return name;
	}

	public Integer getMapId() {
		return mapId;
	}

	public int getTwinCount() {
		if (WorldConfig.WORLD_MAX_TWINS_USUAL == 0) {
			return twinCount;
		}
		return Math.min(WorldConfig.WORLD_MAX_TWINS_USUAL, twinCount);
	}

	public int getBeginnerTwinCount() {
		if (WorldConfig.WORLD_MAX_TWINS_BEGINNER == 0) {
			return beginnerTwinCount;
		} else if (WorldConfig.WORLD_MAX_TWINS_BEGINNER == -1) {
			return 0;
		}
		return Math.min(WorldConfig.WORLD_MAX_TWINS_BEGINNER, beginnerTwinCount);
	}

	public int getMaxUser() {
		return maxUser;
	}

	public boolean isPrison() {
		return prison;
	}

	public boolean isInstance() {
		return instance;
	}

	public int getWaterLevel() {
		return waterlevel;
	}

	public int getDeathLevel() {
		return deathlevel;
	}

	public WorldType getWorldType() {
		return worldType;
	}

	public int getWorldSize() {
		return worldSize;
	}

	public WorldDropType getWorldDropType() {
		return dropWorldType;
	}

	public boolean isFly() {
		return (flags & ZoneAttributes.FLY.getId()) != 0;
	}

	public boolean canGlide() {
		return (flags & ZoneAttributes.GLIDE.getId()) != 0;
	}

	public boolean canPutKisk() {
		return (flags & ZoneAttributes.BIND.getId()) != 0;
	}

	public boolean canRecall() {
		return (flags & ZoneAttributes.RECALL.getId()) != 0;
	}

	public boolean canRide() {
		return (flags & ZoneAttributes.RIDE.getId()) != 0;
	}

	public boolean canFlyRide() {
		return (flags & ZoneAttributes.FLY_RIDE.getId()) != 0;
	}

	public boolean isPvpAllowed() {
		return (flags & ZoneAttributes.PVP_ENABLED.getId()) != 0;
	}

	public boolean isSameRaceDuelsAllowed() {
		return (flags & ZoneAttributes.DUEL_SAME_RACE_ENABLED.getId()) != 0;
	}

	public boolean isOtherRaceDuelsAllowed() {
		return (flags & ZoneAttributes.DUEL_OTHER_RACE_ENABLED.getId()) != 0;
	}

	public int getFlags() {
		return flags;
	}

	protected void afterUnmarshal(Unmarshaller u, Object parent) {
		flags = ZoneAttributes.fromList(flagValues);
	}

	public boolean isExceptBuff() {
		return exceptBuff;
	}

	public AiInfo getAiInfo() {
		return aiInfo;
	}

	public Integer getNameId() {
		return nameId;
	}
}