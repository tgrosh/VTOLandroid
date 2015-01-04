package com.singletongames.vtol;

public interface ILanderListener {
	public void onTakeOff(Lander lander, LaunchPad pad);
    public void onRefuelComplete();
    public void onRepairComplete();
}
