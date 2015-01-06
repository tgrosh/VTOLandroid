package com.singletongames.vtol;

import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.debug.Debug;

/**
 * Created by Troy on 1/5/15.
 */
public class Door extends PhysicsSprite {
    public Door(float pX, float pY, float clippingHeight) {
        super(pX, pY, getTexture(clippingHeight), PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "DOOR", null);
    }

    private static ITextureRegion getTexture(float height) {
        float texHeight = Resources.Doors.getHeight();
        ITextureRegion tex = Resources.Doors;
        tex.setTextureHeight(height);
        tex.setTexturePosition(0, (texHeight - height) / 2f);
        return tex;
    }
}
