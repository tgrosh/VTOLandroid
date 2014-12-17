package com.singletongames.vtol;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.singletongames.vtol.Util.BodyShape;

public class PhysicsSprite extends Sprite {	
	FixtureDef mFixtureDef;
	PhysicsConnector mPhysicsConnector;	
	Body mBody;
	BodyType mBodyType;
	BodyShape mBodyShape;
	Vector2[] mVertices = null;
	boolean markedForDeath = false;
	List<FixtureDef> mFixtureDefs = null;
	List<IPhysicsSpriteListener> listeners = new ArrayList<IPhysicsSpriteListener>();
	private List<Object> mFixtureUserData = new ArrayList<Object>();
	private Object mBodyUserData;
	
	public PhysicsSprite(float pX, float pY, ITextureRegion pTextureRegion, FixtureDef pFixtureDef, Object userData, IPhysicsSpriteListener listener) {
		this(pX, pY, pTextureRegion, pFixtureDef, userData, BodyType.StaticBody, Util.BodyShape.Box, listener);		
	}
	
	public PhysicsSprite(float pX, float pY, ITextureRegion pTextureRegion, FixtureDef pFixtureDef, Object userData, BodyType pBodyType, Util.BodyShape pBodyShape, IPhysicsSpriteListener listener) {
		super(pX, pY, pTextureRegion, Resources.mEngine.getVertexBufferObjectManager());
		mFixtureDef = pFixtureDef;
		mBodyType = pBodyType;
		mBodyShape = pBodyShape;
		mFixtureUserData.add(userData);
		//mBody = Util.CreateBody(this, pFixtureDef, pBodyType, pBodyShape);
		registerContactListener();
		if (listener != null) {			
			listeners.add(listener);
		}
		Load();
	}
	
	public PhysicsSprite(float pX, float pY, ITextureRegion pTextureRegion, FixtureDef pFixtureDef, Object fixtureUserData, BodyType pBodyType, Vector2[] vertices, IPhysicsSpriteListener listener) {
		super(pX, pY, pTextureRegion, Resources.mEngine.getVertexBufferObjectManager());
		mFixtureDef = pFixtureDef;
		mFixtureUserData.add(fixtureUserData);
		mBodyType = pBodyType;
		mBodyShape = Util.BodyShape.Polygon;
		mVertices = vertices;
		//mBody = Util.CreateBody(this, pFixtureDef, pBodyType, Util.BodyShape.Polygon, vertices, null);
		registerContactListener();
		if (listener != null) {			
			listeners.add(listener);
		}
		Load();
	}

	public PhysicsSprite(float pX, float pY, ITextureRegion pTextureRegion, List<FixtureDef> defs, BodyType pBodyType, List<Object> fixtureUserData, Object bodyUserData, IPhysicsSpriteListener listener){
		super(pX, pY, pTextureRegion, Resources.mEngine.getVertexBufferObjectManager());
		mBodyType = pBodyType;
		mBodyShape = Util.BodyShape.Fixtures;
		mFixtureDefs = defs;
		mFixtureUserData = fixtureUserData;
		mBodyUserData = bodyUserData;
		registerContactListener();
		if (listener != null) {			
			listeners.add(listener);
		}
		
		Load();
	}
	
	protected void registerContactListener() {
		GameContactListener.getInstance().registerContactListener(new ContactListener() {			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {}			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {}			
			@Override
			public void endContact(Contact contact) {}			
			@Override
			public void beginContact(Contact contact) {
				if (contact.getFixtureA().getBody().equals(mBody)){
					for (IPhysicsSpriteListener listener: listeners){
						listener.onContact(contact.getFixtureA(), contact.getFixtureB());
					}
				}
				else if (contact.getFixtureB().getBody().equals(mBody)){
					for (IPhysicsSpriteListener listener: listeners){
						listener.onContact(contact.getFixtureB(), contact.getFixtureA());
					}
				}
			}
		});
	}
	
	private void Load() {
		ApplyPhysics();
	}

	private void ApplyPhysics() {		
		destroyPhysics();
		if (mVertices != null){
			mVertices = Util.TransformVertices(this, mVertices);
		}	
		else if (mFixtureDefs != null){			
			mFixtureDefs = Util.TransformVertices(this, mFixtureDefs);
		}	
		mBody = Util.CreateBody(this, mFixtureDef, mBodyType, mBodyShape, mVertices, mFixtureDefs, mFixtureUserData, mBodyUserData);
		if (mBodyUserData == null) mBody.setUserData(this);
		this.setUserData(mBody);
		mPhysicsConnector = new PhysicsConnector(this, mBody);
		Resources.mPhysicsWorld.registerPhysicsConnector(mPhysicsConnector);	
	}
	
	private void destroyPhysics(){
		if (mPhysicsConnector != null){
			Resources.mPhysicsWorld.unregisterPhysicsConnector(mPhysicsConnector);			
		}
		if (mBody != null){
			Resources.mPhysicsWorld.destroyBody(mBody);
		}
	}

	@Override
	public void setFlippedVertical(boolean pFlippedVertical) {
		super.setFlippedVertical(pFlippedVertical);

		ApplyPhysics();
	}

	@Override
	public void setFlippedHorizontal(boolean pFlippedHorizontal) {		
		super.setFlippedHorizontal(pFlippedHorizontal);
		
		ApplyPhysics();
	}

	@Override
	public void setFlipped(boolean pFlippedHorizontal, boolean pFlippedVertical) {
		super.setFlipped(pFlippedHorizontal, pFlippedVertical);
		
		ApplyPhysics();
	}
	
	public boolean isMarkedForDeath() {
		return markedForDeath;
	}

	public void setMarkedForDeath(boolean markedForDeath) {
		this.markedForDeath = markedForDeath;
	}

	public void destroy(){		
		destroyPhysics();
		if (this.hasParent()){
			this.detachSelf();
		}
		
		this.clearUpdateHandlers();
	}

	
	public List<IPhysicsSpriteListener> getListeners() {
		return listeners;
	}

	public void setListeners(List<IPhysicsSpriteListener> listeners) {
		this.listeners = listeners;
	}
	
}
