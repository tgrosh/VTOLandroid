package com.singletongames.vtol.objectives;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.andengine.entity.Entity;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.shape.RectangularShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.ease.EaseBackIn;
import org.andengine.util.modifier.ease.EaseBackOut;
import org.andengine.util.modifier.ease.EaseElasticOut;
import org.andengine.util.modifier.ease.EaseStrongOut;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.singletongames.vtol.LanderScene;
import com.singletongames.vtol.Resources;
import com.singletongames.vtol.objectives.Objective.ObjectiveStatus;

import android.view.animation.ScaleAnimation;

public class ObjectiveManager extends Rectangle {
	List<Objective> objectives = new ArrayList<Objective>();
	List<Objective> visibleObjectives = new ArrayList<Objective>();
	LanderScene scene;
	private List<Sprite> bullets = new ArrayList<Sprite>();
	private List<Sprite> checks = new ArrayList<Sprite>();
	int chapterID;	
	int levelID;
	List<IObjectiveManagerListener> listeners = new ArrayList<IObjectiveManagerListener>();
	WaypointObjective currentWaypoint;
	
	public ObjectiveManager(LanderScene scene, RectangularShape drawArea, int chapterID, int levelID, IObjectiveManagerListener listener) {
		super(drawArea.getX(), drawArea.getY(), drawArea.getWidth(), drawArea.getHeight(), Resources.mEngine.getVertexBufferObjectManager());
		this.scene = scene;
		this.chapterID = chapterID;
		this.levelID = levelID;
		this.listeners.add(listener);
		
		this.setColor(Color.TRANSPARENT);
		
		Load();
	}

	private void Load() {
		loadObjectives();		
		drawObjectives();
	}

	private void loadObjectives() {
		try {
			final SAXParserFactory spf = SAXParserFactory.newInstance();
			
        	SAXParser sp = spf.newSAXParser();
			final XMLReader xmlReader = sp.getXMLReader();
			
			final ObjectiveParser objectiveParser = new ObjectiveParser(this, scene, chapterID, levelID, new IObjectiveListener() {
				@Override
				public void onComplete(Objective objective) {
					//if the objective had a prerequisite that isnt done, override the complete and set it back to in progress
					for (IObjectiveManagerListener listener: listeners){
						listener.onObjectiveComplete(objective);
					}
					showCheckMark(getObjectivePosition(objective));
					
					boolean allComplete = true;
					for (Objective obj: objectives){
						if (obj.getStatus() != ObjectiveStatus.COMPLETE){
							allComplete = false;
						}
					}
					if (allComplete){
						for (IObjectiveManagerListener listener: listeners){
							listener.onAllObjectivesComplete();
						}
					}
					currentWaypoint = getNextWaypointObjective();
				}
				@Override
				public void onFail(Objective objective) {
					currentWaypoint = getNextWaypointObjective();
				}
			});
	        xmlReader.setContentHandler(objectiveParser);
	        
			InputStream inputStream = Resources.mActivity.getAssets().open("objectives/Objectives.xml");			
            xmlReader.parse(new InputSource(new BufferedInputStream(inputStream)));
            
            this.objectives = objectiveParser.getObjectives();
            for (Objective objective : objectives){	
    			if (!objective.isHidden()){
    				this.visibleObjectives.add(objective);
    			}
    		}
            currentWaypoint = getNextWaypointObjective();
            
            inputStream.close();            
		} 
        catch (IOException e) {
			e.printStackTrace();
		} 
        catch (SAXException e) {
			e.printStackTrace();
		} 
        catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	private Objective getObjective(int objectiveID){
		for (Objective obj: objectives){
			if (obj.getObjectiveID() == objectiveID){
				return obj;
			}
		}
		return null;
	}

	private WaypointObjective getNextWaypointObjective(){
		for (Objective obj: objectives){
			if (obj.getClass().equals(WaypointObjective.class) && obj.getStatus() != ObjectiveStatus.COMPLETE){
				return (WaypointObjective) obj;
			}
		}
		
		return null;
	}
	
	
	protected void showCheckMark(int objectivePosition) {
		if (objectivePosition >= 0){
			Sprite check = new Sprite(-7, -5, Resources.ObjectiveCheck, Resources.mEngine.getVertexBufferObjectManager());
			checks.add(check);
			check.setScale(0);
			ScaleModifier scaler = new ScaleModifier(.5f, 0, 1, EaseElasticOut.getInstance());
			check.registerEntityModifier(scaler);
			bullets.get(objectivePosition).attachChild(check);
		}
		
	}

	public List<Objective> getObjectives() {
		return objectives;
	}

	public void addObjective(Objective objective) {
		this.objectives.add(objective);
	}
	
	private void drawObjectives(){
		float currentY = 0;
		
		for (Objective objective : visibleObjectives){				
			Text txt = new Text(0, 0, Resources.mFont_Green24, objective.getDescription(), Resources.mEngine.getVertexBufferObjectManager());
			txt.setPosition(25, currentY);
			txt.setAutoWrap(AutoWrap.WORDS);
			txt.setAutoWrapWidth(this.getWidth() - 25);
			
			Sprite bullet = new Sprite(0, txt.getY() + txt.getHeight()/2 - Resources.ObjectiveBullet.getHeight()/2, Resources.ObjectiveBullet, Resources.mEngine.getVertexBufferObjectManager());
			bullets.add(bullet);
			
			this.attachChild(txt);
			this.attachChild(bullet);
			
			currentY += (txt.getHeight() + 5);			
		}
		
	}
	
	private int getObjectivePosition(Objective objective){		
		for (int x=0; x < visibleObjectives.size(); x++){
			if (visibleObjectives.get(x).equals(objective)){
				return x;
			}
		}
		
		return -1;
	}
	
	public List<IObjectiveManagerListener> getListeners() {
		return listeners;
	}

	public void addListener(IObjectiveManagerListener listener) {
		this.listeners.add(listener);
	}

	
	public WaypointObjective getCurrentWaypoint() {
		return currentWaypoint;
	}

}
