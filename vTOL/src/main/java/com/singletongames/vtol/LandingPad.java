package com.singletongames.vtol;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class LandingPad extends PhysicsSprite {
	int id = -1;
	
	public int getId() {
		return id;
	}

	public LandingPad(Scene scene, float pX, float pY, IPhysicsSpriteListener listener) {
		this(scene, pX, pY, -1, listener);
	}
	
	public LandingPad(Scene scene, float pX, float pY, int id, IPhysicsSpriteListener listener) {
		super(pX, pY, Resources.LandingPad, PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "LandingPad", BodyType.StaticBody, getVertices(), listener);
		this.id = id;

        Sprite rail = new Sprite(pX + 100, pY + 70, Resources.LandingPadRail, Resources.mEngine.getVertexBufferObjectManager());
        rail.setZIndex(30);
        scene.attachChild(rail);
	}

	private static Vector2[] getVertices() {
		Vector2[] vertices = new Vector2[6];
		vertices[0] = Util.getBodyPoint(Resources.LandingPad, new Vector2(72, 116));
		vertices[1] = Util.getBodyPoint(Resources.LandingPad, new Vector2(72, 96));
		vertices[2] = Util.getBodyPoint(Resources.LandingPad, new Vector2(92, 83));
		vertices[3] = Util.getBodyPoint(Resources.LandingPad, new Vector2(364, 83));
        vertices[4] = Util.getBodyPoint(Resources.LandingPad, new Vector2(384, 96));
        vertices[5] = Util.getBodyPoint(Resources.LandingPad, new Vector2(384, 116));
		
		return vertices;
	}
	
}
