package com.singletongames.vtol;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.audio.music.Music;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.GradientStrokeFont;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.preferences.SimplePreferences;

import com.badlogic.gdx.math.Vector2;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.SensorManager;

public class Resources {
    public static final boolean SHOW_FPS = true;
    public static Engine mEngine;
	public static Level mCurrentLevel;
	
	public static boolean DEBUG_DRAW = false;
	public static boolean ClearPrefernces = false; /////////////////////////////////////////////////////////
	
	public static int TrainingProgress = 0;
	
	public static float maxZoomIn = 1.8f;
	public static float maxZoomOut = .5f;
	public static int CAMERA_WIDTH = 1200;
	public static int CAMERA_HEIGHT = 800;
			
	public static PhysicsWorld mPhysicsWorld;
	
	public static Activity mActivity;
	public static Typeface mNeon, mNeonBold;
		
	public static Random rand = new Random((long) (Math.random()*1000));
	public static TextureRegion mSingletonLogo;
	
	public static int selectedLander = 0;
	
	public static TiledTextureRegion ResumeButton, StartButton, MenuButton, RestartButton, PauseButton, OKButton, BackButton,NextButton;
	public static TiledTextureRegion landerLuna, landerHauler;
	public static TextureRegion[] landerDebris = new TextureRegion[6];	
	public static List<TiledTextureRegion> chapterIcons = new ArrayList<TiledTextureRegion>();
	public static List<TextureRegion> titleBars = new ArrayList<TextureRegion>();
	public static List<TextureRegion> HighResLanders = new ArrayList<TextureRegion>();
	public static TextureRegion mThrottleBackground, mThrottleButton, mFuelGaugeBackground, mFuelGaugeOverlay, MainMenuBackground, MessageFrame;
	public static TextureRegion LaunchPad, LandingPad, vtol_logo, titleBar, LanderSelectLocked, TipFrame, TipArrow, refuelPad;
	public static TiledTextureRegion ExplosionSequence, fireworks, CargoDrop;
	public static TextureRegion ObjectiveBullet, ObjectiveCheck, WoodenBox, ChainLink;
	public static TextureRegion GaugeBackground, GaugeGreen, GaugeRed, PingButton, CargoDropGlow, FuelIcon, RepairPad, RepairIcon, DoorBase, DoorTop, DoorBottom;
	
	public static TextureRegion smokeParticle;
	
	public static TextureRegion LanderSelectFrame, LanderSelectArrow, LanderSelectInfoFrame;
	public static TiledTextureRegion LanderSelectInfoProgress;
	
	public static List<String> completedLevelIDs = new ArrayList<String>();	
	public static GradientStrokeFont mFont_REDORANGE48;
	public static Font mFont_Grey48;
	public static Font mFont_Green96;
	public static Font mFont_Yellow96;
	public static Font mFont_Yellow24;
	public static Font mFont_Red96;
	public static Font mFont_Yellow36;
	public static Font mFont_Yellow48;
	public static Font mFont_Yellow72;
	public static Font mFont_BlueGreen96;
	public static Font mFont_BlueGreen72;
	public static Font mFont_BlueGreen48;
	public static Font mFont_Empty96;
	public static Font mFont_Green48;
	public static Font mFont_Red48;
	public static Font mFont_Green32;
	public static Font mFont_Green58;
	public static Font mFont_BlueGreen120;
	public static Font mFont_Green240;
	public static Font mFont_Blue24;
	public static Font mFont_Blue48;
	public static Font mFont_Blue72;
	public static Font mFont_Blue96;
	public static Font mFont_GreenGray48;
	public static Font mFont_White48;
	public static Font mFont_White16;
	public static Font mFont_White18;
	public static Font mFont_White24;
	public static Font mFont_Blue36;
	public static Font mFont_MessageGreen24;
	public static Font mFont_Green18;
	public static Font mFont_Green24;
	
	public static Sound RocketEngine0, LanderExplosion, PingSound, Glug, Ding, RepairSound, DoorOpened, DoorOpening;
	
	public static Sound mRolling, timertick, timergotick, ButtonSound1,ButtonSound2,ButtonSound3,ButtonSound4, ButtonSound1rev,ButtonSound2rev,ButtonSound3rev,ButtonSound4rev;
	public static Sound mSoundDing1, mSoundDing2, mSoundDing3, mKidsCheer, mBallBounce1, mBallBounce2, mBallBounce3, mBallBounce4, mTrampBounce, mWooshIn, mWooshOut, mFail;
	public static Sound mGemApplause, mStartButtonSound, mArcadeButton1;
	
