package com.singletongames.vtol;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.singletongames.vtol.Util.BodyShape;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ColorModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.CircleParticleEmitter;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.Constants;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;

import java.util.ArrayList;
import java.util.List;

public abstract class Lander extends PhysicsAnimatedSprite {

	protected LanderInfo info;
	
	protected Lander mThis = this;
	private AnimatedSprite explosion;
	private List<Sprite> sLanderDebris = new ArrayList<Sprite>();
	private SmoothCamera camera = (SmoothCamera) Resources.mEngine.getCamera();
	
	private float currentThrottle = 0f;
	private float desiredAngle;
	private boolean airborn = false;
	private boolean enginesOn = false;
	private List<Body> baseContacts = new ArrayList<Body>();
	private float maxZoomOut = .75f;
	private float maxZoomIn = 1.5f;	
	private float currentFuel = 0f;
	private boolean exploding = false;
	private boolean destroyed = false;	
	private boolean cameraFocused = false;
	private int noFlyZoneCount = 0;
	private boolean cargoConnected = false;
	//private Line ropeLine;
	private RopeJoint ropeJoint;
	private List<Line> ropeLines = new ArrayList<Line>();
	Body cargoBody;
	int direction = 0;
	float totalHealth = 0;
	float healthRemaining = 0;
	ILanderListener listener;
	boolean takeOffComplete = false;
	float refuelRate = .20f; //percentage of fuel restore per second
    float repairRate = .10f; //percentage of health restore per second

	private List<ExhaustEmitter> exhaustEmitters = new ArrayList<ExhaustEmitter>();
	private List<TakeoffEmitter> takeoffEmitters = new ArrayList<TakeoffEmitter>();
	private boolean paused;
    boolean refueling = false;
    boolean repairing = false;
    private LoopEntityModifier damageCriticalLooper = null;

    public Lander(float pX, float pY, LanderInfo info, TiledTextureRegion landerTextureRegion, List<FixtureDef> fixtureDefs, List<Object> fixtureUserData, ILanderListener listener) {
		super(pX,pY,landerTextureRegion,fixtureDefs, BodyType.DynamicBody, fixtureUserData, "Lander", null);
		this.info = info;
		currentFuel = info.getFuelCapacity();
		healthRemaining = totalHealth = info.getToughness() * 10;
		this.listener = listener;
		
		Load();
	}

