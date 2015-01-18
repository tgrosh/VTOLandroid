package com.singletongames.vtol;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBackOut;
import org.andengine.util.modifier.ease.EaseSineIn;
import org.andengine.util.modifier.ease.EaseSineOut;

import java.util.ArrayList;
import java.util.List;


public class LanderSelectScene extends GameScene {
	private Scene mThis = this;
	
	List<LanderInfo> landers = LanderDB.getInstance().allLanders();
	int mCurrentLanderIndex = 0;
	private AnimatedSprite sInfoProgress1;
	private AnimatedSprite sInfoProgress2;
	private AnimatedSprite sInfoProgress3;
	private AnimatedSprite sInfoProgress4;
	private Text tName;
	private List<Sprite> landerSprites;
	private Sprite currentLanderSprite;
	private Text tDescription;
	private Sprite panelLarge;
	
	private ILanderSelectListener listener;
	private boolean transitioning = false;

	private Text tLocked;
    private Text tTapToSelect;
    private Text title;

	public LanderSelectScene(ILanderSelectListener listener) {
		this.listener = listener;
		if (landers != null && landers.size() > 0){
			Load();
		}
	}

	private void Load() {
		Util.ResetCamera((SmoothCamera) Resources.mEngine.getCamera());
        this.setBackgroundEnabled(false);

		panelLarge = new Sprite(Resources.CAMERA_WIDTH/2 - Resources.LargePanel.getWidth()/2, Resources.CAMERA_HEIGHT/2 - Resources.LargePanel.getHeight()/2, Resources.LargePanel, Resources.mEngine.getVertexBufferObjectManager());
		panelLarge.setScale(.5f);
		panelLarge.setAlpha(0f);

        title = new Text(0, 0, Resources.mFont_Cyan24, "Select Lander", Resources.mEngine.getVertexBufferObjectManager());
        title.setPosition(Resources.LargePanel.getWidth() / 2 - title.getWidth() / 2, 10);

        tLocked = new Text(0, 0, Resources.mFont_Red48, "Locked", Resources.mEngine.getVertexBufferObjectManager());
		tLocked.setPosition(Resources.LargePanel.getWidth() / 2 - tLocked.getWidth() / 2, 335);
		tLocked.setVisible(false);
        tLocked.setZIndex(101);

        tTapToSelect = new Text(0, 0, Resources.mFont_Cyan24, "Tap to Select", Resources.mEngine.getVertexBufferObjectManager());
        tTapToSelect.setPosition(Resources.LargePanel.getWidth()/2 - tTapToSelect.getWidth()/2, 350);
        tTapToSelect.setVisible(false);
        tTapToSelect.setZIndex(101);

        Sprite sLanderFrameOverlay = new Sprite(panelLarge.getWidth()/2 - Resources.LanderPanelOverlay.getWidth()/2, 150, Resources.LanderPanelOverlay, Resources.mEngine.getVertexBufferObjectManager());
        sLanderFrameOverlay.setZIndex(100);
        Sprite sInfoFrameLeft = new Sprite(panelLarge.getWidth()/2 - Resources.InfoPanelOverlay.getWidth() - 20, 388, Resources.InfoPanelOverlay, Resources.mEngine.getVertexBufferObjectManager());
		Sprite sInfoFrameRight = new Sprite(panelLarge.getWidth()/2 + 20, sInfoFrameLeft.getY(), Resources.InfoPanelOverlay, Resources.mEngine.getVertexBufferObjectManager());

        Sprite sArrowLeft = new Sprite(65, sLanderFrameOverlay.getY() + sLanderFrameOverlay.getHeight()/2 - Resources.PanelArrowRight.getHeight()/2, Resources.PanelArrowRight, Resources.mEngine.getVertexBufferObjectManager()){
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp() && !transitioning){
                    Resources.soundButtonBlip.play();
                    Prev();
                }
                return true;
            }

        };
        sArrowLeft.setFlippedHorizontal(true);
        Sprite sArrowRight = new Sprite(panelLarge.getWidth() - Resources.PanelArrowRight.getWidth() - 65, sArrowLeft.getY(), Resources.PanelArrowRight, Resources.mEngine.getVertexBufferObjectManager()){
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp() && !transitioning){
                    Resources.soundButtonBlip.play();
                    Next();
                }
                return true;
            }

        };

        landerSprites = new ArrayList<Sprite>();
		for (int x=0; x<Resources.HighResLanders.size(); x++ ){
			TextureRegion tex = Resources.HighResLanders.get(x);
			
			Sprite s = new Sprite(panelLarge.getWidth()/2 - tex.getWidth()/2, 265 - tex.getHeight()/2, tex, Resources.mEngine.getVertexBufferObjectManager()){
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionUp() && !transitioning){
						SelectLander((Integer) this.getUserData());
					}
					return true;
				}
			};
			s.setUserData(x);
			
			landerSprites.add(s);
		}
		currentLanderSprite = landerSprites.get(0);
        currentLanderSprite.setZIndex(99);

		tName = new Text(0, 0, Resources.mFont_Cyan48, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", Resources.mEngine.getVertexBufferObjectManager());
        Sprite sNameOverlay = new Sprite(panelLarge.getWidth()/2 - Resources.LanderNameOverlay.getWidth()/2, 66, Resources.LanderNameOverlay, Resources.mEngine.getVertexBufferObjectManager());

        tDescription = new Text(0, 0, Resources.mFont_Cyan18, landers.get(mCurrentLanderIndex).getDescription(), Resources.mEngine.getVertexBufferObjectManager());
		tDescription.setAutoWrap(AutoWrap.WORDS);
		tDescription.setAutoWrapWidth(sInfoFrameLeft.getWidth() - 60);
		tDescription.setPosition(sInfoFrameLeft.getX() + 30, sInfoFrameLeft.getY() + 15);
		
		Text attPower = new Text(0, 0, Resources.mFont_Cyan24, "Power", Resources.mEngine.getVertexBufferObjectManager());
		attPower.setPosition(sInfoFrameRight.getX() + sInfoFrameRight.getWidth()/2 - attPower.getWidth() + 25, sInfoFrameRight.getY() + 15);
		Text attToughness = new Text(0, 0, Resources.mFont_Cyan24, "Toughness", Resources.mEngine.getVertexBufferObjectManager());
		attToughness.setPosition(sInfoFrameRight.getX() + sInfoFrameRight.getWidth()/2 - attToughness.getWidth() + 25, attPower.getY() + 25);
		Text attFuel = new Text(0, 0, Resources.mFont_Cyan24, "Fuel", Resources.mEngine.getVertexBufferObjectManager());
		attFuel.setPosition(sInfoFrameRight.getX() + sInfoFrameRight.getWidth()/2 - attFuel.getWidth() + 25, attToughness.getY() + 25);
		Text attWeight = new Text(0, 0, Resources.mFont_Cyan24, "Weight", Resources.mEngine.getVertexBufferObjectManager());
		attWeight.setPosition(sInfoFrameRight.getX() + sInfoFrameRight.getWidth() / 2 - attWeight.getWidth() + 25, attFuel.getY() + 25);

        sInfoProgress1 = new AnimatedSprite(sInfoFrameRight.getX() + sInfoFrameRight.getWidth()/2 + 30, sInfoFrameRight.getY() + 25, Resources.LanderSelectInfoProgress, Resources.mEngine.getVertexBufferObjectManager());
        sInfoProgress2 = new AnimatedSprite(sInfoProgress1.getX(), sInfoProgress1.getY() + 25, Resources.LanderSelectInfoProgress, Resources.mEngine.getVertexBufferObjectManager());
        sInfoProgress3 = new AnimatedSprite(sInfoProgress1.getX(), sInfoProgress2.getY() + 25, Resources.LanderSelectInfoProgress, Resources.mEngine.getVertexBufferObjectManager());
        sInfoProgress4 = new AnimatedSprite(sInfoProgress1.getX(), sInfoProgress3.getY() + 25, Resources.LanderSelectInfoProgress, Resources.mEngine.getVertexBufferObjectManager());


        ButtonSprite backButton = new ButtonSprite(40, 40, Resources.PanelBackButton, Resources.mEngine.getVertexBufferObjectManager(), new ButtonSprite.OnClickListener() {
            @Override
            public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,	float pTouchAreaLocalY) {
                Resources.soundButtonConfirm.play();
                listener.onCancel();
            }
        });
        this.registerTouchArea(backButton);
        panelLarge.attachChild(backButton);

		ShowLanderInfo(mCurrentLanderIndex);

        panelLarge.attachChild(title);

        panelLarge.attachChild(tDescription);

        panelLarge.attachChild(attPower);
        panelLarge.attachChild(attToughness);
        panelLarge.attachChild(attFuel);
        panelLarge.attachChild(attWeight);

        panelLarge.attachChild(sInfoProgress1);
        panelLarge.attachChild(sInfoProgress2);
        panelLarge.attachChild(sInfoProgress3);
        panelLarge.attachChild(sInfoProgress4);

		panelLarge.attachChild(sArrowLeft);
		panelLarge.attachChild(sArrowRight);
		panelLarge.attachChild(sInfoFrameLeft);
		panelLarge.attachChild(sInfoFrameRight);
		panelLarge.attachChild(tName);
        panelLarge.attachChild(sNameOverlay);
		if (landerSprites.size() > 0){
			panelLarge.attachChild(currentLanderSprite);
		}
        panelLarge.attachChild(sLanderFrameOverlay);
        panelLarge.attachChild(tTapToSelect);
        panelLarge.attachChild(tLocked);

		this.attachChild(panelLarge);

		this.registerTouchArea(sArrowLeft);
		this.registerTouchArea(sArrowRight);
        if (landers.get(mCurrentLanderIndex).isLocked()){
            Sprite sLocked = new Sprite(currentLanderSprite.getWidth()/2 - Resources.LanderSelectLocked.getWidth()/2,currentLanderSprite.getHeight()/2 - Resources.LanderSelectLocked.getHeight()/2,Resources.LanderSelectLocked,Resources.mEngine.getVertexBufferObjectManager());
            currentLanderSprite.attachChild(sLocked);
            tLocked.setVisible(true);
            tTapToSelect.setVisible(false);
        }
        else{
            this.registerTouchArea(currentLanderSprite);
            tLocked.setVisible(false);
            tTapToSelect.setVisible(true);
        }
		
		ScaleModifier scaleIn = new ScaleModifier(.5f, .5f, 1f, EaseBackOut.getInstance());
		AlphaModifier alphaIn = new AlphaModifier(.25f, 0f, 1f);
		ParallelEntityModifier par = new ParallelEntityModifier(scaleIn, alphaIn);
		panelLarge.registerEntityModifier(par);
	}

	protected void SelectLander(int index) {		
		Resources.selectedLander = index;
		listener.onSelect();
	}

	private void ShowLanderInfo(final int Index){
		tName.setText(landers.get(Index).getName());
		tName.setPosition(Resources.LargePanel.getWidth() / 2 - tName.getWidth() / 2, 70);
		
		tDescription.setText(landers.get(Index).getDescription());

		sInfoProgress1.setCurrentTileIndex((int) (landers.get(Index).getEngineThrustPct() * 10f));
		sInfoProgress2.setCurrentTileIndex((int) (landers.get(Index).getToughnessPct() * 10f));
		sInfoProgress3.setCurrentTileIndex((int) (landers.get(Index).getFuelCapacityPct() * 10f));
		sInfoProgress4.setCurrentTileIndex((int) (landers.get(Index).getDensityPct() * 10f));
		
		//if current lander sprite is not the one we need to show
		if (!currentLanderSprite.equals(landerSprites.get(Index))){		
			MoveModifier moverOut = new MoveModifier(.2f, currentLanderSprite.getX(), currentLanderSprite.getX(), currentLanderSprite.getY(), currentLanderSprite.getY() + 10f, new IEntityModifierListener() {
				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				}
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, final IEntity pItem) {
					Resources.mEngine.runOnUpdateThread(new Runnable() {					
						@Override
						public void run() {							
							float newY = currentLanderSprite.getY();
							currentLanderSprite.clearEntityModifiers();
							currentLanderSprite.detachSelf();						
							
							currentLanderSprite = landerSprites.get(Index);
							currentLanderSprite.setPosition(panelLarge.getWidth()/2 - currentLanderSprite.getWidth()/2, newY);
							currentLanderSprite.setAlpha(0f);
                            currentLanderSprite.setZIndex(99);
							panelLarge.attachChild(currentLanderSprite);
                            panelLarge.sortChildren();
							if (landers.get(Index).isLocked()){
								Sprite sLocked = new Sprite(currentLanderSprite.getWidth()/2 - Resources.LanderSelectLocked.getWidth()/2,currentLanderSprite.getHeight()/2 - Resources.LanderSelectLocked.getHeight()/2,Resources.LanderSelectLocked,Resources.mEngine.getVertexBufferObjectManager());
								currentLanderSprite.attachChild(sLocked);
								tLocked.setVisible(true);
								tTapToSelect.setVisible(false);
							}
							else{
								mThis.registerTouchArea(currentLanderSprite);
								tLocked.setVisible(false);
								tTapToSelect.setVisible(true);
							}
							
							MoveModifier moverIn = new MoveModifier(.2f, currentLanderSprite.getX(), currentLanderSprite.getX(), currentLanderSprite.getY(), currentLanderSprite.getY() - 10f, new IEntityModifierListener() {
								@Override
								public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
								}								
								@Override
								public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
									transitioning = false;
								}
							}, EaseSineIn.getInstance());
							AlphaModifier alphaIn = new AlphaModifier(.2f, 0f, 1f);
							ParallelEntityModifier parIn = new ParallelEntityModifier(moverIn, alphaIn);
							currentLanderSprite.registerEntityModifier(parIn);
						}
					});
				}
			}, EaseSineOut.getInstance());
			AlphaModifier alphaOut = new AlphaModifier(.2f, 1f, 0f);
			ParallelEntityModifier parOut = new ParallelEntityModifier(moverOut, alphaOut);
			currentLanderSprite.registerEntityModifier(parOut);
		}
	}
	
	private void Next(){
		transitioning = true;
		mCurrentLanderIndex++;
		if (mCurrentLanderIndex > LanderDB.getInstance().allLanders().size()-1){
			mCurrentLanderIndex = 0;
		}
		ShowLanderInfo(mCurrentLanderIndex);
	}
	
	private void Prev(){
		transitioning = true;
		mCurrentLanderIndex--;
		if (mCurrentLanderIndex < 0){
			mCurrentLanderIndex = LanderDB.getInstance().allLanders().size()-1;
		}
		ShowLanderInfo(mCurrentLanderIndex);
	}

}
