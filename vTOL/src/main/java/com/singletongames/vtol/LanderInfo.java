package com.singletongames.vtol;

public class LanderInfo  {
	
	protected int id;
	protected String name;
	protected String description;
    protected float engineThrustPct;
	protected float fuelCapacityPct;
	protected float fuelPerSecond;
	protected float toughnessPct;
	protected float densityPct;
	protected boolean locked;

    private final float maxEngineThrust = 40f;
    private final float minEngineThrust = 10f;
    private final float maxFuelCapacity = 500f;
    private final float minFuelCapacity = 150f;
    private final float maxToughness = 40f;
    private final float minToughness = 15f;
    private final float maxDensity = .40f;
    private final float minDensity = .10f;
	
	public LanderInfo(int id, String name, String description, float engineThrustPct, float fuelCapacityPct, float fuelPerSecond, float toughnessPct, float densityPct, boolean locked) {
		this.id = id;
		this.engineThrustPct = engineThrustPct;
		this.fuelCapacityPct = fuelCapacityPct;
		this.fuelPerSecond = fuelPerSecond;
		this.toughnessPct = toughnessPct;
		this.densityPct = densityPct;
		this.name = name;
		this.description = description;
		this.locked = locked;
	}


	public float getEngineThrust() {
		return minEngineThrust + (engineThrustPct * (maxEngineThrust - minEngineThrust));
	}
	public void setEngineThrust(float engineThrust) {
		this.engineThrustPct = engineThrust;
	}

	public float getFuelCapacity() {
		return minFuelCapacity + (fuelCapacityPct * (maxFuelCapacity - minFuelCapacity));
	}
	public void setFuelCapacity(float fuelCapacity) {
		this.fuelCapacityPct = fuelCapacity;
	}

	public float getFuelPerSecond() {
		return fuelPerSecond;
	}
	public void setFuelPerSecond(float fuelPerSecond) {
		this.fuelPerSecond = fuelPerSecond;
	}

	public float getToughness() {
		return minToughness + toughnessPct * (maxToughness - minToughness);
	}
	public void setToughness(float toughness) {
		this.toughnessPct = toughness;
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
	public void setDensity(float density) {
		this.densityPct = density;
	}

    public float getMinDensity() {
        return minDensity;
    }
    public float getMaxEngineThrust() {
        return maxEngineThrust;
    }
    public float getMinEngineThrust() {
        return minEngineThrust;
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
    public float getMaxDensity() {
        return maxDensity;
    }

    public float getEngineThrustPct() {
        return engineThrustPct;
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


    @Override
	public String toString() {
		return String.format("{ID: %1$s, Name: %2$s, engineThrustPct: %3$s, fuelCapacityPct: %4$s, fuelPerSecond: %5$s, toughnessPct: %6$s, densityPct: %7$s}", String.valueOf(this.getId()), this.getName(), String.valueOf(this.getEngineThrust()), String.valueOf(this.getFuelCapacity()), String.valueOf(this.getFuelPerSecond()), String.valueOf(this.getToughness()), String.valueOf(this.getDensity()));
	}

	
	
}