	private void Load() {
		createExhaustEmitters(); //attach first so the smoke is behind the lander
		createTakeoffEmitters();
		
		if (Resources.RocketEngine0.isPlaying()) {
			Resources.RocketEngine0.pause();
		}
		else if (Resources.RocketEngine0.isStopped()) {
			Resources.RocketEngine0.play();
			Resources.RocketEngine0.pause();
		}
		
		GameContactListener.getInstance().registerContactListener(new ContactListener() {			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				
			}			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {				
				if (destroyed) return;
								
				if (enginesOn && impulse.getNormalImpulses()[0] > (info.getToughness()/10) && (contact.getFixtureA().getBody().equals(mBody) || contact.getFixtureB().getBody().equals(mBody))){
					healthRemaining -= impulse.getNormalImpulses()[0];
					//Debug.w("DEBUG: Damage Taken: " + impulse.getNormalImpulses()[0]);
					//Debug.w("DEBUG: Health Remaining: " + healthRemaining);
					showDamageTaken();
					
					if (healthRemaining <= 0){
						Explode(contact.getWorldManifold().getPoints()[0].x*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,contact.getWorldManifold().getPoints()[0].y*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
					}
				}				
				
				if (impulse.getNormalImpulses()[0] > (totalHealth/2) && contact.getFixtureA().getBody().equals(mBody)) {
					//Debug.w("DEBUG: Impact Detected (" + impulse.getNormalImpulses()[0] + ")");
					Explode(contact.getWorldManifold().getPoints()[0].x*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,contact.getWorldManifold().getPoints()[0].y*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
				} else if (impulse.getNormalImpulses()[0] > (totalHealth/2) && contact.getFixtureB().getBody().equals(mBody)) {
					//Debug.w("DEBUG: Impact Detected (" + impulse.getNormalImpulses()[0] + ")");
					Explode(contact.getWorldManifold().getPoints()[0].x*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,contact.getWorldManifold().getPoints()[0].y*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
				}
			}			
			@Override
			public void endContact(Contact contact) {
				if (contact.getFixtureA().getBody().equals(mBody) && contact.getFixtureA().isSensor()){
					if (baseContacts.contains(contact.getFixtureB().getBody())){
						baseContacts.remove(contact.getFixtureB().getBody());
					}
				}
				else if (contact.getFixtureB().getBody().equals(mBody) && contact.getFixtureB().isSensor()){					
					if (baseContacts.contains(contact.getFixtureA().getBody())){
						baseContacts.remove(contact.getFixtureA().getBody());
					}
				}
			}
			public void beginContact(Contact contact) {				
				if (contact.getFixtureA().getBody().equals(mBody) && contact.getFixtureA().isSensor()){
					if (!cargoConnected && contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals("CargoAttachment")){						
						cargoBody = contact.getFixtureB().getBody();
						Cargo cargo = (Cargo) cargoBody.getUserData();
						if (cargo.isAttachable()) attachCargo();
					}
					if (!contact.getFixtureB().isSensor() && !baseContacts.contains(contact.getFixtureB().getBody())){
						baseContacts.add(contact.getFixtureB().getBody());
					}
				}
				else if (contact.getFixtureB().getBody().equals(mBody) && contact.getFixtureB().isSensor()){
					if (!cargoConnected && contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals("CargoAttachment")){
						cargoBody = contact.getFixtureA().getBody();
						Cargo cargo = (Cargo) cargoBody.getUserData();
						if (cargo.isAttachable()) attachCargo();
					}
					if (!contact.getFixtureA().isSensor() && !baseContacts.contains(contact.getFixtureA().getBody())){
						baseContacts.add(contact.getFixtureA().getBody());
					}
				}
			}
		});
		
		NoFlyListenerManager.getInstance().addListener(new INoFlyListener() {			
			@Override
			public void onExit(Body body) {
				noFlyZoneCount--;
			}
			
			@Override
			public void onEnter(Body body) {
				noFlyZoneCount++;
			}
		});
				
		Resources.mEngine.registerUpdateHandler(new IUpdateHandler() {
			float counter = 0.0f;
			
			@Override
			public void reset() {
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				if (paused) {
					if (Resources.RocketEngine0.isPlaying()) Resources.RocketEngine0.pause();
					return;
				}
				
				if (destroyed || !enginesOn) {
					stopExhaust();
					stopTakeoffEmitters();
					if (Resources.RocketEngine0.isPlaying()) Resources.RocketEngine0.pause();
					return;
				}
				
				if (healthRemaining < (totalHealth * .25)){
					showDamageCritical();
				}
                else{
                    clearDamageCritical();
                }

                if (refueling && getCurrentFuelPercentage() >= 1){
                    stopRefueling();
                    Resources.Ding.play();
                    listener.onRefuelComplete();
                }
                if (refueling && getCurrentFuelPercentage() < 1){
                    currentFuel += ((refuelRate * info.getFuelCapacity()) * pSecondsElapsed);
                }

                if (repairing && getCurrentHealthPercentage() >= 1){
                    stopRepairing();
                    Resources.Ding.play();
                    listener.onRepairComplete();
                }
                if (repairing && getCurrentHealthPercentage() < 1){
                    healthRemaining += ((repairRate * totalHealth) * pSecondsElapsed);
                }

				float mainEngineThrust = 0f;
				float bodyAngle = mBody.getAngle();
				airborn = (baseContacts.size() == 0);
				
				if (currentFuel > 0f){								
					mainEngineThrust = info.getEngineThrust() * currentThrottle;
					
					if (Math.abs(bodyAngle) < .1f) bodyAngle = 0;
					float bodyAngleSin = (float) Math.sin(bodyAngle); 
					float bodyAngleCos = (float) Math.cos(bodyAngle); 
					Vector2 mainEngineVector = new Vector2(mainEngineThrust * (bodyAngleSin), 0 - mainEngineThrust * bodyAngleCos);

					if (noFlyZoneCount > 0){
						mainEngineThrust = 0;
						if (mBody.getLinearVelocity().y < 0){
							mBody.applyLinearImpulse(new Vector2(0f, mainEngineVector.y * -.05f), mBody.getWorldCenter());
						}
					}
					else{						
						mBody.applyForce(mainEngineVector, mBody.getWorldCenter());	//main engine
						//DrawForceDebugLine(mBody.getWorldCenter(), mainEngineVector);
						
						currentFuel -= (currentThrottle * info.getFuelPerSecond()) * pSecondsElapsed;
					}
				}
				else{
					currentFuel = 0f;
				}
				
				float eX, eY;
				if (mainEngineThrust > 0){
					if (Resources.RocketEngine0.isLoaded())	{
						Resources.RocketEngine0.setVolume(currentThrottle);
						Resources.RocketEngine0.setRate(.5f + currentThrottle);
						if (Resources.RocketEngine0.isPaused()) Resources.RocketEngine0.resume();
					}
					
					
					if (airborn){	
						if (!takeOffComplete) {
							if (listener != null) listener.onTakeOff(Lander.this, null); //TODO
							takeOffComplete = true;
						}
						
						if (mBody.getLinearVelocity().x < -2f && direction > -1){
							direction = -1;
							float angle = mBody.getAngle();
							float angularVelocity = mBody.getAngularVelocity();
							Vector2 linearVelocty = mBody.getLinearVelocity();
							Body tempCargo = null;
							if (cargoConnected) {
								tempCargo = cargoBody;
								detachCargo();
							}
							
							Lander.this.setFlippedHorizontal(true);
							
							mBody.setTransform(mBody.getPosition(), angle);
							mBody.setAngularVelocity(angularVelocity);
							mBody.setLinearVelocity(linearVelocty);							
							if (!cargoConnected && tempCargo != null) {
								cargoBody = tempCargo;
								Cargo cargo = (Cargo) cargoBody.getUserData();
								if (cargo.isAttachable()) attachCargo();
							}
						}
						else if (mBody.getLinearVelocity().x > 2f && direction < 0){
							direction = 1;
							float angle = mBody.getAngle();
							float angularVelocity = mBody.getAngularVelocity();
							Vector2 linearVelocty = mBody.getLinearVelocity();
							Body tempCargo = null;
							if (cargoConnected) {
								tempCargo = cargoBody;
								detachCargo();
							}
							
							Lander.this.setFlippedHorizontal(false);
							
							mBody.setTransform(mBody.getPosition(), angle);
							mBody.setAngularVelocity(angularVelocity);
							mBody.setLinearVelocity(linearVelocty);							
							if (!cargoConnected && tempCargo != null) {
								cargoBody = tempCargo;
								Cargo cargo = (Cargo) cargoBody.getUserData();
								if (cargo.isAttachable()) attachCargo();
							}
						}

                        double degreesToGo = desiredAngle - (Math.toDegrees(bodyAngle) % 360);
                        if (degreesToGo > 180){ //dont flip over to get to correct angle
                            //Debug.w("Tried to Flip Over! desiredAngle: " + desiredAngle + ", currentAngle: " + (Math.toDegrees(bodyAngle) % 360) + ", resulting in degreesToGo: " + degreesToGo);
                            degreesToGo -= 360f;
                        } else if (degreesToGo < -180){ //dont flip over to get to correct angle
                            //Debug.w("Tried to Flip Over! desiredAngle: " + desiredAngle + ", currentAngle: " + (Math.toDegrees(bodyAngle) % 360) + ", resulting in degreesToGo: " + degreesToGo);
                            degreesToGo += 360f;
                        }
                        mBody.setAngularVelocity((float) (1f * (degreesToGo / 20f))); //TODo
												
						for (int x=0; x<getExhaustPoints().size(); x++){
							Vector2 bodyPoint = new Vector2(getExhaustPoints().get(x).x/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,getExhaustPoints().get(x).y/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
							eX = mBody.getWorldPoint(bodyPoint).x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
							eY = mBody.getWorldPoint(bodyPoint).y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;	
							exhaustEmitters.get(x).setPosition(eX, eY);
							
							exhaustEmitters.get(x).Start();
							stopTakeoffEmitters();
						}					
					}
					else{
						takeOffComplete = false;
						
						for (int x=0; x<getExhaustPoints().size(); x++){
							Vector2 bodyPoint = new Vector2(getExhaustPoints().get(x).x/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,getExhaustPoints().get(x).y/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
							eX = mBody.getWorldPoint(bodyPoint).x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
							eY = mBody.getWorldPoint(bodyPoint).y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;	
							
							takeoffEmitters.get(x).setPosition(eX, eY);
							takeoffEmitters.get(x).Start();
						}						
					}
										
					mThis.setCurrentTileIndex(1);
					
					if (cameraFocused) camera.setZoomFactor(maxZoomOut);					
				}
				else{
					if (Resources.RocketEngine0.isPlaying()) Resources.RocketEngine0.pause();
					
					stopExhaust();
					stopTakeoffEmitters();
					
					mThis.setCurrentTileIndex(0);
					
					if (cameraFocused) camera.setZoomFactor(maxZoomIn);
				}					
			}
		});
		
		explosion = new AnimatedSprite(0, 0, Resources.ExplosionSequence, Resources.mEngine.getVertexBufferObjectManager());
		explosion.setScale(2);		
	}

	protected void showDamageTaken() {
		ColorModifier damageCriticalColorModifier1 = new ColorModifier(.15f, Color.WHITE, Color.RED, new IEntityModifierListener() {			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
			}			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
			}
		});
		ColorModifier damageCriticalColorModifier2 = new ColorModifier(.15f, Color.RED, Color.WHITE, new IEntityModifierListener() {			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
			}			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
			}
		});
		SequenceEntityModifier seq = new SequenceEntityModifier(damageCriticalColorModifier1, damageCriticalColorModifier2);		
		this.registerEntityModifier(seq);		
	}
	
