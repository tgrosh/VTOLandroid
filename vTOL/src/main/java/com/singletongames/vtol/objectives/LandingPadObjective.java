package com.singletongames.vtol.objectives;

import com.singletongames.vtol.Cargo;
import com.singletongames.vtol.CargoDrop;
import com.singletongames.vtol.ILanderSceneListener;
import com.singletongames.vtol.LanderScene;
import com.singletongames.vtol.LandingPad;

public class LandingPadObjective extends Objective implements ILanderSceneListener {
	int landingPadId = -1;
	
	public LandingPadObjective(ObjectiveManager manager, LanderScene scene, int objectiveID, int landingPadId, String description, boolean hidden, IObjectiveListener listener) {
		super(manager, scene, objectiveID, description, hidden, listener);
		this.landingPadId = landingPadId;
		scene.addListener(this);
	}

	public LandingPadObjective(ObjectiveManager manager, LanderScene scene, int objectiveID, int landingPadId, String description, boolean hidden, int prerequisiteID, IObjectiveListener listener) {
		super(manager, scene, objectiveID, description, hidden, prerequisiteID, listener);
		this.landingPadId = landingPadId;
		scene.addListener(this);
	}

	@Override
	public void onThrottleChange(float currentThrottle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPreviewComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPreviewStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraLookComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLanderTakeoff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLanderTouchdown() {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void onLanderRefuelComplete() {

    }

    @Override
	public void onSafeLanding(LandingPad pad) {
		if (this.prerequisiteComplete && pad.getId() == this.landingPadId){
			setComplete();
		}
	}

	@Override
	public void onSafeReturn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLanderDestroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMissionFail() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMissionSuccess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onObjectiveComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onObjectiveZoneEnter(ObjectiveZone objectiveZone) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onObjectiveZoneExit(ObjectiveZone objectiveZone) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNoFlyEnter() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNoFlyExit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCargoPickup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCargoDeliver(Cargo cargo, CargoDrop drop) {
		// TODO Auto-generated method stub
		
	}

}
