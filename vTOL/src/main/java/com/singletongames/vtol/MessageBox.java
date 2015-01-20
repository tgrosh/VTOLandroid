package com.singletongames.vtol;

import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.modifier.ease.EaseBackOut;

public class MessageBox {
	private static MessageBox instance = new MessageBox();
    static Sprite sFrame;
    static Scene mScene;

	public static void show(Scene scene, String title, String message){
		show(scene, title, message, null);
	}
    public static void show(Scene scene, String title, String message, final IMessageBoxListener listener){
        show(scene, title, message, false, listener);
    }
	public static void show(Scene scene, String title, String message, boolean multiPanel, final IMessageBoxListener listener) {
        mScene = scene;
        TextureRegion panelRegion = Resources.LargePanel;
        int messageStartY = 120;
        int messageStartX = 90;
        if (multiPanel){
            panelRegion = Resources.MultiPanel;
            messageStartY = 60;
            messageStartX = 180;
        }
		sFrame = new Sprite(Resources.CAMERA_WIDTH/2 - panelRegion.getWidth()/2, Resources.CAMERA_HEIGHT/2 - panelRegion.getHeight()/2, panelRegion, Resources.mEngine.getVertexBufferObjectManager()){
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                Resources.mEngine.runOnUpdateThread(new Runnable() {
                    @Override
                    public void run() {
                        Resources.soundButtonConfirm.play();
                        closeMessage(listener);
                    }
                });

                return true;
            }
        };
		sFrame.setScale(.5f);
		sFrame.setAlpha(0f);
        scene.registerTouchArea(sFrame);

        Text tTitle = new Text(0, 0, Resources.mFont_Cyan24, title, Resources.mEngine.getVertexBufferObjectManager());
        tTitle.setPosition(panelRegion.getWidth() / 2 - tTitle.getWidth() / 2, 10);
        sFrame.attachChild(tTitle);

        Text tTapToContinue = new Text(0, 0, Resources.mFont_Cyan24, "Tap to Continue", Resources.mEngine.getVertexBufferObjectManager());
        tTitle.setPosition(panelRegion.getWidth() / 2 - tTitle.getWidth() / 2, sFrame.getHeight() - 30);
        sFrame.attachChild(tTapToContinue);

		String[] messages = message.split("\\n");
		int currentMsgY = messageStartY;
		int pSpace = 20;
		for (String msg: messages){
			Text tMessage = new Text(0, 0, Resources.mFont_Cyan24, msg, Resources.mEngine.getVertexBufferObjectManager());
			tMessage.setPosition(messageStartX, currentMsgY);
			tMessage.setAutoWrap(AutoWrap.WORDS);
			tMessage.setAutoWrapWidth(sFrame.getWidth() - (messageStartX*2));
			tMessage.setAlpha(.5f);

			sFrame.attachChild(tMessage);
			
			currentMsgY+=tMessage.getHeight() + pSpace;
		}

		ScaleModifier scaleIn = new ScaleModifier(.5f, .5f, 1f, EaseBackOut.getInstance());
		AlphaModifier alphaIn = new AlphaModifier(.25f, 0f, 1f);
		ParallelEntityModifier par = new ParallelEntityModifier(scaleIn, alphaIn);
		sFrame.registerEntityModifier(par);

		scene.attachChild(sFrame);
	}

    private static void closeMessage(IMessageBoxListener listener) {
        if (listener != null) {
            listener.onClose();
        }
        if (sFrame.hasParent()) {
            mScene.unregisterTouchArea(sFrame);
            sFrame.detachSelf();
        }
    }

    public static MessageBox getInstance() {
		return instance;
	}
}
