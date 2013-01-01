package studio.coldstream.soccerdude;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.ColorModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.TextMenuItem;
import org.anddev.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.util.Debug;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;

/**
 * @author Scalar
 * Main Menu - Kicks off game - either continue or new game.
 * Instructions
 * Credits
 * Quit Game
 */
public class MainMenu extends BaseExample implements IOnMenuItemClickListener {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final boolean DEBUG_MODE = false;

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	
	private static final String TAG = "SOCCER_MENU";
	
	protected static final int MENU_START = 1000;
	protected static final int MENU_QUIT = MENU_START + 1;
	protected static final int MENU_CREDITS = MENU_START + 2;
	protected static final int MENU_INSTRUCTIONS = MENU_START + 3;
	protected static final int MENU_CONTINUE = MENU_START + 4;

	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;
	
	private Texture mTexture;
	private TextureRegion mFaceTextureRegion;
	
	private Texture mFontTexture;
	private Font mFont;
	private Texture mMenuFontTexture;
	private Font mMenuFont;
	private Texture mKidFontTexture;
	private Font mKidFont;
	
	private Texture mSoccerFontTexture;
	private Font mSoccerFont;
	
	protected Scene mMainScene;
	protected MenuScene mMenuScene;
	
	private boolean menuFlag;
	
	private int currentChapter;

	private SharedPreferences prefsPrivate;
	
