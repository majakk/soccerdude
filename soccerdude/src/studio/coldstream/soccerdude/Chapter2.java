package studio.coldstream.soccerdude;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.BoundCamera;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLayer;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXProperties;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTile;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTileProperty;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.anddev.andengine.entity.layer.tiled.tmx.util.exception.TMXLoadException;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.ColorModifier;
import org.anddev.andengine.entity.modifier.DelayModifier;
import org.anddev.andengine.entity.modifier.LoopEntityModifier;
import org.anddev.andengine.entity.modifier.ParallelEntityModifier;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.anddev.andengine.entity.modifier.PathModifier.Path;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.TextMenuItem;
import org.anddev.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.font.StrokeFont;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.util.constants.Constants;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;

public class Chapter2 extends BaseExample implements IOnMenuItemClickListener {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final boolean DEBUG_MODE = false;
	
	private static final int THIS_CHAPTER = 2;
	
	private static final String TAG = "SOCCER_MAIN";
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	
	protected static final int MENU_RESET = 1000;
	protected static final int MENU_QUIT = MENU_RESET + 1;
	protected static final int MENU_OK = MENU_RESET + 2;
	protected static final int MENU_RESUME = MENU_RESET + 3;
	
	private static final int FONT_SIZE = 32;

	// ===========================================================
	// Fields
	// ===========================================================

	private BoundCamera mBoundChaseCamera;

	private Texture mTexture;
	private TiledTextureRegion mPlayerTextureRegion;
	private TiledTextureRegion mWillowTextureRegion;
	private TiledTextureRegion mBallTextureRegion;
	private TiledTextureRegion mWaspTextureRegion;
	private TiledTextureRegion mDustTextureRegion;
	private TiledTextureRegion mCardTextureRegion;
	private TextureRegion mStoryPanelRegion;
	private TMXTiledMap mTMXTiledMap;
	protected int mCactusCount;
	
	private TMXTile oldTMXTile;
	private TMXTile oldBallTile;
	private float[] oldPlayerFootCordinates = {0.0f, 0.0f};
	private float[] oldBallCenterCordinates = {0.0f, 0.0f};
	
	private Texture mOnScreenControlTexture;
	private TextureRegion mOnScreenControlBaseTextureRegion;
	private TextureRegion mOnScreenControlKnobTextureRegion;
	
	private Texture mFontTexture;
	private Font mFont;
	private Texture mFontTexture2;
	private Font mFont2;
	private Font mMenuFont;
	
	
	private Texture mStrokeFontTexture;
	private StrokeFont mStrokeFont;
	private Texture mXPFontTexture;
	private Font mXPFont;
		
	private ScoreBoard scoreBoard;
	//private GoalBoard goalBoard;
	
	private float mValueX;
	private float mValueY;
	private float mImpVectX;
	private float mImpVectY;
	
	private int mBallAcc;
	private boolean shootBall;
	private boolean playerNearBall;
	
	protected Scene mMainScene;
	protected MenuScene mMenuScene;
	protected MenuScene mStoryScene;
	protected MenuScene mGameOverScene;
	protected AnalogOnScreenControl analogOnScreenControl;
	
	// Player Status
	private int xp;
	
	
	//Chapter Flags
	private boolean goalFlag;
	private boolean killedWaspFlag;
	private boolean signFlag1;
	private boolean signFlag2;
	private boolean willowFlag;
	private boolean willowFlag2;
	private boolean countFlag;
	private boolean recoverFlag;
	private boolean gameOverFlag;
	private boolean nextChapter;
	
	//private int flagReset;
	
	private ChangeableText storyText;	
	
