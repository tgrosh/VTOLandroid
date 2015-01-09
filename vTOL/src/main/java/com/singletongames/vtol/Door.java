package com.singletongames.vtol;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.shape.Shape;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Troy on 1/5/15.
 */
public class Door extends Entity {

    private final List<IDoorListener> listeners = new ArrayList<IDoorListener>();
    private PhysicsSprite doorTop;
    private PhysicsSprite doorBottom;
    private ITextureRegion doorBottomTexture;
    private ITextureRegion doorTopTexture;
    private float doorTopPosition;
    private float doorBottomPosition;
    private float doorOpenDuration = 3f; //time (sec) it takes to open the door

    private final float mHeight;

    private Door self = this;
    private boolean opening;
    private IUpdateHandler doorOpeningHandler;

    public Door(float x, float y, float height){
        this(x, y, height, null);
    }

    public Door(float x, float y, float height, IDoorListener listener) {
        mHeight = height;
        if (listener != null) listeners.add(listener);

        this.setX(x);
        this.setY(y);

        doorBottomTexture = getDoorBottomTexture(height);
        doorTopTexture = getDoorTopTexture(height);

        doorBottomPosition = y-doorBottomTexture.getHeight();
        doorTopPosition = y-height + (Resources.DoorBase.getHeight()*2);

        doorTop = new PhysicsSprite(x + Resources.DoorBase.getWidth()/2 - doorTopTexture.getWidth()/2, doorTopPosition, doorTopTexture, PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "DOOR_TOP", BodyType.KinematicBody, Util.BodyShape.Box, null);
        doorBottom = new PhysicsSprite(x+ Resources.DoorBase.getWidth()/2 - doorBottomTexture.getWidth()/2, doorBottomPosition, doorBottomTexture, PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "DOOR_BOTTOM", BodyType.KinematicBody, Util.BodyShape.Box, null);

        PhysicsSprite doorBaseTop = new PhysicsSprite(x, y-height + Resources.DoorBase.getHeight(), Resources.DoorBase, PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "DOORBASE_TOP", BodyType.StaticBody, getBaseVertices(), null);
        PhysicsSprite doorBaseBottom = new PhysicsSprite(x, y, Resources.DoorBase, PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "DOORBASE_BOTTOM", BodyType.StaticBody, getBaseVertices(), null);
        doorBaseBottom.setFlippedVertical(true);

        Resources.mEngine.getScene().attachChild(doorBaseBottom);
        Resources.mEngine.getScene().attachChild(doorBaseTop);
        Resources.mEngine.getScene().attachChild(doorTop);
        Resources.mEngine.getScene().attachChild(doorBottom);
    }

    private Vector2[] getBaseVertices() {
        Vector2[] vertices = new Vector2[4];
        vertices[0] = Util.getBodyPoint(Resources.DoorBase, new Vector2(0, 0));
        vertices[1] = Util.getBodyPoint(Resources.DoorBase, new Vector2(214, 0));
        vertices[2] = Util.getBodyPoint(Resources.DoorBase, new Vector2(162, 64));
        vertices[3] = Util.getBodyPoint(Resources.DoorBase, new Vector2(52, 64));

        return vertices;
    }

    private static ITextureRegion getDoorBottomTexture(float height) {
        float doorBottomHeight = ((height - (Resources.DoorBase.getHeight()*2)) / 2) + 12;

        return getTextureRegion(Resources.DoorBottom, doorBottomHeight, 0);
    }

    private static ITextureRegion getDoorTopTexture(float height) {
        float texHeight = Resources.DoorTop.getHeight();
        float doorTopHeight = ((height - (Resources.DoorBase.getHeight()*2)) / 2) + 12;

        return getTextureRegion(Resources.DoorTop, doorTopHeight, texHeight - doorTopHeight);
    }

    private static ITextureRegion getTextureRegion(ITextureRegion region, float height, float positionY){
        ITextureRegion tex = region.deepCopy();
        tex.setTextureHeight(height);
        tex.setTexturePosition(0, positionY);

        return tex;
    }

    public void addListener(IDoorListener listener){
        listeners.add(listener);
    }

    public void Open(){
        if (!this.opening) {
            Resources.DoorOpening.play();
            for(IDoorListener listener: listeners){
                listener.onDoorOpening();
            }

            final float doorSpeed = doorBottom.getHeight() / doorOpenDuration;

            doorOpeningHandler = new IUpdateHandler() {
                @Override
                public void onUpdate(float pSecondsElapsed) {
                    float newHeight = doorBottom.getHeight() - (doorSpeed * pSecondsElapsed);
                    float newTopY = (mY-mHeight + (Resources.DoorBase.getHeight() * 2)) - (doorSpeed * pSecondsElapsed);
                    float newBottomY = doorBottom.getY() + (doorSpeed * pSecondsElapsed);
                    doorBottomTexture = getTextureRegion(Resources.DoorBottom, newHeight, 0);
                    doorTopTexture = getTextureRegion(Resources.DoorTop, newHeight, (Resources.DoorTop.getHeight() - newHeight));

                    doorBottom.destroy();
                    doorTop.destroy();

                    doorTop = new PhysicsSprite(mX+Resources.DoorBase.getWidth() / 2 - doorTopTexture.getWidth() / 2, newTopY, doorTopTexture, PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "DOOR_TOP", BodyType.KinematicBody, Util.BodyShape.Box, null);
                    doorBottom = new PhysicsSprite(mX+Resources.DoorBase.getWidth() / 2 - doorBottomTexture.getWidth() / 2, newBottomY, doorBottomTexture, PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "DOOR_BOTTOM", BodyType.KinematicBody, Util.BodyShape.Box, null);

                    Resources.mEngine.getScene().attachChild(doorTop);
                    Resources.mEngine.getScene().attachChild(doorBottom);

                    if (newHeight <= 0) {
                        Resources.DoorOpening.stop();
                        Resources.DoorOpened.play();
                        for(IDoorListener listener: listeners){
                            listener.onDoorOpened();
                        }
                        opening = false;
                        Resources.mEngine.unregisterUpdateHandler(doorOpeningHandler);
                    }
                }

                @Override
                public void reset() {

                }
            };
            Resources.mEngine.registerUpdateHandler(doorOpeningHandler);
        }

        this.opening = true;
    }
}
