package com.singletongames.vtol;

import java.util.ArrayList;
import java.util.List;

import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class WoodenBox extends Cargo {

	public WoodenBox(float pX, float pY, int id) {		
		super(pX, pY, id, Resources.WoodenBox, getFixtureDefs(Resources.WoodenBox), getFixtureUserData());
	}

	private static List<Object> getFixtureUserData() {
		ArrayList<Object> result = new ArrayList<Object>();
		
		result.add(null);
		result.add("CargoAttachment");
		
		return result;
	}

	private static List<FixtureDef> getFixtureDefs(TextureRegion tex) {		
		List<FixtureDef> defs = new ArrayList<FixtureDef>();
		
		defs.add(Util.createPolygonFixtureDef(getBodyVertices(tex), PhysicsFactory.createFixtureDef(.025f, 0, .75f)));
		defs.add(Util.createPolygonFixtureDef(getCargoAttachmentVertices(tex), PhysicsFactory.createFixtureDef(0f, 0f, 0f, true)));
				
		return defs;
	}
	
	protected static Vector2[] getBodyVertices(TextureRegion tex) {
		Vector2[] vertices = new Vector2[4];
		vertices[0] = Util.getBodyPoint(tex, new Vector2(0, 0));
		vertices[1] = Util.getBodyPoint(tex, new Vector2(60, 0));		
		vertices[2] = Util.getBodyPoint(tex, new Vector2(60, 60));
		vertices[3] = Util.getBodyPoint(tex, new Vector2(0, 60f));
		
		return vertices;
	}

	
	protected static Vector2[] getCargoAttachmentVertices(TextureRegion tex) {
		Vector2[] vertices = new Vector2[4];
		vertices[0] = Util.getBodyPoint(tex, new Vector2(0f, 0f));
		vertices[1] = Util.getBodyPoint(tex, new Vector2(0, -60f));		
		vertices[2] = Util.getBodyPoint(tex, new Vector2(60, -60f));
		vertices[3] = Util.getBodyPoint(tex, new Vector2(60, 0));
		
		return vertices;
	}
}