	private Music mMusic;


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
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera).setNeedsMusic(true));
	}

	@Override
	public void onLoadResources() {
		MusicFactory.setAssetBasePath("mfx/");
		try {
			this.mMusic = MusicFactory.createMusicFromAsset(this.mEngine.getMusicManager(), this, "soccerintro.ogg");
			//this.mMusic.setLooping(true);
		} catch (final IOException e) {
			Debug.e(e);
		}
		
		TextureRegionFactory.setAssetBasePath("gfx/");
		this.mTexture = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFaceTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, this, "soccerbanner_sm.png", 0, 0);		
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
		
		this.mFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = new Font(this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 22, true, Color.WHITE);
		this.mMenuFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuFont = new Font(this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.WHITE);
		this.mKidFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mKidFont = new Font(this.mKidFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC), 32, true, Color.BLACK);		
		this.mEngine.getTextureManager().loadTextures(this.mFontTexture, this.mMenuFontTexture, this.mKidFontTexture);
		this.mEngine.getFontManager().loadFonts(this.mFont, this.mMenuFont, this.mKidFont);
		
		FontFactory.setAssetBasePath("font/");
		this.mSoccerFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mSoccerFont = FontFactory.createFromAsset(this.mSoccerFontTexture, this, "next.ttf", 48, true, Color.WHITE);
		
		this.mEngine.getTextureManager().loadTexture(this.mSoccerFontTexture);
		this.mEngine.getFontManager().loadFont(this.mSoccerFont);		
		
		menuFlag = false;
		
		// App's private pref's
        prefsPrivate = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        currentChapter = prefsPrivate.getInt("CurrentChapter", 0);        
        
        
	}

	@Override
	public Scene onLoadScene() {
		//this.mEngine.registerUpdateHandler(new FPSLogger());
		this.mMenuScene = this.createMenuScene();
		
		this.mMainScene = new Scene(2);
		this.mMainScene.setBackground(new ColorBackground(0.0f, 0.0f, 0.0f));

		/* Calculate the coordinates for the face, so its centered on the camera. */
		final int centerX = (CAMERA_WIDTH - this.mFaceTextureRegion.getWidth()) / 2;
		final int centerY = (CAMERA_HEIGHT - this.mFaceTextureRegion.getHeight()) / 2;
		
		/* Create the face and add it to the scene. */
		final Sprite splash1 = new Sprite(centerX, centerY, this.mFaceTextureRegion);
		//splash1.setScale(0.0f);
		splash1.setColor(0.0f, 0.0f, 0.0f);
				
		splash1.registerEntityModifier(new SequenceEntityModifier(							
				new ColorModifier(1.9f,0.0f,1.0f,0.0f,1.0f,0.0f,1.0f)				
		));			
		
		final ChangeableText soccerText = new ChangeableText(192, 26, mSoccerFont, "SOCCER");
		soccerText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		final ChangeableText kidText = new ChangeableText(382, 82, mKidFont, "KID");
		kidText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		//splashText.setScale(1.0f);
		/*final Path path = new Path(2).to(0 - this.mFaceTextureRegion2.getWidth() - 32, centerY2 + this.mFaceTextureRegion2.getHeight() + 34).to(centerX2 - 30, centerY2 + this.mFaceTextureRegion2.getHeight() + 34);
		splashText.registerEntityModifier(new SequenceEntityModifier(				
				new DelayModifier(6.0f),
				//new ParallelEntityModifier(
					//new AlphaModifier(1.9f, 0.0f, 1.0f),
					//new ScaleModifier(1.0f, 0.0f, 1.0f)),
				new ScaleModifier(0.1f, 0.0f, 1.0f),
				new PathModifier(0.4f,path),
				//new ColorModifier(0.5f,0.0f,1.0f,0.0f,1.0f,0.0f,1.0f),
				new DelayModifier(2.0f),
				//new AlphaModifier(1.5f, 0.0f, 1.0f),
				new ColorModifier(1.2f,1.0f,0.0f,1.0f,0.0f,1.0f,0.0f),
				new DelayModifier(1.0f)
		));*/
		
		
		//scene.getLastChild().attachChild(splash2);
		
		
		//Match timer?
		this.mMainScene.registerUpdateHandler(new TimerHandler(1 / 2.0f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				//elapsedText.setText("Seconds elapsed: " + MainActivity.this.mEngine.getSecondsElapsedTotal());
				
				
				if(MainMenu.this.mEngine.getSecondsElapsedTotal() > 2.0f && !menuFlag){
					Log.d(TAG, "Set Menu Screen" );
					menuFlag = true;
					MainMenu.this.mMainScene.setChildScene(mMenuScene, false, true, true);
					MainMenu.this.mMusic.play();
				}
					
			}
		}));
		
		this.mMainScene.getLastChild().attachChild(splash1);
		this.mMainScene.getLastChild().attachChild(soccerText);
		this.mMainScene.getLastChild().attachChild(kidText);
				
		return this.mMainScene;
	}

	@Override
	public void onLoadComplete() {
		//this.mMainScene.setChildScene(mMenuScene, false, true, true);
		
	}
	
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if(DEBUG_MODE) Log.d(TAG, "Menu Button Pressed" );			
		
			this.mMainScene.setChildScene(this.mMenuScene, false, true, true);	
			
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}

	@Override
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
			case MENU_CONTINUE:
				Intent mainIntent = new Intent(MainMenu.this, ChapterHandler.class);
				mainIntent.putExtra("CURRENT_CHAPTER", 0);
				MainMenu.this.startActivity(mainIntent);
				MainMenu.this.finish();
				
				return true;
			case MENU_START:
					
				Intent mainIntent2 = new Intent(MainMenu.this, ChapterHandler.class);
				mainIntent2.putExtra("CURRENT_CHAPTER", 1);
				MainMenu.this.startActivity(mainIntent2);
				MainMenu.this.finish();
				
				return true;
			
			case MENU_QUIT:
				/* End Activity. */
				MainMenu.this.finish();
				return true;
			
			default:
				return false;
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================
	
	protected MenuScene createMenuScene() {
		final MenuScene menuScene = new MenuScene(this.mCamera);
		
		menuScene.setBackgroundEnabled(false);
		
		final Rectangle Rect = new Rectangle(0,0, CAMERA_WIDTH, CAMERA_HEIGHT);
		Rect.setColor(1.0f, 1.0f, 1.0f); // Whatever color you fancy
		Rect.setAlpha(0.6f);		
		//Rect.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.getLastChild().attachChild(Rect);

		if(currentChapter > 0){
			final IMenuItem continueMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_CONTINUE, this.mMenuFont, "CONTINUE"), 1.0f,1.0f,1.0f, 0.0f,0.0f,0.0f);
			continueMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			menuScene.addMenuItem(continueMenuItem);
		}
		
		final IMenuItem resetMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_START, this.mMenuFont, "NEW GAME"), 1.0f,1.0f,1.0f, 0.0f,0.0f,0.0f);
		resetMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(resetMenuItem);

		final IMenuItem quitMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_QUIT, this.mMenuFont, "QUIT GAME"), 1.0f,1.0f,1.0f, 0.0f,0.0f,0.0f);
		quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(quitMenuItem);		
		
		menuScene.buildAnimations();				

		menuScene.setOnMenuItemClickListener(this);
		return menuScene;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
