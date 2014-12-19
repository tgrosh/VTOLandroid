package com.singletongames.vtol;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.util.color.Color;
import org.andengine.util.color.ColorUtils;
import org.andengine.util.debug.Debug;

/**
 * Created by Troy on 12/16/14.
 */
public class PolygonTestScene extends GameScene {
    private PinchZoomDetector mPinchZoomDetector = null;
    private PolygonTestScene mThis = this;
    private SurfaceScrollDetector mScrollDetector = null;
    SmoothCamera mCamera;

    public PolygonTestScene() {
        mCamera = (SmoothCamera) Resources.mEngine.getCamera();
        Util.ResetCamera(mCamera);

        TimerHandler delay = new TimerHandler(1f, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                Load();
            }
        });
        this.registerUpdateHandler(delay);

    }

    private void Load() {
        Util.InitializePhysicsWorld(Resources.mEngine, new Vector2(0, SensorManager.GRAVITY_EARTH), false);
        Resources.mPhysicsWorld.setContactListener(GameContactListener.getInstance());

        Resources.mCurrentLevel = new Level(Resources.mEngine, 0, 0, "0-0", false, false, false, 0);
        Resources.mCurrentLevel.Load(this, true, true, null);
        mCamera.setCenterDirect((Resources.CAMERA_WIDTH/2), (Resources.CAMERA_HEIGHT/2));

        String bgColor = Resources.mCurrentLevel.getMap().getBackgroundColor();
        if (!bgColor.equals("")) {
            float[] colorParts = ColorUtils.HexToOpenGL(bgColor);
            this.setBackground(new Background(new Color(colorParts[0], colorParts[1], colorParts[2])));
        }

        mScrollDetector = new SurfaceScrollDetector(new ScrollDetector.IScrollDetectorListener() {
            @Override
            public void onScroll(ScrollDetector pScollDetector, int pPointerID,float pDistanceX, float pDistanceY) {
                final float zoomFactor = mCamera.getZoomFactor();
                mCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
            }
            @Override
            public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {
                mCamera.setEasingEnabled(false);
                mCamera.setMaxVelocity(10000f, 10000f);
            }
            @Override
            public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {
                mCamera.setEasingEnabled(true);
                mCamera.setMaxVelocity(400f, 400f);
            }
        });

        if(MultiTouch.isSupported(Resources.mActivity)) {
            try {
                mPinchZoomDetector = new PinchZoomDetector(new PinchZoomDetector.IPinchZoomDetectorListener() {
                    public float mPinchZoomStartedCameraZoomFactor;

                    @Override
                    public void onPinchZoomStarted(PinchZoomDetector pPinchZoomDetector, TouchEvent pSceneTouchEvent) {
                        this.mPinchZoomStartedCameraZoomFactor = mCamera.getZoomFactor();
                    }

                    @Override
                    public void onPinchZoom(PinchZoomDetector pPinchZoomDetector, TouchEvent pTouchEvent, float pZoomFactor) {
                        mCamera.setZoomFactorDirect(this.mPinchZoomStartedCameraZoomFactor * pZoomFactor);
                    }

                    @Override
                    public void onPinchZoomFinished(PinchZoomDetector pPinchZoomDetector, TouchEvent pTouchEvent, float pZoomFactor) {
                        mCamera.setZoomFactorDirect(this.mPinchZoomStartedCameraZoomFactor * pZoomFactor);
                    }
                });
            } catch (final Exception e) {
                mPinchZoomDetector = null;
            }
        } else {
            mPinchZoomDetector = null;
        }

        this.setOnSceneTouchListener(new IOnSceneTouchListener() {
            @Override
            public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
                if(mPinchZoomDetector != null) {
                    mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);

                    if(mPinchZoomDetector.isZooming()) {
                        mScrollDetector.setEnabled(false);
                    } else {
                        if(pSceneTouchEvent.isActionDown()) {
                            mScrollDetector.setEnabled(true);
                        }
                        mScrollDetector.onTouchEvent(pSceneTouchEvent);
                    }
                } else {
                    mScrollDetector.onTouchEvent(pSceneTouchEvent);
                }

                return true;
            }
        });
    }
}