	public static Music mTitle1;
	
	
	public static final short CollisionCategory_Novas = 0x0001;
	public static final short CollisionCategory_Mines = 0x0002;
	public static final short CollisionCategory_Rocks = 0x0004;
	public static final short CollisionCategory_Walls = 0x0008;
	
	public static final short CollisionMask_Novas = CollisionCategory_Mines | CollisionCategory_Rocks;
	public static final short CollisionMask_Mines = CollisionCategory_Novas | CollisionCategory_Walls;
	public static final short CollisionMask_Rocks = CollisionCategory_Novas | CollisionCategory_Walls;
	public static final short CollisionMask_Walls = CollisionCategory_Mines | CollisionCategory_Rocks;
		
	public static void Load(BaseGameActivity pActivity) throws IOException {
        mActivity = pActivity;

        mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);

        mNeon = Typeface.createFromAsset(pActivity.getAssets(), "fonts/PolenticalNeon.ttf");
        mNeonBold = Typeface.createFromAsset(pActivity.getAssets(), "fonts/PolenticalNeonBold.ttf");

        vtol_logo = Util.GetTextureRegion("gfx/vtol_logo.png");
        StartButton = Util.GetTiledTextureRegion("gfx/StartButton.png", 3, 1);
        PauseButton = Util.GetTiledTextureRegion("gfx/PauseButton.png", 3, 1);
        RestartButton = Util.GetTiledTextureRegion("gfx/RestartButton.png", 3, 1);
        MenuButton = Util.GetTiledTextureRegion("gfx/MenuButton.png", 3, 1);
        ResumeButton = Util.GetTiledTextureRegion("gfx/ResumeButton.png", 3, 1);
        NextButton = Util.GetTiledTextureRegion("gfx/NextButton.png", 3, 1);
        BackButton = Util.GetTiledTextureRegion("gfx/BackButton.png", 3, 1);
        OKButton = Util.GetTiledTextureRegion("gfx/OkButton.png", 3, 1);
        LaunchPad = Util.GetTextureRegion("gfx/LaunchPad.png");
        LandingPad = Util.GetTextureRegion("gfx/LandingPad.png");
        refuelPad = Util.GetTextureRegion("gfx/refuelPad.png");
        LanderSelectLocked = Util.GetTextureRegion("gfx/LanderSelectLocked.png");
        MainMenuBackground = Util.GetTextureRegion("gfx/MainMenuBackground.png");
        TipFrame = Util.GetTextureRegion("gfx/TipFrame.png");
        TipArrow = Util.GetTextureRegion("gfx/TipArrow.png");
        ObjectiveBullet = Util.GetTextureRegion("gfx/ObjectiveBullet.png");
        ObjectiveCheck = Util.GetTextureRegion("gfx/ObjectiveCheck.png");
        WoodenBox = Util.GetTextureRegion("gfx/WoodenBox.png");
        ChainLink = Util.GetTextureRegion("gfx/ChainLink.png");
        GaugeBackground = Util.GetTextureRegion("gfx/GaugeBackground.png");
        GaugeGreen = Util.GetTextureRegion("gfx/GaugeGreen.png");
        GaugeRed = Util.GetTextureRegion("gfx/GaugeRed.png");
        PingButton = Util.GetTextureRegion("gfx/PingButton.png");
        CargoDropGlow = Util.GetTextureRegion("gfx/CargoDropGlow.png");
        FuelIcon = Util.GetTextureRegion("gfx/FuelIcon.png");
        RepairPad = Util.GetTextureRegion("gfx/repairPad.png");
        RepairIcon = Util.GetTextureRegion("gfx/RepairIcon.png");
        DoorBase = Util.GetTextureRegion("gfx/DoorBase.png");
        DoorTop = Util.GetTextureRegion("gfx/DoorTop.png");
        DoorBottom = Util.GetTextureRegion("gfx/DoorBottom.png");

        landerDebris[0] = Util.GetTextureRegion("gfx/LanderDebris1.png");
        landerDebris[1] = Util.GetTextureRegion("gfx/LanderDebris2.png");
        landerDebris[2] = Util.GetTextureRegion("gfx/LanderDebris3.png");
        landerDebris[3] = Util.GetTextureRegion("gfx/LanderDebris4.png");
        landerDebris[4] = Util.GetTextureRegion("gfx/LanderDebris5.png");
        landerDebris[5] = Util.GetTextureRegion("gfx/LanderDebris6.png");

