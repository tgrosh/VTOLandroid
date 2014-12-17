package com.singletongames.vtol.objectives;

import java.util.ArrayList;
import java.util.List;

import com.singletongames.vtol.LanderScene;

public class Objective implements IObjectiveManagerListener {
	public enum ObjectiveStatus{
		INPROGRESS,
		COMPLETE,
		FAILED
	}
	protected ObjectiveStatus status = ObjectiveStatus.INPROGRESS;
	protected List<IObjectiveListener> listeners = new ArrayList<IObjectiveListener>();
	private boolean notified = false;
	LanderScene scene;
	private String description; 
	int prerequisiteID = -1;
	boolean prerequisiteComplete = false;
	int objectiveID;
	boolean hidden = false;
	
	public Objective(ObjectiveManager manager, LanderScene scene, int objectiveID, String description, boolean hidden, IObjectiveListener listener){
		this.objectiveID = objectiveID;
		this.scene = scene;
		this.description = description;		
		this.listeners.add(listener);
		prerequisiteComplete = true;
		manager.addListener(this);
		this.hidden = hidden;
	}
	
	public Objective(ObjectiveManager manager, LanderScene scene, int objectiveID, String description, boolean hidden, int prerequisiteID, IObjectiveListener listener){
		this.objectiveID = objectiveID;
		this.scene = scene;
		this.description = description;
		this.prerequisiteID = prerequisiteID;
		if (prerequisiteID == -1) {
			prerequisiteComplete = true;
		}
		this.listeners.add(listener);
		manager.addListener(this);
		this.hidden = hidden;
	}
		
	
	public List<IObjectiveListener> getListeners() {
		return listeners;
	}
	
	public void addListener(IObjectiveListener listener) {
		listeners.add(listener);
	}
	
	protected void setComplete(){
		if (this.status != ObjectiveStatus.COMPLETE) notified = false;
		this.status = ObjectiveStatus.COMPLETE;
		if (!notified){
			for (IObjectiveListener listener: listeners){
				listener.onComplete(this);
			}
		}
		notified = true;
	}
	
	protected void setFailed(){
		if (this.status != ObjectiveStatus.FAILED) notified = false;
		this.status = ObjectiveStatus.FAILED;
		if (!notified){
			for (IObjectiveListener listener: listeners){
				listener.onFail(this);
			}
		}
		notified = true;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public int getObjectiveID() {
		return objectiveID;
	}
	
	public ObjectiveStatus getStatus() {
		return status;
	}

	public void setStatus(ObjectiveStatus status) {
		this.status = status;
		notified = false;
	}

	public int getPrerequisiteID() {
		return prerequisiteID;
	}

	@Override
	public void onAllObjectivesComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onObjectiveComplete(Objective objective) {
		if (this.prerequisiteID >= 0 && objective.getObjectiveID() == this.prerequisiteID){
			prerequisiteComplete = true;
		}
	}

	@Override
	public void onObjectiveFail(Objective objective) {
		if (this.prerequisiteID >= 0 && objective.getObjectiveID() == this.prerequisiteID){
			prerequisiteComplete = false;
		}
	}
	
}
