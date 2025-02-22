package com.singletongames.vtol.objectives;

import org.andengine.util.debug.Debug;

import com.singletongames.vtol.Cargo;
import com.singletongames.vtol.CargoDrop;
import com.singletongames.vtol.ILanderSceneListener;
import com.singletongames.vtol.LanderScene;
import com.singletongames.vtol.LandingPad;

public class ZoneObjective extends Objective {	
	int objectiveZoneID;
	
	public int getObjectiveZoneID() {
		return objectiveZoneID;
	}

	public ZoneObjective(ObjectiveManager manager, LanderScene scene, int id, int objectiveZoneID, String description, boolean hidden, IObjectiveListener listener) {
		super(manager, scene, id, description, hidden, listener);		
		this.objectiveZoneID = objectiveZoneID;
		
		Load();
	}
	
	public ZoneObjective(ObjectiveManager manager, LanderScene scene, int id, int objectiveZoneID, String description, boolean hidden, int prerequisiteID, IObjectiveListener listener) {
		super(manager, scene, id, description, hidden, prerequisiteID, listener);		
		this.objectiveZoneID = objectiveZoneID;
		
		Load();
	}

	private void Load() {
		scene.addListener(new ILanderSceneListener() {			
			@Override
			public void onThrottleChange(float currentThrottle) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSafeLanding(LandingPad pad) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPreviewStart() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPreviewComplete() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onObjectiveZoneExit(ObjectiveZone objZone) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onObjectiveZoneEnter(ObjectiveZone objZone) {
				//Debug.w("DEBUG: onObjectiveZoneEnter: " + objZone.getId());
				if (prerequisiteComplete && objectiveZoneID == objZone.getId()){
					ZoneObjective.super.setComplete();
				}
			}
			
			@Override
			public void onObjectiveComplete() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onNoFlyExit() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onNoFlyEnter() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMissionSuccess() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMissionFail() {
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
            public void onLanderRepairComplete() {

            }

            @Override
			public void onLanderTakeoff() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLanderDestroy() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onCameraLookComplete() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSafeReturn() {
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
		});
	}

}
