package com.singletongames.vtol;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.singletongames.vtol.objectives.ObjectiveZone;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.util.modifier.IModifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Troy on 1/1/15.
 */
public class RefuelPad extends PhysicsSprite {
    int id = -1;
    List<IRefuelPadListener> refuelPadListeners = new ArrayList<IRefuelPadListener>();
    final RefuelPad self = this;
    private TimerHandler fuelingTimer;
    LanderScene mScene;

    public int getId() {
        return id;
    }

    public RefuelPad(LanderScene scene, float pX, float pY, IRefuelPadListener listener) {
        this(scene, pX, pY, -1, listener);
    }

    public RefuelPad(LanderScene scene, float pX, float pY, int id, IRefuelPadListener listener) {
        super(pX, pY, Resources.refuelPad, PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "RefuelPad", BodyDef.BodyType.StaticBody, getVertices(), null);
        this.id = id;
        mScene = scene;

        IPhysicsSpriteListener l = new IPhysicsSpriteListener() {
            @Override
            public void onContact(Fixture fixtureA, final Fixture fixtureB) {
                if (fixtureA.getUserData() != null && fixtureA.getUserData().toString().toUpperCase().equals("REFUELPAD") &&
                        fixtureB.getUserData() != null && fixtureB.getUserData().toString().toUpperCase().equals("LANDERBASE")){
                    for (final IRefuelPadListener listener: refuelPadListeners){
                        listener.onRefuelLanding(self);
                        startFueling();
                    }
                }
            }
        };

        GameContactListener.getInstance().registerContactListener(new ContactListener() {
            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }
            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
            @Override
            public void endContact(Contact contact) {
                if (contact.getFixtureA().getBody().equals(mBody) &&
                        contact.getFixtureB().getUserData() != null &&
                        contact.getFixtureB().getUserData().toString().toUpperCase().equals("LANDERBASE")){
                    for (final IRefuelPadListener listener: refuelPadListeners){
                        listener.onRefuelTakeoff(self);
                        stopFueling();
                    }
                }
                else if (contact.getFixtureB().getBody().equals(mBody) &&
                        contact.getFixtureA().getUserData() != null &&
                        contact.getFixtureA().getUserData().toString().toUpperCase().equals("LANDERBASE")){
                    for (final IRefuelPadListener listener: refuelPadListeners){
                        listener.onRefuelTakeoff(self);
                        stopFueling();
                    }
                }
            }
            public void beginContact(Contact contact) {

            }
        });

        //adds the listener that will listen for this refueling pad to register a landing
        if (listener != null) this.refuelPadListeners.add(listener);

        //adds the physics sprite listener that will look for landings (above) and broadcast when it happens
        this.listeners.add(l);

        scene.addListener(new ILanderSceneListener() {
            @Override
            public void onThrottleChange(float currentThrottle) {

            }

            @Override
            public void onPreviewComplete() {

            }

            @Override
            public void onPreviewStart() {

            }

            @Override
            public void onCameraLookComplete() {

            }

            @Override
            public void onLanderTakeoff() {

            }

            @Override
            public void onLanderTouchdown() {

            }

            @Override
            public void onLanderRefuelComplete() {
                stopFueling();
            }

            @Override
            public void onLanderRepairComplete() {

            }

            @Override
            public void onSafeLanding(LandingPad pad) {

            }

            @Override
            public void onSafeReturn() {

            }

            @Override
            public void onLanderDestroy() {

            }

            @Override
            public void onMissionFail() {

            }

            @Override
            public void onMissionSuccess() {

            }

            @Override
            public void onObjectiveComplete() {

            }

            @Override
            public void onObjectiveZoneEnter(ObjectiveZone objectiveZone) {

            }

            @Override
            public void onObjectiveZoneExit(ObjectiveZone objectiveZone) {

            }

            @Override
            public void onNoFlyEnter() {

            }

            @Override
            public void onNoFlyExit() {

            }

            @Override
            public void onCargoPickup() {

            }

            @Override
            public void onCargoDeliver(Cargo cargo, CargoDrop drop) {

            }
        });
    }

    public void addListener(IRefuelPadListener listener){
        if (listener != null) this.refuelPadListeners.add(listener);
    }

    public void startFueling(){
        fuelingTimer = new TimerHandler(1f, true, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                Sprite fuelIcon = new Sprite(370,40,Resources.FuelIcon, Resources.mEngine.getVertexBufferObjectManager());
                fuelIcon.setAlpha(0);
                fuelIcon.setScale(.5f);
                MoveModifier mover = new MoveModifier(1.5f,fuelIcon.getX(),fuelIcon.getX(), fuelIcon.getY(), fuelIcon.getY()-50f);
                AlphaModifier alpha1 = new AlphaModifier(.75f,0,1);
                AlphaModifier alpha2 = new AlphaModifier(.75f,1,0);
                ScaleModifier scale1 = new ScaleModifier(.75f, .5f, 1.5f);
                ScaleModifier scale2 = new ScaleModifier(.75f, 1.5f, .5f);
                SequenceEntityModifier seq1 = new SequenceEntityModifier(new IEntityModifier[]{alpha1,alpha2});
                SequenceEntityModifier seq2 = new SequenceEntityModifier(new IEntityModifier[]{scale1,scale2});
                ParallelEntityModifier par = new ParallelEntityModifier(new IEntityModifier.IEntityModifierListener() {
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
                }, new IEntityModifier[]{mover, seq1, seq2});
                fuelIcon.registerEntityModifier(par);
                self.attachChild(fuelIcon);
                Resources.Glug.play();
            }
        });
        Resources.mEngine.registerUpdateHandler(fuelingTimer);
    }

    public void stopFueling(){
        Resources.mEngine.unregisterUpdateHandler(fuelingTimer);
    }

    private static Vector2[] getVertices() {
        Vector2[] vertices = new Vector2[4];
        vertices[0] = Util.getBodyPoint(Resources.refuelPad, new Vector2(122, 80));
        vertices[1] = Util.getBodyPoint(Resources.refuelPad, new Vector2(403, 80));
        vertices[2] = Util.getBodyPoint(Resources.refuelPad, new Vector2(403, 120));
        vertices[3] = Util.getBodyPoint(Resources.refuelPad, new Vector2(122, 120));

        return vertices;
    }

}
