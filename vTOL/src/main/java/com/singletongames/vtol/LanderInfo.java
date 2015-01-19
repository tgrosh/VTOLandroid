package com.singletongames.vtol;

public class LanderInfo  {

    protected int id;
	protected String name;
	protected String description;
    protected float speedLimitPct;
    protected float powerPct;
	protected float fuelCapacityPct;
	protected float fuelPerSecond;
	protected float toughnessPct;
	protected float densityPct;
	protected boolean locked;
    protected float agilityPct = .1f;

    private final float maxSpeedLimit = 12f;
    private final float minSpeedLimit = 5f;
    private final float maxPower = 40f;
    private final float minPower = 10f;
    private final float maxFuelCapacity = 500f;
    private final float minFuelCapacity = 150f;
    private final float maxToughness = 40f;
    private final float minToughness = 15f;
    private final float maxDensity = .40f;
    private final float minDensity = .10f;
    private final float maxAngleLimit = 75f;
    private final float minAngleLimit = 20f;
    private final float maxTurnRate = .05f;
    private final float minTurnRate = .02f;
    private final float maxAgility = 0f;
    private final float minAgility = 1f;

	public LanderInfo(int id, String name, String description, float speedLimitPct, float agilityPct, float powerPct, float fuelCapacityPct, float fuelPerSecond, float toughnessPct, float densityPct, boolean locked) {
		this.id = id;
        this.speedLimitPct = speedLimitPct;
        this.agilityPct = agilityPct;
		this.powerPct = powerPct;
		this.fuelCapacityPct = fuelCapacityPct;
		this.fuelPerSecond = fuelPerSecond;
		this.toughnessPct = toughnessPct;
		this.densityPct = densityPct;
		this.name = name;
		this.description = description;
		this.locked = locked;
	}

    public float getAgility(){
        return minAgility + (agilityPct * (maxAgility - minAgility));
    }

    public float getAngleLimit() {
        return minAngleLimit + (agilityPct * (maxAngleLimit - minAngleLimit));
    }

    public float getTurnRate() {
        return minTurnRate + (agilityPct * (maxTurnRate - minTurnRate));
    }

    public float getSpeedLimit() {
        return minSpeedLimit + (speedLimitPct * (maxSpeedLimit - minSpeedLimit));
    }

    public float getPower() {
		return minPower + (powerPct * (maxPower - minPower));
	}

	public float getFuelCapacity() {
		return minFuelCapacity + (fuelCapacityPct * (maxFuelCapacity - minFuelCapacity));
	}

	public float getFuelPerSecond() {
		return fuelPerSecond;
	}

	public float getToughness() {
		return minToughness + toughnessPct * (maxToughness - minToughness);
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public float getDensity() {
		return minDensity + densityPct * (maxDensity - minDensity);
	}

    public float getMaxPower() {
        return maxPower;
    }
    public float getMinPower() {
        return minPower;
    }
    public float getMaxFuelCapacity() {
        return maxFuelCapacity;
    }
    public float getMinFuelCapacity() {
        return minFuelCapacity;
    }
    public float getMaxToughness() {
        return maxToughness;
    }
    public float getMinToughness() {
        return minToughness;
    }
    public float getMinDensity() {
        return minDensity;
    }
    public float getMaxDensity() {
        return maxDensity;
    }
    public float getMinSpeedLimit() {
        return minSpeedLimit;
    }
    public float getMaxSpeedLimit() {
        return maxSpeedLimit;
    }
    public float getPowerPct() {
        return powerPct;
    }
    public float getFuelCapacityPct() {
        return fuelCapacityPct;
    }
    public float getToughnessPct() {
        return toughnessPct;
    }
    public float getDensityPct() {
        return densityPct;
    }
    public float getSpeedLimitPct() {
        return speedLimitPct;
    }
    public float getAgilityPct() {
        return agilityPct;
    }
    public float getMaxAngleLimit() {
        return maxAngleLimit;
    }
    public float getMinAngleLimit() {
        return minAngleLimit;
    }
    public float getMaxTurnRate() {
        return maxTurnRate;
    }
    public float getMinTurnRate() {
        return minTurnRate;
    }

    @Override
	public String toString() {
		return String.format("{ID: %1$s, Name: %2$s, powerPct: %3$s, fuelCapacityPct: %4$s, fuelPerSecond: %5$s, toughnessPct: %6$s, densityPct: %7$s}", String.valueOf(this.getId()), this.getName(), String.valueOf(this.getPower()), String.valueOf(this.getFuelCapacity()), String.valueOf(this.getFuelPerSecond()), String.valueOf(this.getToughness()), String.valueOf(this.getDensity()));
	}

	
	
}