	//private Sound mExplosionSound;
	private Sound mBounceSound;
	private Sound mKickSound;
	private Sound mGoalSound;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public Engine onLoadEngine() {
		
		//Toast.makeText(this, "One day in the backyard...", Toast.LENGTH_LONG).show();
		this.mBoundChaseCamera = new BoundCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mBoundChaseCamera).setNeedsSound(true));
	}

	@Override
	public void onLoadResources() {
		TextureRegionFactory.setAssetBasePath("gfx/");

		/* Controller */
		this.mTexture = new Texture(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);		
		this.mOnScreenControlTexture = new Texture(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mOnScreenControlBaseTextureRegion = TextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = TextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_knob.png", 128, 0);
		this.mEngine.getTextureManager().loadTextures(this.mTexture, this.mOnScreenControlTexture);
		
		/* Player */
		this.mTexture = new Texture(128, 128, TextureOptions.DEFAULT);
		this.mPlayerTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "player_second.png", 0, 0, 3, 4); // 72x128
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
		
		/* Willow */
		this.mTexture = new Texture(128, 128, TextureOptions.DEFAULT);
		this.mWillowTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "willow.png", 0, 0, 3, 4); // 72x128
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
		
		/* Ball */
		this.mTexture = new Texture(64, 128, TextureOptions.DEFAULT);
		this.mBallTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "ui_ball.png", 0, 0, 2, 4); 
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
		
		/* Wasp */
		this.mTexture = new Texture(256, 128, TextureOptions.DEFAULT);
		this.mWaspTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "bosswasp.png", 0, 0, 4, 2); 
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
		
		/* Dust */
		this.mTexture = new Texture(256, 32, TextureOptions.DEFAULT);
		this.mDustTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "dust.png", 0, 0, 8, 1); 
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
		
		/* Text */
		this.mFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = new Font(this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.WHITE);
		this.mFontTexture2 = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont2 = new Font(this.mFontTexture2, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 22, true, Color.WHITE);
		this.mStrokeFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mStrokeFont = new StrokeFont(this.mStrokeFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), FONT_SIZE, true, Color.WHITE, 2, Color.BLACK);
		this.mXPFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		//this.mXPFont = FontFactory.createFromAsset(this.mFontTexture, this, "Plok.ttf", 24, true, Color.CYAN);
		this.mXPFont = new StrokeFont(this.mXPFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 24, true, Color.YELLOW, 1, Color.BLACK);		
		this.mEngine.getTextureManager().loadTextures(this.mFontTexture, this.mFontTexture2, this.mStrokeFontTexture, this.mXPFontTexture);
		this.mEngine.getFontManager().loadFonts(this.mFont,this.mFont2, this.mStrokeFont, this.mXPFont);
		
		/* StoryPanel */
		this.mTexture = new Texture(512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA); //336x240
		this.mStoryPanelRegion = TextureRegionFactory.createFromAsset(this.mTexture, this, "storypanel.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
		
		/* HUD */		
		this.mTexture = new Texture(128, 32, TextureOptions.DEFAULT);
		this.mCardTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "redheart.png", 0, 0, 4, 1);
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
						
		/* Menu */
		FontFactory.setAssetBasePath("font/");
		this.mFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuFont = FontFactory.createFromAsset(this.mFontTexture, this, "Plok.ttf", 48, true, Color.WHITE);
		
		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.mEngine.getFontManager().loadFont(this.mMenuFont);
		
		/* Sounds */
		SoundFactory.setAssetBasePath("mfx/");
		try {
			//this.mExplosionSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "explosion.ogg");
			this.mBounceSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "bounce3.wav");
			this.mKickSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "kick.wav");
			this.mGoalSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "goal.wav");
		} catch (final IOException e) {
			Debug.e(e);
		}
				
		this.scoreBoard = new ScoreBoard(0, 0, this.mStrokeFont, this.mCardTextureRegion , null);
		mBoundChaseCamera.setHUD(this.scoreBoard);
		
		Intent data = this.getIntent();
	    //currentChapter = data.getIntExtra("CURRENT_CHAPTER", 0);
	    xp = data.getIntExtra("CURRENT_XP", 0);
		scoreBoard.setMaxLife(data.getIntExtra("CURRENT_MAX_LIFE", 6));
		
		Log.d(TAG, "ALIFE:" + Integer.toString(scoreBoard.getLife()) + " AMAXLIFE:" + Integer.toString(data.getIntExtra("CURRENT_MAX_LIFE", 6)));
		
		mBallAcc = 0;
		shootBall = false;
		goalFlag = false;
		killedWaspFlag = false;
		signFlag1 = false;
		signFlag2 = false;
		willowFlag = false;
		willowFlag2 = false;
		countFlag = false;
		recoverFlag = false;
		gameOverFlag = false;
		nextChapter = false;		
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		
		this.mMenuScene = this.createMenuScene();
		this.mGameOverScene = this.createGameOverScene();
		this.mStoryScene = this.createStoryScene();
		
		this.mMainScene = new Scene(2);

		try {
			final TMXLoader tmxLoader = new TMXLoader(this, this.mEngine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, new ITMXTilePropertiesListener() {
				@Override
				public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer, final TMXTile pTMXTile, final TMXProperties<TMXTileProperty> pTMXTileProperties) {
					/* We are going to count the tiles that have the property "cactus=true" set. */
					if(pTMXTileProperties.containsTMXProperty("cactus", "true")) {
						Chapter2.this.mCactusCount++;
						
					}
					
					//if(DEBUG_MODE) Log.d(TAG, Integer.toString(pTMXTile.getGlobalTileID() ));
					
				}
			});
			this.mTMXTiledMap = tmxLoader.loadFromAsset(this, "tmx/level2.tmx");
			

			//Toast.makeText(this, "Cactus count in this TMXTiledMap: " + this.mCactusCount, Toast.LENGTH_LONG).show();
		} catch (final TMXLoadException tmxle) {
			Debug.e(tmxle);
		}
		
		final TMXLayer tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);
		this.mMainScene.getFirstChild().attachChild(tmxLayer);

		/* Make the camera not exceed the bounds of the TMXEntity. */
		this.mBoundChaseCamera.setBounds(0, tmxLayer.getWidth(), 0, tmxLayer.getHeight());
		this.mBoundChaseCamera.setBoundsEnabled(true);

		/* Calculate the coordinates for the face, so its centered on the camera. */
		final int centerX = (CAMERA_WIDTH - this.mPlayerTextureRegion.getTileWidth()) / 2;
		final int centerY = (CAMERA_HEIGHT - this.mPlayerTextureRegion.getTileHeight()) / 2;

		/* Create the player sprite and add it to the scene. */
		final AnimatedSprite player = new AnimatedSprite(centerX, centerY, this.mPlayerTextureRegion);
		this.mBoundChaseCamera.setChaseEntity(player);
		final PhysicsHandler playerPhysicsHandler = new PhysicsHandler(player);
		player.registerUpdateHandler(playerPhysicsHandler);
		player.setPosition(2.0f * 32, 35.0f * 32);
				
		/* Create the ball sprite and add it to the scene. */
		final AnimatedSprite ball = new AnimatedSprite(centerX, centerY, this.mBallTextureRegion);
		final PhysicsHandler ballPhysicsHandler = new PhysicsHandler(ball);
		ball.registerUpdateHandler(ballPhysicsHandler);		
		ball.setPosition(7.0f * 32, 34.0f * 32);
		
		/* Create the wasp sprite and add it to the scene. */
		final AnimatedSprite wasp = new AnimatedSprite(centerX, centerY, this.mWaspTextureRegion);
		final PhysicsHandler waspPhysicsHandler = new PhysicsHandler(wasp);
		wasp.registerUpdateHandler(waspPhysicsHandler);		
		wasp.setPosition(400.0f, 400.0f);
		
		/* Create the willow sprite and add it to the scene. */
		final AnimatedSprite willow = new AnimatedSprite(centerX, centerY, this.mWillowTextureRegion);
		//final PhysicsHandler willowPhysicsHandler = new PhysicsHandler(willow);
		//willow.registerUpdateHandler(willowPhysicsHandler);	
		final float wx = (mTMXTiledMap.getTileWidth() * 10) ; 
		final float wy = (mTMXTiledMap.getTileHeight() * 34) ;
		willow.setPosition(wx, wy);
		
		/*final Path path = new Path(5).to(wx, wy).to(wx, wy+32).to(wx+32, wy+32).to(wx+32, wy).to(wx, wy);

		willow.registerEntityModifier(new LoopEntityModifier(new PathModifier(6, path, null, new IPathModifierListener() {
			@Override
			public void onWaypointPassed(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
				switch(pWaypointIndex) {
					case 0:
						willow.animate(new long[]{150, 150, 150}, 6, 8, true);
						break;
					case 1:
						willow.animate(new long[]{150, 150, 150}, 3, 5, true);
						break;
					case 2:
						willow.animate(new long[]{150, 150, 150}, 0, 2, true);
						break;
					case 3:
						willow.animate(new long[]{150, 150, 150}, 9, 11, true);
						break;
				}
			}
		})));*/

		/* Now we are going to create a rectangle that will  always highlight the tile below the feet of the pEntity. */
		final Rectangle currentTileRectangle = new Rectangle(0, 0, this.mTMXTiledMap.getTileWidth(), this.mTMXTiledMap.getTileHeight());
		currentTileRectangle.setColor(1, 1, 1, 0.25f);
		this.mMainScene.getLastChild().attachChild(currentTileRectangle);
		
		final Rectangle ballTileRectangle = new Rectangle(0, 0, this.mTMXTiledMap.getTileWidth(), this.mTMXTiledMap.getTileHeight());
		ballTileRectangle.setColor(0, 0, 0, 0.25f);
		this.mMainScene.getLastChild().attachChild(ballTileRectangle);
		
		this.mMainScene.registerUpdateHandler(new IUpdateHandler() {
			float endTimer = -100.0f;
			
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				
				/* Get the scene-coordinates of the players feet. */
				final float[] playerFootCordinates = player.convertLocalToSceneCoordinates(12, 31); //Change this?? The ball seems to work better... -->
				//final float[] playerFootCordinates = {player.getX() + 12, player.getY() + 31};
				final float[] ballCenterCordinates = {ball.getX() + 15, ball.getY() + 15};

				/* Get the tile the feet of the player are currently waking on. */
				final TMXTile playerTile = tmxLayer.getTMXTileAt(playerFootCordinates[Constants.VERTEX_INDEX_X], playerFootCordinates[Constants.VERTEX_INDEX_Y]);
				final TMXTile ballTile = tmxLayer.getTMXTileAt(ballCenterCordinates[Constants.VERTEX_INDEX_X], ballCenterCordinates[Constants.VERTEX_INDEX_Y]);
								
				if(playerTile != null && DEBUG_MODE) {
					// tmxTile.setTextureRegion(null); <-- Rubber-style removing of tiles =D
					currentTileRectangle.setPosition(playerTile.getTileX(), playerTile.getTileY());
				}
								
				if(ballTile != null && DEBUG_MODE) {
					// tmxTile.setTextureRegion(null); <-- Rubber-style removing of tiles =D
					ballTileRectangle.setPosition(ballTile.getTileX(), ballTile.getTileY());
				}		
				
				if(Math.abs(player.getX() - ball.getX()) < 48 && Math.abs(player.getY() - ball.getY()) < 48)
					playerNearBall = true;
				else
					playerNearBall = false;
				
				//Wasp routine
				if(wasp.getX() > 0 && wasp.getY() > 0){
					final float ox = wasp.getX();
					final float cx = (mTMXTiledMap.getTileWidth() * 33) + 75.0f * (float)Math.sin(Chapter2.this.mEngine.getSecondsElapsedTotal() / 2.25);
					final float cy = (mTMXTiledMap.getTileHeight() * 14) + 15.0f * (float)Math.sin(4 * Chapter2.this.mEngine.getSecondsElapsedTotal());
					wasp.setPosition(cx, cy);
					if(tmxLayer.getTMXTileAt(ox,cy) != tmxLayer.getTMXTileAt(cx,cy)){
						if(ox < cx)
							wasp.animate(new long[]{150, 150, 150}, 0, 2, true);
						else
							wasp.animate(new long[]{150, 150, 150}, 4, 6, true);
					}
						
				}
												
				/* PLAYER HIT WALL */
				if(isProp(playerTile,"wall","true")) {
					playerPhysicsHandler.setVelocity(-mValueX * 50, -mValueX * 50);
					mValueX = 0; mValueY = 0;
					player.setPosition(oldPlayerFootCordinates[Constants.VERTEX_INDEX_X], oldPlayerFootCordinates[Constants.VERTEX_INDEX_Y]);					
				}
				else{
					//Save last known good coordinates for player
					oldPlayerFootCordinates[Constants.VERTEX_INDEX_X] = player.getX();
					oldPlayerFootCordinates[Constants.VERTEX_INDEX_Y] = player.getY();
					
					if(playerTile != oldTMXTile){
						if(mValueX > Math.abs(mValueY))
							player.animate(new long[]{150, 150, 150}, 3, 5, false);
						else if(mValueX < -Math.abs(mValueY))
							player.animate(new long[]{150, 150, 150}, 9, 11, false);						
						else if(mValueY > Math.abs(mValueX))
							player.animate(new long[]{150, 150, 150}, 6, 8, false);																	
						else if(mValueY < -Math.abs(mValueX))
							player.animate(new long[]{150, 150, 150}, 0, 2, false);				
						
						willow.animate(new long[]{150, 150, 150}, 6, 8, false);
					}					
				}
				
				/* BALL HIT WALL */
				if(isProp(ballTile,"wall","true") || isProp(ballTile,"sign","true")) {		
					//Bouncing routine					
					//Chapter2.this.mBounceSound.play();
					if(ballTile.getTileY() == oldBallTile.getTileY())
						mImpVectX = -mImpVectX;
					
					if(ballTile.getTileX() == oldBallTile.getTileX())
						mImpVectY = -mImpVectY;
					
					ball.setPosition(oldBallCenterCordinates[Constants.VERTEX_INDEX_X], oldBallCenterCordinates[Constants.VERTEX_INDEX_Y]);
					
					//Add a bit of acceleration to get away from walls
					mBallAcc = mBallAcc + 10;		
				}
				else{					
					//Save last known good coordinates for ball
					oldBallCenterCordinates[Constants.VERTEX_INDEX_X] = ball.getX();
					oldBallCenterCordinates[Constants.VERTEX_INDEX_Y] = ball.getY();	
				}
				
				/* BOUNCE BALL */
				if(isProp(ballTile,"bounce","true")) {		
					//Bounce and Acc
					ball.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(0.25f, 1, 1.5f), new ScaleModifier(0.25f, 1.5f, 1)));
					if(mBallAcc > 1)
						mBallAcc = mBallAcc - 2;
					
				}
				
				/* BALL HIT BRICKWALL */
				if(isProp(ballTile,"brickwall1","true") && mBallAcc > 100 && !playerNearBall) {		
					//scoreBoard.setLife(scoreBoard.getLife() + 1);					
					ballTile.setGlobalTileID(mTMXTiledMap, 54); //Pure awesome!!! It does change the tile!!!
					incXP(5, ballTile.getTileX(), ballTile.getTileY(), false);
				}
				else if(isProp(ballTile,"brickwall2","true") && mBallAcc > 100 && !playerNearBall) {		
					//scoreBoard.setLife(scoreBoard.getLife() + 1);					
					ballTile.setGlobalTileID(mTMXTiledMap, 55); //Pure awesome!!! It does change the tile!!!
					incXP(5, ballTile.getTileX(), ballTile.getTileY(), false);
				}
				else if(isProp(ballTile,"brickwall3","true") && mBallAcc > 100 && !playerNearBall) {		
					//scoreBoard.setLife(scoreBoard.getLife() + 1);					
					ballTile.setGlobalTileID(mTMXTiledMap, 56); //Pure awesome!!! It does change the tile!!!
					incXP(10, ballTile.getTileX(), ballTile.getTileY(), true);
				}
				
				/* BALL HIT CONE */
				if(isProp(ballTile,"cone1","true")) {		
					scoreBoard.setLife(scoreBoard.getLife() + 1);					
					ballTile.setGlobalTileID(mTMXTiledMap, 40); //Pure awesome!!! It does change the tile!!!
					incXP(5, ballTile.getTileX(), ballTile.getTileY(), true);
				}
				if(isProp(ballTile,"cone2","true")) {		
					scoreBoard.setLife(scoreBoard.getLife() + 1);					
					ballTile.setGlobalTileID(mTMXTiledMap, 100); //Pure awesome!!! It does change the tile!!!
					incXP(5, ballTile.getTileX(), ballTile.getTileY(), true);
				}
				
				/* BALL HIT CRATE */
				if(isProp(ballTile,"crate","true") && mBallAcc > 100 && !playerNearBall) {		
					scoreBoard.setLife(scoreBoard.getLife() + 1);					
					ballTile.setGlobalTileID(mTMXTiledMap, 40); //Pure awesome!!! It does change the tile!!!
					incXP(5, ballTile.getTileX(), ballTile.getTileY(), true);
				}
				
				/* BALL HIT CACTUS */
				if(isProp(ballTile,"cactus","true") && mBallAcc > 100 && !playerNearBall) {		
					//scoreBoard.setLife(scoreBoard.getLife() + 1);					
					ballTile.setGlobalTileID(mTMXTiledMap, 40); //Pure awesome!!! It does change the tile!!!
					incXP(10, ballTile.getTileX(), ballTile.getTileY(), true);
				}
				
				/* GOAL */
				if(isProp(ballTile,"goal","true") && !goalFlag && killedWaspFlag) {		
					Chapter2.this.mGoalSound.play();
					//GOAL!!!
					//mBallAcc = 0;
					//ball.stopAnimation();
					//Toast.makeText(mBaseContext, "GOAL!!!", Toast.LENGTH_LONG).show();
					//scoreBoard.setLife(5);
					//scoreBoard.GoalText.setText("GOAL!!!");
					incXP(100, ballTile.getTileX(), ballTile.getTileY(), false);
					showGoal();
					goalFlag = true;
				}
				
				/* PLAYER HIT CACTUS */
				if(isProp(playerTile,"cactus","true") && !recoverFlag) {							
					recoverFlag = true;
					scoreBoard.setLife(scoreBoard.getLife() - 1); //Test
					
					if(mValueX != 0.0f && mValueY != 0.0f){
						mImpVectX = mValueX / ((float) Math.sqrt(mValueX * mValueX + mValueY * mValueY));
						mImpVectY = mValueY / ((float) Math.sqrt(mValueX * mValueX + mValueY * mValueY));
					}
					else{
						final float vx = playerTile.getTileX() + (playerTile.getTileWidth() / 2)  - player.getX();
						final float vy = playerTile.getTileY() + (playerTile.getTileHeight() / 2)  - player.getY();
						mImpVectX = vx / ((float) Math.sqrt(vx * vx + vy * vy));
						mImpVectY = vy / ((float) Math.sqrt(vx * vx + vy * vy));
					}
					
					final Path path = new Path(2).to(player.getX(), player.getY()).to(player.getX() - 32*mImpVectX, player.getY() - 32*mImpVectY);
					player.registerEntityModifier(new ParallelEntityModifier(							
							new PathModifier(0.5f,path),
							new ColorModifier(0.5f,1,1,0,1,0,1)							
					));						
				}
				else
					recoverFlag = false; //Well optimally we need to make it timed
				
				
				/* PLAYER HIT SIGNS */
				if(isProp(playerTile,"sign","true") && playerTile.getTileColumn() == 13 && playerTile.getTileRow() == 30 && !signFlag1) {					
					signFlag1 = true;
					storyText.setText("The sign reads -\n\n'Kimberly Street'");
					mMainScene.setChildScene(mStoryScene, false, true, true);
				}
				if(playerTile.getTileColumn() != 13 && playerTile.getTileColumn() != 14 && playerTile.getTileRow() != 30)				
					signFlag1 = false;
				
				if(isProp(playerTile,"sign","true") && playerTile.getTileColumn() == 4 && playerTile.getTileRow() == 22 && !signFlag2) {					
					signFlag2 = true;
					storyText.setText("The sign reads -\n\n'Lord Willows Amazing Maze'");
					mMainScene.setChildScene(mStoryScene, false, true, true);
				}
				if(playerTile.getTileColumn() != 4 || playerTile.getTileRow() != 22)				
					signFlag2 = false;
				
				/* PLAYER HIT WILLOW */
				if(player.collidesWith(willow) && !willowFlag) {
					
					storyText.setText("Hey, I tell you what:," +
							"\nThe secret is sort of..." +
							"\na path to the big soccer" +
							"\nfield. Before I tell you" +
							"\nHOW to get there let us" +
							"\nplay hide and seek!'");
					
					mMainScene.setChildScene(mStoryScene, false, true, true);
					
				}
				
				if(willowFlag && !countFlag){
					willow.setPosition(5.0f * 32, 1.0f * 32);
					storyText.setText("Alright Will, fair enough" +
						"\nCounting Now, 1, 2, 3,..." +
						"\n\n\n     ...100!");
					
			
					mMainScene.setChildScene(mStoryScene, false, true, true);
					
				}
				
				if(player.collidesWith(willow) && willowFlag && countFlag && !willowFlag2){
					willow.stopAnimation();
					storyText.setText("You found me!" +
						"\nLitsten, the secret trail" +
						"\nis to the east. Seek an" +
						"\nentrance to it between the" +
						"\ntwo odd Atanodo bushes.");
					
			
					mMainScene.setChildScene(mStoryScene, false, true, true);
					willow.setPosition(-5.0f * 32, -2.0f * 32);
				}
				
				/* PLAYER HIT WASP */
				if(player.collidesWith(wasp) && !recoverFlag) {							
					recoverFlag = true;
					scoreBoard.setLife(scoreBoard.getLife() - 1); //Test
					
					if(mValueX > 0.0f && mValueY > 0.0f){
						mImpVectX = mValueX / ((float) Math.sqrt(mValueX * mValueX + mValueY * mValueY));
						mImpVectY = mValueY / ((float) Math.sqrt(mValueX * mValueX + mValueY * mValueY));
					}
					else{
						final float vx = wasp.getX() - player.getX();
						final float vy = wasp.getY() - player.getY();
						mImpVectX = vx / ((float) Math.sqrt(vx * vx + vy * vy));
						mImpVectY = vy / ((float) Math.sqrt(vx * vx + vy * vy));
					}							
					
					final Path path = new Path(2).to(player.getX(), player.getY()).to(player.getX() - 32*mImpVectX, player.getY() - 32*mImpVectY);
					player.registerEntityModifier(new ParallelEntityModifier(							
							new PathModifier(0.4f,path),
							new ColorModifier(0.5f,1,1,0,1,0,1)							
					));						
				}
				else
					recoverFlag = false; //Well optimally we need to make it timed
				
				/* BALL HIT WASP */
				if(ball.collidesWith(wasp) && mBallAcc > 100 && !playerNearBall) {							
					wasp.setPosition(-100, -100);
					mBallAcc = 0;
					incXP(25, ballTile.getTileX(), ballTile.getTileY(), true);
					killedWaspFlag = true;
					willowFlag = true;
					countFlag = true;
					willowFlag2 = false;
					/*storyText.setText("Thank You!!," +
							"\nMy friends call me Will" +
							"\nby the way! Hey, Score a" +
							"\ngoal and I will guide you" +
							"\nthrough our yard towards" +
							"\nKiberly street.'");
					
					mMainScene.setChildScene(mStoryScene, false, true, true);*/
				}
				else if(ball.collidesWith(wasp)){
					mImpVectX = -mImpVectX;
					mImpVectY = -mImpVectX;
				}				
				
				
				/* Collision between player and ball */
				if(player.collidesWith(ball)) {
					ball.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(0.25f, 1, 1.5f), new ScaleModifier(0.25f, 1.5f, 1)));
					mImpVectX = mValueX;
					mImpVectY = mValueY;
					
					mBallAcc = 150; //Impact ball velocity
					
					if(mValueX < 0)
						ball.animate(new long[]{100, 100, 100, 100, 100, 100, 100}, 0, 6, true);	
					else
						ball.animate(new long[]{100, 100, 100, 100, 100, 100, 100}, 6, 0, true);
				}
				
				/* Shoot Ball */
				if(shootBall && playerNearBall){
					Chapter2.this.mKickSound.play();
					mImpVectX = mValueX / (float) Math.sqrt(mValueX * mValueX + mValueY * mValueY);
					mImpVectY = mValueY / (float) Math.sqrt(mValueX * mValueX + mValueY * mValueY);
					mBallAcc = 200;
					
					if(mValueX < 0)
						ball.animate(new long[]{100, 100, 100, 100, 100, 100, 100}, 0, 6, true);	
					else
						ball.animate(new long[]{100, 100, 100, 100, 100, 100, 100}, 6, 0, true);					
				}
				
				if(mBallAcc > 0 && mBallAcc < 400){
					mBallAcc--;					
				}
				else{
					mBallAcc = 0;
					ball.stopAnimation();
				}				
				
				/* Game Over? */
				if(scoreBoard.getLife() < 1)
					gameOverFlag = true;
				
				if(gameOverFlag){
					mMainScene.setChildScene(mGameOverScene, false, true, true);
				}
				
				/* Next Level */
				if((playerTile.getTileColumn() == 38 && (playerTile.getTileRow() == 35 || playerTile.getTileRow() == 36)) && !nextChapter){
					nextChapter = true;
					endTimer = Chapter2.this.mEngine.getSecondsElapsedTotal();
					
				}
				
				if(nextChapter && (endTimer + 1.0f) < Chapter2.this.mEngine.getSecondsElapsedTotal()){
					//Plus some time...
					//Next level!					
					nextChapter = false;
					
					Intent mainIntent = new Intent(Chapter2.this,ChapterHandler.class);
					mainIntent.putExtra("CURRENT_CHAPTER", THIS_CHAPTER + 1);
					mainIntent.putExtra("CURRENT_XP", xp);
					mainIntent.putExtra("CURRENT_MAX_LIFE", scoreBoard.getMaxLife());
					Chapter2.this.startActivity(mainIntent);
					Chapter2.this.finish();
				}
				
				ballPhysicsHandler.setVelocity(mImpVectX * mBallAcc, mImpVectY * mBallAcc);
				playerPhysicsHandler.setVelocity(mValueX * 75, mValueY * 75);
				/*if(mValueX == 0 && mValueY == 0)
					player.setPosition(oldPlayerFootCordinates[Constants.VERTEX_INDEX_X], oldPlayerFootCordinates[Constants.VERTEX_INDEX_Y]);*/
				//ball.animate(new long[]{150, 150, 150, 150, 150, 150, 150}, 0, 6, false);
				
				//Update scoreboard
				scoreBoard.ScoreText.setText(" XP: " + Integer.toString(xp));
				scoreBoard.DebugText.setText("D: " + Integer.toString(playerTile.getGlobalTileID() - 1) + "," + Boolean.toString(isProp(playerTile,"stone","true")) + "," + Integer.toString(playerTile.getTileColumn()) + "," + Integer.toString(playerTile.getTileRow()));
				
				//Save tiles
				oldTMXTile = playerTile;
				oldBallTile = ballTile;
				shootBall = false;
			}
		});
		//this.mMainScene.getFirstChild().attachChild(tmxLayer);
		this.mMainScene.getLastChild().attachChild(ball);
		this.mMainScene.getLastChild().attachChild(player);
		this.mMainScene.getLastChild().attachChild(wasp);		
		this.mMainScene.getLastChild().attachChild(willow);	
		//Match timer?
		/*mMainScene.registerUpdateHandler(new TimerHandler(1 / 2.0f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				//elapsedText.setText("Seconds elapsed: " + Chapter2.this.mEngine.getSecondsElapsedTotal());
				//Wasp routine
				
				wasp.animate(new long[]{160, 160, 160}, 0, 2, false);
			}
		}));*/
		/*final Rectangle Rect = new Rectangle(0,0, CAMERA_WIDTH, CAMERA_HEIGHT);
		Rect.setColor(0.0f, 0.0f, 0.0f); // Whatever color you fancy
		Rect.registerEntityModifier(new SequenceEntityModifier(				
				new DelayModifier(1.0f),
								
				new AlphaModifier(1.0f, 0.0f, 1.0f)
				//new ColorModifier(1.2f,1.0f,0.0f,1.0f,0.0f,1.0f,0.0f),
				
		));				
		this.mMainScene.getLastChild().attachChild(Rect);*/
		
		
		this.analogOnScreenControl = new AnalogOnScreenControl(0, CAMERA_HEIGHT - this.mOnScreenControlBaseTextureRegion.getHeight(), this.mBoundChaseCamera, this.mOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.1f, 200, new IAnalogOnScreenControlListener() {
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
				//Set global movement vector				
				mValueX = pValueX;
				mValueY = pValueY;				
			}

			@Override
			public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
				//TODO: create a global click and move this into the main action loop! Then only activate when player and ball are close
				shootBall = true;
				/*if (mBallAcc > 0)
					mBallAcc = mBallAcc + 200;*/
			}
		});
		
		this.analogOnScreenControl.getControlBase().setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.analogOnScreenControl.getControlBase().setAlpha(0.5f);
		this.analogOnScreenControl.getControlBase().setScaleCenter(0, 128);
		this.analogOnScreenControl.getControlBase().setScale(1.00f);
		this.analogOnScreenControl.getControlKnob().setScale(1.00f);
		this.analogOnScreenControl.refreshControlKnobPosition();

		this.mMainScene.setChildScene(analogOnScreenControl);
				
		return this.mMainScene;		
	}

	@Override
	public void onLoadComplete() {

	}
	
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if(DEBUG_MODE)
				Log.d(TAG, "Menu Button Pressed" );
			
			
			//this.mMainScene.clearChildScene();
			//if(this.mMainScene.hasChildScene()) {
				/* Remove the menu and reset it. */
				
			//	this.mMenuScene.back();
			//this.mMainScene.getLastChild().attachChild(mMenuScene);
			//} else {
				/* Attach the menu. */
				this.mMainScene.setChildScene(this.mMenuScene, false, true, true);
				
				
			//}
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}

	@Override
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
			case MENU_RESET:
				/* Restart the animation. */
				//this.mMainScene.reset();

				/* Remove the menu and reset it. */
				this.mMainScene.clearChildScene();		
				Intent mainIntent = new Intent(Chapter2.this,Chapter2.class);
				Chapter2.this.startActivity(mainIntent);
				Chapter2.this.finish();
				//this.mMainScene.setChildScene(analogOnScreenControl);
				return true;
			case MENU_RESUME:
				/* Restart the animation. */
				//this.mMainScene.reset();

				/* Remove the menu and reset it. */
				this.mMainScene.clearChildScene();		
				//this.mMenuScene.clearChildScene();	
				//this.mMenuScene.reset();
				
				//this.mMainScene.reset();
				this.mMainScene.setChildScene(analogOnScreenControl);
				return true;
			case MENU_QUIT:
				/* End Activity. */
				//this.finish();
				this.mMainScene.clearChildScene();		
				Intent mainIntent2 = new Intent(Chapter2.this,MainMenu.class);
				Chapter2.this.startActivity(mainIntent2);
				Chapter2.this.finish();
				return true;
			case MENU_OK:
				this.mMainScene.clearChildScene();		
				//this.mMenuScene.clearChildScene();	
				//this.mMenuScene.reset();
				if(!willowFlag){
					this.willowFlag = true;
				}
				else if(this.willowFlag && !this.countFlag){
					this.countFlag = true;					
				}
				else if(this.willowFlag && this.countFlag && !this.willowFlag2){
					this.willowFlag2 = true;
				}
				//this.mMainScene.reset();
				this.mMainScene.setChildScene(analogOnScreenControl);
				
				//this.analogOnScreenControl.clearTouchAreas();
				
				return true;				
			default:
				return false;
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================
	
	private boolean isProp(TMXTile tile, String prop, String value){
		if(mTMXTiledMap.getTMXTileProperties(tile.getGlobalTileID()) != null && mTMXTiledMap.getTMXTileProperties(tile.getGlobalTileID()).containsTMXProperty(prop, value) == true)
			return true;
		else
			return false;
	}
	
	protected MenuScene createMenuScene() {
		final MenuScene menuScene = new MenuScene(this.mBoundChaseCamera);
		
		menuScene.setBackgroundEnabled(false);
		
		final Rectangle Rect = new Rectangle(0,0, CAMERA_WIDTH, CAMERA_HEIGHT);
		Rect.setColor(1.0f, 1.0f, 1.0f); // Whatever color you fancy
		Rect.setAlpha(0.4f);		
		//Rect.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.getFirstChild().attachChild(Rect);

		final IMenuItem resetMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_RESUME, this.mFont, "RESUME"), 1.0f,1.0f,1.0f, 0.0f,0.0f,0.0f);
		resetMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(resetMenuItem);

		final IMenuItem quitMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_QUIT, this.mFont, "MAIN MENU"), 1.0f,1.0f,1.0f, 0.0f,0.0f,0.0f);
		quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(quitMenuItem);
		
		menuScene.buildAnimations();				

		menuScene.setOnMenuItemClickListener(this);
		return menuScene;
	}
	
	protected MenuScene createGameOverScene() {
		final MenuScene gameOverScene = new MenuScene(this.mBoundChaseCamera);
		
		gameOverScene.setBackgroundEnabled(false);
		
		final Rectangle Rect = new Rectangle(0,0, CAMERA_WIDTH, CAMERA_HEIGHT);
		Rect.setColor(1.0f, 1.0f, 1.0f); // Whatever color you fancy
		Rect.setAlpha(0.4f);		
		//Rect.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gameOverScene.getFirstChild().attachChild(Rect);
		
		final ChangeableText storyText = new ChangeableText(24, 48, mMenuFont, "GAME OVER");
		storyText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gameOverScene.getFirstChild().attachChild(storyText);

		final IMenuItem resetMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_RESET, this.mFont, "CONTINUE"), 1.0f,1.0f,1.0f, 0.0f,0.0f,0.0f);
		resetMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gameOverScene.addMenuItem(resetMenuItem);

		final IMenuItem quitMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_QUIT, this.mFont, "MAIN MENU"), 1.0f,1.0f,1.0f, 0.0f,0.0f,0.0f);
		quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gameOverScene.addMenuItem(quitMenuItem);
		
		gameOverScene.buildAnimations();				

		gameOverScene.setOnMenuItemClickListener(this);
		return gameOverScene;
	}
	
	protected MenuScene createStoryScene() {
		final MenuScene storyScene = new MenuScene(this.mBoundChaseCamera);
		
		storyScene.setBackgroundEnabled(false);
				
		final Sprite storyPanel = new Sprite(72,40, Chapter2.this.mStoryPanelRegion);
		storyPanel.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        storyScene.getFirstChild().attachChild(storyPanel);        
        
		storyText = new ChangeableText(72+16, 40+16, mFont2, "0********1*********2*****\n0********1*********2*****\n0********1*********2*****\n0********1*********2*****\n0********1*********2*****\n0********1*********2*****");
		storyText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		//storyText.setColor(0.0f, 0.0f, 0.0f);
		storyScene.getFirstChild().attachChild(storyText);	

		final IMenuItem okMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_OK, this.mMenuFont, "[ OK ]"), 1.0f,1.0f,1.0f, 1.0f,1.0f,1.0f);
		okMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		okMenuItem.setScale(0.5f);
		//okMenuItem.setPosition(128, 128);
		storyScene.addMenuItem(okMenuItem);
		storyScene.addMenuItem(okMenuItem);
		storyScene.addMenuItem(okMenuItem);
		storyScene.addMenuItem(okMenuItem);				

		storyScene.buildAnimations();				

		storyScene.setOnMenuItemClickListener(this);
						
		return storyScene;
	}
		
	public void incXP(int ixp, float ix, float iy, boolean makeDust){
		xp += ixp;
		final ChangeableText pointsText = new ChangeableText(ix, iy, Chapter2.this.mXPFont, "+" + Integer.toString(ixp));
		pointsText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
							
		final Path path = new Path(2).to(ix, iy).to(ix, iy - 64);
		pointsText.registerEntityModifier(new ParallelEntityModifier(
				new AlphaModifier(2.8f, 1.0f, 0.0f),
				new PathModifier(3.0f,path),
				new ScaleModifier(3.0f, 1.0f, 1.75f)
		));		
		
		final AnimatedSprite dust = new AnimatedSprite(ix, iy, this.mDustTextureRegion.clone());
		dust.animate(new long[]{150, 150, 150, 150, 150, 150, 150, 150}, 0, 7, false);
				
		if(makeDust)
			Chapter2.this.mMainScene.getLastChild().attachChild(dust);
		Chapter2.this.mMainScene.getLastChild().attachChild(pointsText);
	}
	
	public void showGoal(){		
		final ChangeableText pointsText = new ChangeableText(124, 120, Chapter2.this.mMenuFont, "GOAL!");
		pointsText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		pointsText.setAlpha(0.0f);		
		
		pointsText.registerEntityModifier(new SequenceEntityModifier(				
				new ParallelEntityModifier(
					new AlphaModifier(0.9f, 0.0f, 1.0f),
					new ScaleModifier(1.0f, 0.0f, 1.9f)),
				new DelayModifier(2.0f),
				new AlphaModifier(1.5f, 1.0f, 0.0f)						
		));		
		Chapter2.this.scoreBoard.getFirstChild().attachChild(pointsText);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
    public class ScoreBoard extends HUD {
        
    	//ToDo: implement scoreboard. Now we just use it for debugging ;)
        //private int score;
    	private final ChangeableText DebugText;
        private final ChangeableText ScoreText;            
        
        private final List<AnimatedSprite> CardSprite;
        private int life;
        protected static final int MAX_LIFE = 16;
        private int maxLife;
        private final TiledTextureRegion cardTexture;
                       
        private int getLife(){
        	return life;
        }
        
        private int getMaxLife(){
        	return maxLife;
        }
        
        private void setMaxLife(int ml){
        	if(ml <= MAX_LIFE)
        		maxLife = ml;
        	else
        		maxLife = MAX_LIFE;
        	
        }
        
        private void setLife(int li){
        	if(li >= 0 && li <= maxLife)
        		life = li;
        	else if(li > maxLife)
        		life = maxLife;
        	else
        		life = 0;
        	
        	Log.d(TAG, "LIFE:" + Integer.toString(life) + " MAXLIFE:" + Integer.toString(maxLife));
        	
        	for(int i = 1; i <= MAX_LIFE; i++){        		
            	if(i  <= life && i > 1 && i % 2 == 0){            		
	            	CardSprite.get((i / 2) - 1).setCurrentTileIndex(3);    		            
	        	}            	
            	else if (i  > life && i % 2 == 0)
            		CardSprite.get((i / 2) - 1).setCurrentTileIndex(0);
            	            		
        	}
        	
        	if(life % 2 == 1){
        		
        		CardSprite.get((life - 1)/2).setCurrentTileIndex(2);  
        		
        	}
        	
        	if(life == 0)
        		CardSprite.get(0).setCurrentTileIndex(0);
        }
     
        public ScoreBoard(float pX, float pY, Font pFont, TiledTextureRegion cT, final Camera pCamera) {
            cardTexture = cT;
            life = 6; //INITIAL LIFE VALUE
        	
        	ScoreText = new ChangeableText(pX, pY, pFont, " XP: 0   ");
            this.attachChild(ScoreText);
            
            DebugText = new ChangeableText(180, 280, pFont, "D:                 ");
            if(DEBUG_MODE){	            
	            this.attachChild(DebugText);
            }
            
            setMaxLife(life);
            
            CardSprite = new LinkedList<AnimatedSprite>();
            for(int i = 0; i < (MAX_LIFE / 2); i++){            	
            	CardSprite.add(new AnimatedSprite(440 - (i * 32), 0, this.cardTexture.clone()));
	            CardSprite.get(i).setScale(0.75f);  	            	
	            //CardSprite.get(i).animate(new long[]{0,0,100}, 0, 2, false);
	            this.attachChild(CardSprite.get(i));
            }
            
            setLife(life);            	
        }     
    }   

}