        titleBar = Util.GetTextureRegion("gfx/TitleBar.png");
        titleBars.add(Util.GetTextureRegion("gfx/TitleBar0.png"));
        titleBars.add(Util.GetTextureRegion("gfx/TitleBar1.png"));

        chapterIcons.add(Util.GetTiledTextureRegion("gfx/ChapterIcons0.png", 3, 1));
        chapterIcons.add(Util.GetTiledTextureRegion("gfx/ChapterIcons1.png", 3, 1));

        landerLuna = Util.GetTiledTextureRegion("gfx/Lander.png", 2, 1);
        landerHauler = Util.GetTiledTextureRegion("gfx/Lander2.png", 2, 1);
        CargoDrop = Util.GetTiledTextureRegion("gfx/CargoDrop.png", 5, 1);

        mThrottleBackground = Util.GetTextureRegion("gfx/ThrottleBackground.png");
        mThrottleButton = Util.GetTextureRegion("gfx/ThrottleButton.png");
        mFuelGaugeBackground = Util.GetTextureRegion("gfx/FuelGaugeBackground.png");
        mFuelGaugeOverlay = Util.GetTextureRegion("gfx/FuelGaugeOverlay.png");

        ExplosionSequence = Util.GetTiledTextureRegion("gfx/ExplosionSequence.png", 5, 1);
        fireworks = Util.GetTiledTextureRegion("gfx/fireworks.png", 8, 3);

        smokeParticle = Util.GetTextureRegion("gfx/SmokeParticle.png");

        MessageFrame = Util.GetTextureRegion("gfx/MessageFrame.png");
        LanderSelectFrame = Util.GetTextureRegion("gfx/LanderSelectFrame.png");
        LanderSelectArrow = Util.GetTextureRegion("gfx/LanderSelectArrow.png");
        LanderSelectInfoFrame = Util.GetTextureRegion("gfx/LanderSelectInfoFrame.png");
        LanderSelectInfoProgress = Util.GetTiledTextureRegion("gfx/LanderSelectInfoProgress.png", 1, 11);

        HighResLanders.add(Util.GetTextureRegion("gfx/Landerh0.png"));
        HighResLanders.add(Util.GetTextureRegion("gfx/Landerh1.png"));

//			mTitle1 = MusicFactory.createMusicFromAsset(mEngine.getMusicManager(), pActivity, "sfx/title1.mp3");
//			mTitle1.setLooping(true);

