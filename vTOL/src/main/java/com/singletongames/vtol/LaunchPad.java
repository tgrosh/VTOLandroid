package com.singletongames.vtol;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class LaunchPad extends PhysicsSprite {
	
	public LaunchPad(Scene scene, float pX, float pY, IPhysicsSpriteListener listener) {
		super(pX, pY, Resources.LaunchPad, PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "LaunchPad", BodyType.StaticBody, getVertices(), listener);

        Sprite pillars = new Sprite(pX + 36, pY + 64, Resources.LaunchPadPillars, Resources.mEngine.getVertexBufferObjectManager());
        pillars.setZIndex(30);
        scene.attachChild(pillars);
	}

	private static Vector2[] getVertices() {
		Vector2[] vertices = new Vector2[6];
		vertices[0] = Util.getBodyPoint(Resources.LaunchPad, new Vector2(37, 137));
		vertices[1] = Util.getBodyPoint(Resources.LaunchPad, new Vector2(37, 117));
		vertices[2] = Util.getBodyPoint(Resources.LaunchPad, new Vector2(63, 91));
		vertices[3] = Util.getBodyPoint(Resources.LaunchPad, new Vector2(322, 91));
        vertices[4] = Util.getBodyPoint(Resources.LaunchPad, new Vector2(348, 117));
        vertices[5] = Util.getBodyPoint(Resources.LaunchPad, new Vector2(348, 137));
		
		return vertices;
	}
	
}
