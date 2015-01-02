package com.singletongames.vtol;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Troy on 1/1/15.
 */
public class RefuelPad extends PhysicsSprite {
    int id = -1;
    List<IRefuelPadListener> refuelPadListeners = new ArrayList<IRefuelPadListener>();

    public int getId() {
        return id;
    }

    public RefuelPad(float pX, float pY, IRefuelPadListener listener) {
        this(pX, pY, -1, listener);
    }

    public RefuelPad(float pX, float pY, int id, IRefuelPadListener listener) {
        super(pX, pY, Resources.refuelPad, PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "RefuelPad", BodyDef.BodyType.StaticBody, getVertices(), null);
        this.id = id;

        IPhysicsSpriteListener l = new IPhysicsSpriteListener() {
            @Override
            public void onContact(Fixture fixtureA, final Fixture fixtureB) {
                if (fixtureA.getUserData() != null && fixtureA.getUserData().toString().toUpperCase().equals("REFUELPAD") &&
                        fixtureB.getUserData() != null && fixtureB.getUserData().toString().toUpperCase().equals("LANDERBASE")){
                    for (final IRefuelPadListener listener: refuelPadListeners){
                        listener.onRefuelLanding();
                    }
                    Debug.d("Refuel Landing");
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
                        listener.onRefuelTakeoff();
                        Debug.d("Refuel Takeoff");
                    }
                }
                else if (contact.getFixtureB().getBody().equals(mBody) &&
                        contact.getFixtureA().getUserData() != null &&
                        contact.getFixtureA().getUserData().toString().toUpperCase().equals("LANDERBASE")){
                    for (final IRefuelPadListener listener: refuelPadListeners){
                        listener.onRefuelTakeoff();
                        Debug.d("Refuel Takeoff");
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
    }

    public void addListener(IRefuelPadListener listener){
        if (listener != null) this.refuelPadListeners.add(listener);
    }

    private static Vector2[] getVertices() {
        Vector2[] vertices = new Vector2[4];
        vertices[0] = Util.getBodyPoint(Resources.refuelPad, new Vector2(16, 90));
        vertices[1] = Util.getBodyPoint(Resources.refuelPad, new Vector2(184, 90));
        vertices[2] = Util.getBodyPoint(Resources.refuelPad, new Vector2(188, 100));
        vertices[3] = Util.getBodyPoint(Resources.refuelPad, new Vector2(12, 100));

        return vertices;
    }

}
