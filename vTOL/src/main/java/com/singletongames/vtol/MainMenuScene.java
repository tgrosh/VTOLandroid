package com.singletongames.vtol;

import android.app.AlertDialog;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;

public class MainMenuScene extends GameScene {
	protected SmoothCamera mCamera = (SmoothCamera) Resources.mEngine.getCamera();
	protected Engine mEngine = Resources.mEngine;
	Scene mScene = this;
	
	public MainMenuScene(){
		Util.ResetCamera(mCamera);

		if (Resources.ClearPrefernces){
			Resources.mActivity.runOnUiThread(new Runnable() {			
				@Override
				public void run() {
					AlertDialog.Builder alert = new AlertDialog.Builder(Resources.mActivity);
			        alert.setTitle("Clearing Preferences");
			        alert.setMessage("Preferences have been cleared");
			        alert.setPositiveButton("OK", null);
			        alert.show();	
				}
			});	
		}
		
		TimerHandler delay = new TimerHandler(1f, new ITimerCallback() {			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				Load();
			}
		});
		this.registerUpdateHandler(delay);
				
			
	}

	protected void Load() {
		Sprite bg = new Sprite(0,0,Resources.MainMenuBackground,Resources.mEngine.getVertexBufferObjectManager());
		bg.setScale(1.1f);
		this.attachChild(bg);
		
		Sprite logo = new Sprite(Resources.CAMERA_WIDTH/2 - Resources.vtol_logo.getWidth()/2, 150, Resources.vtol_logo, mEngine.getVertexBufferObjectManager());
		this.attachChild(logo);
		
		ButtonSprite startButton = new ButtonSprite(0,0,Resources.StartButton, Resources.mEngine.getVertexBufferObjectManager(), new OnClickListener() {			
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,	float pTouchAreaLocalY) {
                Resources.soundButtonStart.play();
				Util.FadeToBlack(mScene, new ChapterSelectScene());
			}
		});		
		startButton.setPosition(Resources.CAMERA_WIDTH/2 - startButton.getWidth()/2, logo.getY() + logo.getHeight() + 100);
		this.attachChild(startButton);
		this.registerTouchArea(startButton);
		
		Util.FadeIn(this);
	}

	@Override
	public void Pause() {
	}

	@Override
	public void Resume() {
	}

	@Override
	public void Back() {
		System.exit(0);		
	}	
}
