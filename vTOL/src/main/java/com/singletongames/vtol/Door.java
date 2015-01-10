package com.singletongames.vtol;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Troy on 1/5/15.
 */
public class Door extends Entity {

    private final List<IDoorListener> listeners = new ArrayList<IDoorListener>();
    private final PhysicsAnimatedSprite doorBaseBottom;
    private final PhysicsAnimatedSprite doorBaseTop;
    private PhysicsAnimatedSprite doorTop;
    private PhysicsAnimatedSprite doorBottom;
    private ITiledTextureRegion doorBottomTexture;
    private ITiledTextureRegion doorTopTexture;
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

        doorBaseTop = new PhysicsAnimatedSprite(x, y-height + Resources.DoorBase.getTextureRegion(0).getHeight(), Resources.DoorBase, PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "DOORBASE_TOP", BodyType.StaticBody, getBaseVertices(), null);
        doorBaseBottom = new PhysicsAnimatedSprite(x, y, Resources.DoorBase, PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "DOORBASE_BOTTOM", BodyType.StaticBody, getBaseVertices(), null);
        doorBaseBottom.setFlippedVertical(true);

        doorBottomPosition = y-doorBottomTexture.getHeight();
        doorTopPosition = y-height + (doorBaseBottom.getHeight()*2);

        doorTop = new PhysicsAnimatedSprite(x + doorBaseBottom.getWidth()/2 - doorTopTexture.getWidth()/2, doorTopPosition, doorTopTexture, PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "DOOR_TOP", BodyType.KinematicBody, Util.BodyShape.Box, null);
        doorBottom = new PhysicsAnimatedSprite(x+ doorBaseBottom.getWidth()/2 - doorBottomTexture.getWidth()/2, doorBottomPosition, doorBottomTexture, PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "DOOR_BOTTOM", BodyType.KinematicBody, Util.BodyShape.Box, null);

        Resources.mEngine.getScene().attachChild(doorBaseBottom);
        Resources.mEngine.getScene().attachChild(doorBaseTop);
        Resources.mEngine.getScene().attachChild(doorTop);
        Resources.mEngine.getScene().attachChild(doorBottom);
    }

    private Vector2[] getBaseVertices() {
        Vector2[] vertices = new Vector2[4];
        vertices[0] = Util.getBodyPoint(Resources.DoorBase.getTextureRegion(0), new Vector2(0, 0));
        vertices[1] = Util.getBodyPoint(Resources.DoorBase.getTextureRegion(0), new Vector2(214, 0));
        vertices[2] = Util.getBodyPoint(Resources.DoorBase.getTextureRegion(0), new Vector2(162, 30));
        vertices[3] = Util.getBodyPoint(Resources.DoorBase.getTextureRegion(0), new Vector2(52, 30));

        return vertices;
    }

    private static ITiledTextureRegion getDoorBottomTexture(float height) {
        float doorBottomHeight = ((height - (Resources.DoorBase.getTextureRegion(0).getHeight()*2)) / 2) + 12;

        return getTiledTextureRegion(Resources.DoorBottom, doorBottomHeight, 0);
    }

    private static ITiledTextureRegion getDoorTopTexture(float height) {
        float texHeight = Resources.DoorTop.getTextureRegion(0).getHeight();
        float doorTopHeight = ((height - (Resources.DoorBase.getTextureRegion(0).getHeight()*2)) / 2) + 12;

        return getTiledTextureRegion(Resources.DoorTop, doorTopHeight, texHeight - doorTopHeight);
    }

    private static ITiledTextureRegion getTiledTextureRegion(TiledTextureRegion region, float height, float positionY){
        TiledTextureRegion tiledTex = region.deepCopy();
        ITextureRegion tex0 = tiledTex.getTextureRegion(0);
        ITextureRegion tex1 = tiledTex.getTextureRegion(1);
        tex0.setTextureHeight(height);
        tex0.setTexturePosition(0, positionY);
        tex1.setTextureHeight(height);
        tex1.setTexturePosition(tex0.getWidth(), positionY);

        return tiledTex;
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
                    float newTopY = (mY-mHeight + (doorBaseBottom.getHeight() * 2)) - (doorSpeed * pSecondsElapsed);
                    float newBottomY = doorBottom.getY() + (doorSpeed * pSecondsElapsed);
                    doorBottomTexture = getTiledTextureRegion(Resources.DoorBottom, newHeight, 0);
                    doorTopTexture = getTiledTextureRegion(Resources.DoorTop, newHeight, (Resources.DoorTop.getHeight() - newHeight));

                    doorBottom.destroy();
                    doorTop.destroy();

                    doorTop = new PhysicsAnimatedSprite(mX + doorBaseBottom.getWidth() / 2 - doorTopTexture.getWidth() / 2, newTopY, doorTopTexture, PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "DOOR_TOP", BodyType.KinematicBody, Util.BodyShape.Box, null);
                    doorBottom = new PhysicsAnimatedSprite(mX + doorBaseBottom.getWidth() / 2 - doorBottomTexture.getWidth() / 2, newBottomY, doorBottomTexture, PhysicsFactory.createFixtureDef(1000f, .05f, .5f), "DOOR_BOTTOM", BodyType.KinematicBody, Util.BodyShape.Box, null);

                    doorTop.setCurrentTileIndex(1);
                    doorBottom.setCurrentTileIndex(1);
                    doorBaseBottom.setCurrentTileIndex(1);
                    doorBaseTop.setCurrentTileIndex(1);

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