        RocketEngine0 = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), pActivity, "sfx/RocketEngine0.ogg");
        RocketEngine0.setLooping(true);
        LanderExplosion = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), pActivity, "sfx/LanderExplosion.ogg");
        PingSound = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), pActivity, "sfx/Ping.ogg");
        Glug = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), pActivity, "sfx/glug.ogg");
        Ding = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), pActivity, "sfx/ding.ogg");
        RepairSound = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), pActivity, "sfx/Repair.ogg");
        DoorOpened = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), pActivity, "sfx/doorOpened.ogg");
        DoorOpening = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), pActivity, "sfx/doorOpening.ogg");

        mFont_Green96 = Util.GetGradientStrokeFont(96, Util.SimpletonTextColorScheme.GRADIENT_GREEN,Color.BLACK, 4, true, mEngine);
        mFont_Yellow96 = Util.GetGradientStrokeFont(96, Util.SimpletonTextColorScheme.GRADIENT_REDORANGE,Color.BLACK, 4, true, mEngine);
        mFont_Red96 = Util.GetGradientStrokeFont(96, Util.SimpletonTextColorScheme.GRADIENT_RED,Color.BLACK, 4, true, mEngine);
        mFont_Grey48 = Util.GetGradientStrokeFont(48, Util.SimpletonTextColorScheme.GRADIENT_GREY,Color.BLACK, 2, true, mEngine);
        mFont_Yellow48 = Util.GetGradientStrokeFont(48, Util.SimpletonTextColorScheme.GRADIENT_REDORANGE,Color.BLACK, 2, true, mEngine);
        mFont_Yellow72 = Util.GetGradientStrokeFont(72, Util.SimpletonTextColorScheme.GRADIENT_REDORANGE,Color.BLACK, 3, true, mEngine);
        mFont_Blue24 = Util.GetGradientStrokeFont(24, Util.SimpletonTextColorScheme.GRADIENT_BLUE,Color.BLACK, 2, true, mEngine);
        mFont_Blue36 = Util.GetGradientStrokeFont(36, Util.SimpletonTextColorScheme.GRADIENT_BLUE,Color.BLACK, 2, true, mEngine);
        mFont_Blue48 = Util.GetGradientStrokeFont(48, Util.SimpletonTextColorScheme.GRADIENT_BLUE,Color.BLACK, 2, true, mEngine);
        mFont_Blue72 = Util.GetGradientStrokeFont(72, Util.SimpletonTextColorScheme.GRADIENT_BLUE,Color.BLACK, 3, true, mEngine);
        mFont_Blue96 = Util.GetGradientStrokeFont(96, Util.SimpletonTextColorScheme.GRADIENT_BLUE,Color.BLACK, 4, true, mEngine);
        mFont_BlueGreen96 = Util.GetGradientStrokeFont(96,Util.SimpletonTextColorScheme.GRADIENT_BLUEGREEN,Color.BLACK,4, true, mEngine);
        mFont_BlueGreen72 = Util.GetGradientStrokeFont(72,Util.SimpletonTextColorScheme.GRADIENT_BLUEGREEN,Color.BLACK,3, true, mEngine);
        mFont_BlueGreen48 = Util.GetGradientStrokeFont(48,Util.SimpletonTextColorScheme.GRADIENT_BLUEGREEN,Color.BLACK,2, true, mEngine);
        mFont_Empty96 = Util.GetGradientStrokeFont(96, Util.SimpletonTextColorScheme.TRANSPARENT, Color.GRAY, 4, true, mEngine);
        mFont_Green48 = Util.GetGradientStrokeFont(48, Util.SimpletonTextColorScheme.GRADIENT_GREEN,Color.BLACK, 2, true, mEngine);
        mFont_Red48 = Util.GetGradientStrokeFont(48, Util.SimpletonTextColorScheme.GRADIENT_RED,Color.BLACK, 2, true, mEngine);
        mFont_Green32 = Util.GetGradientStrokeFont(32, Util.SimpletonTextColorScheme.GRADIENT_GREEN,Color.BLACK, 2, true, mEngine);
        mFont_Green58 = Util.GetGradientStrokeFont(58, Util.SimpletonTextColorScheme.GRADIENT_GREEN,Color.BLACK, 2, true, mEngine);
        mFont_BlueGreen120 = Util.GetGradientStrokeFont(120, Util.SimpletonTextColorScheme.GRADIENT_BLUEGREEN,Color.BLACK, 6, true, mEngine);
        mFont_Green240 = Util.GetGradientStrokeFont(240, Util.SimpletonTextColorScheme.GRADIENT_GREEN,Color.BLACK, 10, true, mEngine);
        mFont_GreenGray48 = Util.GetGradientStrokeFont(48, Util.SimpletonTextColorScheme.GRADIENT_GREEN_GRAY,Color.BLACK, 2, true, mEngine);
        mFont_White48 = Util.GetGradientStrokeFont(48, Util.SimpletonTextColorScheme.WHITE,Color.BLACK, 2, true, mEngine);
        mFont_White16 = Util.GetGradientStrokeFont(16, Util.SimpletonTextColorScheme.WHITE,Color.BLACK, 1, true, mEngine);
        mFont_White18 = Util.GetGradientStrokeFont(18, Util.SimpletonTextColorScheme.WHITE,Color.BLACK, 1, true, mEngine);
        mFont_White24 = Util.GetGradientStrokeFont(18, Util.SimpletonTextColorScheme.WHITE,Color.BLACK, 1, true, mEngine);
        mFont_Yellow24 = Util.GetStrokeFont(24, Color.YELLOW,Color.BLACK, 2, true, mEngine);
        mFont_Yellow36 = Util.GetStrokeFont(36, Color.YELLOW,Color.BLACK, 3, true, mEngine);
        mFont_MessageGreen24 = Util.GetStrokeFont(24, Color.parseColor("#00CC00"),Color.parseColor("#006600"), 2, true, mEngine);
        mFont_Green18 = Util.GetGradientStrokeFont(18, Util.SimpletonTextColorScheme.GRADIENT_GREEN,Color.BLACK, 2, true, mEngine);
        mFont_Green24 = Util.GetGradientStrokeFont(24, Util.SimpletonTextColorScheme.GRADIENT_GREEN,Color.BLACK, 2, true, mEngine);

        completedLevelIDs = Arrays.asList(SimplePreferences.getInstance(mActivity).getString("COMPLETED_LEVEL_IDS", "").split(","));

        TrainingProgress = SimplePreferences.getInstance(mActivity).getInt("TrainingProgress", 0);
    }
}
