package com.singletongames.vtol;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class BasicLander extends Lander {

	public BasicLander(float pX, float pY, ILanderListener listener) {
		super(pX, pY, LanderDB.getInstance().getLander(0), Resources.landerHauler, getFixtureDefs(Resources.landerHauler), getFixtureUserData(), listener);
	}

	private static List<FixtureDef> getFixtureDefs(TiledTextureRegion tex) {		
		List<FixtureDef> defs = new ArrayList<FixtureDef>();
		
		defs.add(Util.createPolygonFixtureDef(getBodyVertices(tex), PhysicsFactory.createFixtureDef(LanderDB.getInstance().getLander(0).density, .05f, .75f)));
		defs.add(Util.createPolygonFixtureDef(getBaseVertices(tex), PhysicsFactory.createFixtureDef(0f, 0f, 0f, true)));
				
		return defs;
	}

	@Override
	protected List<Vector2> getExhaustPoints() {
		List<Vector2> p = new ArrayList<Vector2>();
		p.add(new Vector2(-25f, 45f));
		p.add(new Vector2(11f, 45f));
		return p;
	}

	protected static ArrayList<Object> getFixtureUserData(){
		ArrayList<Object> result = new ArrayList<Object>();
		
		result.add(null);
		result.add("LanderBase");
		
		return result;
	}
	
	protected static Vector2[] getBodyVertices(TiledTextureRegion tex) {
		Vector2[] vertices = new Vector2[5];
		vertices[0] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(8f, 98f));
		vertices[1] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(8f, 32f));		
		vertices[2] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(97f, 32f));
		vertices[3] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(120f, 60f));
		vertices[4] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(97f, 98f));
		
		return vertices;
	}

	
	protected static Vector2[] getBaseVertices(TiledTextureRegion tex) {
		Vector2[] vertices = new Vector2[4];
		vertices[0] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(18f, 98f));
		vertices[1] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(18f, 60f));		
		vertices[2] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(87f, 60f));
		vertices[3] = Util.getBodyPoint(tex.getTextureRegion(0), new Vector2(87f, 98f));
		
		return vertices;
	}

}
