package com.singletongames.vtol;

import org.andengine.engine.camera.SmoothCamera;

/**
 * Created by Troy on 12/16/14.
 */
public class PolygonTestScene extends GameScene {

    public PolygonTestScene() {
        Util.ResetCamera((SmoothCamera) Resources.mEngine.getCamera());

//		Resources.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
//
		Resources.mCurrentLevel = new Level(Resources.mEngine, 0, 0, "0-0", false, false, false, 0);
		Resources.mCurrentLevel.Load(this, Resources.DEBUG_DRAW, false, null);
//		float mapScale = .25f;
//		Resources.mCurrentLevel.getMap().getTMXLayers().get(0).setScaleCenter(0, 0);
//		Resources.mCurrentLevel.getMap().getTMXLayers().get(0).setScale(mapScale);
//		mCamera.setCenterDirect((Resources.CAMERA_WIDTH/2), (Resources.CAMERA_HEIGHT/2));
    }
}