	protected void showDamageCritical() {
		if (damageCriticalLooper == null){
            ColorModifier damageCriticalColorModifier1 = new ColorModifier(.15f, Color.WHITE, Color.RED, new IEntityModifierListener() {
                @Override
                public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                }
                @Override
                public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                }
            });
            ColorModifier damageCriticalColorModifier2 = new ColorModifier(.15f, Color.RED, Color.WHITE, new IEntityModifierListener() {
                @Override
                public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                }
                @Override
                public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                }
            });
            SequenceEntityModifier seq = new SequenceEntityModifier(damageCriticalColorModifier1, damageCriticalColorModifier2);
            damageCriticalLooper = new LoopEntityModifier(seq);
            this.registerEntityModifier(damageCriticalLooper);
        }
	}

    protected void clearDamageCritical(){
        this.unregisterEntityModifier(damageCriticalLooper);
        damageCriticalLooper = null;
        this.setColor(Color.WHITE);
    }

	protected void attachCargo() {
		Resources.mEngine.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				if (!cargoConnected){
					createRope(mBody.getWorldCenter().x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 
									mBody.getWorldCenter().y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 
									cargoBody.getWorldCenter().x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 
									cargoBody.getWorldCenter().y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
					
					RopeJointDef ropeJointDef = new RopeJointDef();
					ropeJointDef.bodyA = mBody;
					ropeJointDef.bodyB = cargoBody;
					ropeJointDef.localAnchorA.set(new Vector2(0,0));
					ropeJointDef.localAnchorB.set(Util.getBodyPoint((Sprite)cargoBody.getUserData(), new Vector2(((Sprite)cargoBody.getUserData()).getWidth()/2,0)));
					ropeJointDef.maxLength = 2;				
					ropeJointDef.collideConnected = true;
					ropeJoint = (RopeJoint) Resources.mPhysicsWorld.createJoint(ropeJointDef);

					cargoConnected = true;
				}
			}
		});	
	}
	
	protected void detachCargo(){
		if (cargoConnected){
			//Debug.w("DEBUG: Detaching Cargo");
			cargoConnected = false;
			Resources.mPhysicsWorld.destroyJoint(ropeJoint);
			ropeJoint = null;
			cargoBody = null;
			//Debug.w("DEBUG: Joint Destroyed");
			Resources.mEngine.runOnUpdateThread(new Runnable() {						
				@Override
				public void run() {	
					if (!cargoConnected){
						for (Line ropeLine: ropeLines){
							ropeLine.detachSelf();
							//Debug.w("DEBUG: Rope Line Detached");
						}
						//Debug.w("DEBUG: Cargo Detached");
					}
				}
			});		
		}
	}
	
	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		if (cargoConnected){
			//Debug.w("DEBUG: Setting Rope Position");
			setRopePosition(mBody.getWorldCenter().x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 
					mBody.getWorldCenter().y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 
					cargoBody.getWorldCenter().x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 
					cargoBody.getWorldCenter().y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);	
			//Debug.w("DEBUG: Rope Position Set");
		}
		
		super.onManagedUpdate(pSecondsElapsed);
	};
	
	protected abstract List<Vector2> getExhaustPoints();
	
	private void createExhaustEmitters() {
		for (Vector2 exhaustPoint: getExhaustPoints()){
			ExhaustEmitter emitter = new ExhaustEmitter(exhaustPoint.x, exhaustPoint.y);
			exhaustEmitters.add(emitter);			
		}		
	}

	private void createTakeoffEmitters() {
		for (Vector2 exhaustPoint: getExhaustPoints()){
			TakeoffEmitter emitter = new TakeoffEmitter(exhaustPoint.x, exhaustPoint.y);
			takeoffEmitters.add(emitter);			
		}		
	}
	
	private void createLanderDebris(final float x, final float y){
		CircleParticleEmitter particleEmitter = new CircleParticleEmitter(x,y, 10);
		SpriteParticleSystem smokeParticleSystem = new SpriteParticleSystem(particleEmitter, 60, 360, 360, Resources.smokeParticle, Resources.mEngine.getVertexBufferObjectManager());
		
		smokeParticleSystem.addParticleInitializer(new AlphaParticleInitializer<Sprite>(0));
		smokeParticleSystem.addParticleInitializer(new RotationParticleInitializer<Sprite>(0.0f, 360.0f));
		smokeParticleSystem.addParticleInitializer(new ExpireParticleInitializer<Sprite>(1.5f));

		smokeParticleSystem.addParticleModifier(new ScaleParticleModifier<Sprite>(0, 1.5f, 1.0f, 3.0f));
		smokeParticleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(0, .1f, 0, 1));
		smokeParticleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(.5f, 1.5f, 1, 0));		
		smokeParticleSystem.addParticleInitializer(new VelocityParticleInitializer<Sprite>(-10, 10, -50, -100));
				
		Resources.mEngine.getScene().attachChild(smokeParticleSystem);
		
		Resources.mEngine.runOnUpdateThread(new Runnable() {			
			@Override
			public void run() {
				FixtureDef fixtureDef = new FixtureDef();
				
				fixtureDef.density = .1f;
				fixtureDef.friction = 0.75f;
				fixtureDef.restitution = 0.15f;
				
				for (TextureRegion tex: Resources.landerDebris){
					Sprite LanderDebrisSprite = new Sprite(mBody.getWorldCenter().x*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, mBody.getWorldCenter().y*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, tex, Resources.mEngine.getVertexBufferObjectManager());
					
					Body body = Util.CreateBody(LanderDebrisSprite, fixtureDef, BodyType.DynamicBody, BodyShape.Box);
					LanderDebrisSprite.setUserData(body);
					Resources.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(LanderDebrisSprite, body));
					sLanderDebris.add(LanderDebrisSprite);
					
					Resources.mEngine.getScene().attachChild(LanderDebrisSprite);
					applyBlastImpulse(body, mBody.getWorldCenter(), body.getWorldPoint(new Vector2(LanderDebrisSprite.getWidth()*.6f/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, LanderDebrisSprite.getHeight()*.6f/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT)), .5f);
				}
				
				Resources.mPhysicsWorld.unregisterPhysicsConnector(Resources.mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(mThis));
				Resources.mPhysicsWorld.destroyBody(mBody);
				mThis.detachSelf();				
				stopExhaust();
			}
		});	
	}
		
	public void setAngle(float angle){
		desiredAngle = (360f + angle) % 360f;
	}
	
	public void setThrottle(float throttle){
		currentThrottle = throttle;
	}
	
	public void FocusCamera(){		
		camera.setChaseEntity(mThis);		
		((SmoothCamera) Resources.mEngine.getCamera()).updateChaseEntity();
		camera.setZoomFactor(maxZoomIn);
		
		cameraFocused = true;
	}
	
	public void FocusCameraDirect(){
		final float[] centerCoordinates = mThis.getSceneCenterCoordinates();
		((SmoothCamera) Resources.mEngine.getCamera()).setCenterDirect(centerCoordinates[Constants.VERTEX_INDEX_X], centerCoordinates[Constants.VERTEX_INDEX_Y]);		
		camera.setZoomFactor(maxZoomIn);
		camera.setChaseEntity(mThis);
		
		cameraFocused = true;
	}

	public void BlurCamera(){
		camera.setChaseEntity(null);
		camera.updateChaseEntity();
		camera.setZoomFactor(1f);

		cameraFocused = false;
	}

	public float getCurrentFuelPercentage() {
		return currentFuel / info.getFuelCapacity();
	}

	public void Explode(final float contactX, final float contactY){	
		if (exploding) return;
				
		//Debug.w("Exploding at " + landerSprite.getX() + ", " + landerSprite.getY());
		
		camera.setZoomFactor(1f);
		
		explosion.setPosition(contactX - explosion.getWidth()/2, contactY - explosion.getHeight()/2);
		explosion.animate(new long[] {100,100,100,100,1000},0,4,true, new IAnimationListener() {			
			@Override
			public void onAnimationStarted(AnimatedSprite pAnimatedSprite,	int pInitialLoopCount) {
			}			
			@Override
			public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,	int pRemainingLoopCount, int pInitialLoopCount) {				
			}			
			@Override
			public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,	int pOldFrameIndex, int pNewFrameIndex) {
				//Debug.w("New Animation Frame: " + pNewFrameIndex + " of " + (pAnimatedSprite.getTileCount() -1));
				if (pNewFrameIndex == pAnimatedSprite.getTileCount() -1){
					//last frame
					AlphaModifier alpha = new AlphaModifier(1f, 1, 0, new IEntityModifierListener() {						
						@Override
						public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
						}						
						@Override
						public void onModifierFinished(IModifier<IEntity> pModifier, final IEntity pItem) {
							Resources.mEngine.runOnUpdateThread(new Runnable() {								
								@Override
								public void run() {
									pItem.detachSelf();
									exploding = false;
									//Debug.w("Explosion faded");
								}
							});
						}
					});
					pAnimatedSprite.registerEntityModifier(alpha);
					//Debug.w("Fading explosion");
				}
			}			
			@Override
			public void onAnimationFinished(final AnimatedSprite pAnimatedSprite) {
			}
		});
		explosion.setAlpha(1f);
		Resources.mEngine.getScene().attachChild(explosion);
		exploding = true;
		//Debug.w("Explosion created");
		destroyed = true;
		
		Resources.LanderExplosion.play();
		createLanderDebris(contactX, contactY);		
	}
	
	public void startEngines(){
		enginesOn = true;
	}
	
	public void stopEngines(){
		enginesOn = false;
	}
		
	void applyBlastImpulse(Body body, Vector2 blastCenter, Vector2 applyPoint, float blastPower) {
		Vector2 blastDir = applyPoint.sub(blastCenter);
	      float distance = blastDir.len();
	      //ignore bodies exactly at the blast point - blast direction is undefined
	      if ( distance == 0 ){	    	  
	          return;
	      }
	      float invDistance = 1 / distance;
	      float impulseMag = blastPower * invDistance * invDistance;
	      body.applyLinearImpulse(blastDir.mul(impulseMag), applyPoint);
	  }
	
	private void DrawForceDebugLine(Vector2 forceOrigin, Vector2 forceVector) {		
		
		float x1 = forceOrigin.x*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		float y1 = forceOrigin.y*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		float x2 = x1 + (x1 - (forceOrigin.x*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT + forceVector.x *PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT)) ;
		float y2 = y1 + (y1 - (forceOrigin.y*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT + forceVector.y *PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));
		Line debugThrustLine = new Line(x1,y1,x2,y2, Resources.mEngine.getVertexBufferObjectManager());
		AlphaModifier alpha = new AlphaModifier(.25f, 1f, 0f, new IEntityModifierListener() {			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
			}			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, final IEntity pItem) {
				Resources.mEngine.runOnUpdateThread(new Runnable() {					
					@Override
					public void run() {
						pItem.detachSelf();	
					}
				});				
			}
		});
		debugThrustLine.registerEntityModifier(alpha);
		
		debugThrustLine.setPosition(x1, y1, x2, y2);					
		debugThrustLine.setColor(Color.RED);
		debugThrustLine.setLineWidth(2f);
		
		Resources.mEngine.getScene().attachChild(debugThrustLine);
	}

	private void stopExhaust() {
		for (ExhaustEmitter ex: exhaustEmitters){
			ex.Stop();
		}
	}
	private void stopTakeoffEmitters() {
		for (TakeoffEmitter ex: takeoffEmitters){
			ex.Stop();
		}
	}
	
	public void setPaused(boolean paused){
		this.paused = paused; 
	}

	private void setRopePosition(float x1, float y1, float x2, float y2){		
		if (ropeLines == null || ropeLines.size() < 3) return;
		
		for(int x=0;x<3;x++){
			Line ropeLine = ropeLines.get(x);
			if (ropeLine != null){
				ropeLine.setPosition((x1)+(x-1), y1, (x2)+(x-1), y2);
			}
		}
	}
	private void createRope(float x1, float y1, float x2, float y2) {
		for (Line ropeLine: ropeLines){
			ropeLine.detachSelf();
			//Debug.w("DEBUG: Rope Line Detached");
		}
		ropeLines.clear();
		
		//Debug.w("DEBUG: Creating Rope");
		for (int x=-1;x<2;x++){
			Line ropeLine = new Line(
					(x1)+x, 
					y1, 
					(x2)+x, 
					y2, 
					Resources.mEngine.getVertexBufferObjectManager()
			);
			if (x == 0){
				ropeLine.setColor(new Color(.25f,.25f,.25f,1f));
			}
			else{
				ropeLine.setColor(Color.BLACK);
			}
			ropeLine.setZIndex(Lander.this.getZIndex()-1);
			ropeLine.setLineWidth(1f);	
			Resources.mEngine.getScene().attachChild(ropeLine);
			Resources.mEngine.getScene().sortChildren();
			
			ropeLines.add(ropeLine);
		}
		//Debug.w("DEBUG: Rope Created");
	}	

	public void registerListener(ILanderListener listener){
		this.listener = listener;
	}

	public float getCurrentHealthPercentage() {
		return healthRemaining / totalHealth;
	}

    public void startRefueling() {
        refueling = true;
    }

    public void stopRefueling() {
        refueling = false;
    }

    public void startRepairing() {
        repairing = true;
    }

    public void stopRepairing() {
        repairing = false;
    }
}
