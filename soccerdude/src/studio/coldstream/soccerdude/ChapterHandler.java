package studio.coldstream.soccerdude;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.ColorModifier;
import org.anddev.andengine.entity.modifier.DelayModifier;
import org.anddev.andengine.entity.modifier.ParallelEntityModifier;

import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;


/**
 * @author Scalar
 * Well, well This is the chapter handler which shows chapter text - launches the 
 * correct level (depending on if its new game or continue?) from previous session or level.
 * Store and retrieve user data
 * 
 */
public class ChapterHandler extends BaseExample {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final boolean DEBUG_MODE = false;

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	
	private static final int MIN_MAX_LIFE = 6;
	
	private static final String TAG = "CHAPTER_HANDLER";
	
	
	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;	
		
	private Texture mFontTexture;
	private Font mFont;
	private Texture mMenuFontTexture;
	private Font mMenuFont;
	
	protected Scene mMainScene;	
	private ChangeableText splashText; 
	
	private int currentChapter;
	private int currentXP;
	private int currentMaxLife;

	private SharedPreferences app_preferences;
	private SharedPreferences prefsPrivate;
	private SharedPreferences.Editor editor;
	private SharedPreferences.Editor prefsEditor;
	private int programCounter;
	
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
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
				
		this.mFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = new Font(this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 22, true, Color.WHITE);
		this.mMenuFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuFont = new Font(this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.WHITE);
		
		
		this.mEngine.getTextureManager().loadTextures(this.mFontTexture, this.mMenuFontTexture);
		this.mEngine.getFontManager().loadFonts(this.mFont, this.mMenuFont);
		
		app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        programCounter = app_preferences.getInt("pcounter", 0);
        
        //A program counter
        editor = app_preferences.edit();
        editor.putInt("pcounter", ++programCounter);
        editor.commit(); // Very important
		
        Intent data = this.getIntent();
	    currentChapter = data.getIntExtra("CURRENT_CHAPTER", 0);
	    if(DEBUG_MODE) currentChapter = 2; //Set chapter in debug mode
		if(DEBUG_MODE) Log.d(TAG, Integer.toString(currentChapter));
		if(currentChapter < 1){ //Continue Sent	from Main Menu		        
	        // App's private pref's
	        prefsPrivate = getSharedPreferences("preferences", Context.MODE_PRIVATE);
	        currentChapter = prefsPrivate.getInt("CurrentChapter", 1);
	        currentXP = prefsPrivate.getInt("currentXP", 0);
	        currentMaxLife = prefsPrivate.getInt("currentMaxLife", MIN_MAX_LIFE);
	        
	        Log.d(TAG, "CHAPTERLIFE:" + Integer.toString(currentMaxLife));
	        
	        if(currentMaxLife < MIN_MAX_LIFE)
				currentMaxLife = MIN_MAX_LIFE;
		}		
		else if(currentChapter == 1){
			currentXP = 0;
			currentMaxLife = MIN_MAX_LIFE;
			
			prefsPrivate = getSharedPreferences("preferences", Context.MODE_PRIVATE);
	        prefsEditor = prefsPrivate.edit();
	        prefsEditor.putInt("CurrentChapter", 1);        
	        prefsEditor.putInt("currentXP", 0);
	        prefsEditor.putInt("currentMaxLife", MIN_MAX_LIFE);
	        prefsEditor.commit(); // Very important*/
	        
	        Log.d(TAG, "CHAPTERLIFE:" + Integer.toString(currentMaxLife));
			
		}
		else if(currentChapter > 1){
			//Update from Intent and Store!
			currentXP = data.getIntExtra("CURRENT_XP", 0);
			currentMaxLife = data.getIntExtra("CURRENT_MAX_LIFE", MIN_MAX_LIFE);
			
			Log.d(TAG, "CHAPTERLIFE:" + Integer.toString(currentMaxLife));
			
			if(currentMaxLife < MIN_MAX_LIFE)
				currentMaxLife = MIN_MAX_LIFE;
			
			prefsPrivate = getSharedPreferences("preferences", Context.MODE_PRIVATE);
	        prefsEditor = prefsPrivate.edit();
	        prefsEditor.putInt("CurrentChapter", currentChapter);        
	        prefsEditor.putInt("currentXP", currentXP);
	        prefsEditor.putInt("currentMaxLife", currentMaxLife);
	        prefsEditor.commit(); // Very important*/
			
		}		
		
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		//this.mMenuScene = this.createMenuScene();
		
		this.mMainScene = new Scene(2);
		this.mMainScene.setBackground(new ColorBackground(0.0f, 0.0f, 0.0f));		
		
		if(currentChapter == 1){
			splashText = new ChangeableText(24, 32, mFont, "- CHAPTER 1 -\n\n" +
					"They call me Kid and this is my story.\n" +
					"One day while kicking ball in the \n" +
					"backyard, I suddenly heard a \n" +
					"laud scream echoing from deep within\n" +
					"the woods...");
		}
		else if(currentChapter == 2){
			splashText = new ChangeableText(24, 32, mFont, "- CHAPTER 2 -\n\n" +
					"My new friend Will lives in a fancy \n" +
					"place on top of a hill. But all I whish\n" +
					"for is to become a football pro. I want\n" +
					"to play in a team, in a league, in a \n" +
					"world championship! To be - a star...");
		}
		else if(currentChapter == 3){
			splashText = new ChangeableText(24, 32, mFont, "- CHAPTER 3 -\n\n" +
					"The trail ahead looks - frightful...\n" +
					"Will had to pick something up but said\n" +
					"he would team up later on. Im quite\n" +
					"qurious about this soccer field though.\n" +
					"The sun is getting low, perhaps I can\n" +
					"find my way through before it gets dark.");
		}
		else if(currentChapter == 4){
			splashText = new ChangeableText(24, 32, mFont, "- TO BE CONTINUED -\n\n" +
					"The story does not end here of course :)\n" +
					"Please stay tuned as we work on updates!\n" +
					"If you liked the game so far don't \n" +
					"forget to rate it on Android Market!\n\n" +
					"Yours - Team Studio Coldstream");
		}
		
		splashText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		splashText.setScale(0.0f);
		//final Path path = new Path(2).to(0 - this.mFaceTextureRegion2.getWidth() - 32, centerY2 + this.mFaceTextureRegion2.getHeight() + 34).to(centerX2 - 30, centerY2 + this.mFaceTextureRegion2.getHeight() + 34);
		splashText.registerEntityModifier(new SequenceEntityModifier(				
				new DelayModifier(2.0f),
				new ParallelEntityModifier(
					//new AlphaModifier(1.9f, 0.0f, 1.0f),
					//new ScaleModifier(1.0f, 0.0f, 1.0f)),
				new ScaleModifier(0.1f, 0.0f, 1.0f),
				//new PathModifier(0.4f,path),
				new ColorModifier(2.0f,0.0f,1.0f,0.0f,1.0f,0.0f,1.0f)),
				new DelayModifier(4.0f),
				//new AlphaModifier(1.5f, 0.0f, 1.0f),
				new ColorModifier(2.0f,1.0f,0.0f,1.0f,0.0f,1.0f,0.0f),
				new DelayModifier(1.0f)
		));
				
	
		this.mMainScene.getFirstChild().attachChild(splashText);
		
		//Match timer?
		this.mMainScene.registerUpdateHandler(new TimerHandler(1 / 2.0f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				//elapsedText.setText("Seconds elapsed: " + MainActivity.this.mEngine.getSecondsElapsedTotal());
				if(currentChapter == 1){
					if(ChapterHandler.this.mEngine.getSecondsElapsedTotal() > 10.0f){
						Intent mainIntent = new Intent(ChapterHandler.this, MainActivity.class);
						mainIntent.putExtra("CURRENT_CHAPTER", currentChapter);
						mainIntent.putExtra("CURRENT_XP", currentXP);
						mainIntent.putExtra("CURRENT_MAX_LIFE", currentMaxLife);
						ChapterHandler.this.startActivity(mainIntent);
						ChapterHandler.this.finish();
					}
				}
				else if(currentChapter == 2){
					if(ChapterHandler.this.mEngine.getSecondsElapsedTotal() > 10.0f){
						Intent mainIntent = new Intent(ChapterHandler.this, Chapter2.class);
						mainIntent.putExtra("CURRENT_CHAPTER", currentChapter);
						mainIntent.putExtra("CURRENT_XP", currentXP);
						mainIntent.putExtra("CURRENT_MAX_LIFE", currentMaxLife);
						ChapterHandler.this.startActivity(mainIntent);
						ChapterHandler.this.finish();
					}
				}
				else if(currentChapter == 3){
					if(ChapterHandler.this.mEngine.getSecondsElapsedTotal() > 10.0f){
						Intent mainIntent = new Intent(ChapterHandler.this, Chapter3.class);
						mainIntent.putExtra("CURRENT_CHAPTER", currentChapter);
						mainIntent.putExtra("CURRENT_XP", currentXP);
						mainIntent.putExtra("CURRENT_MAX_LIFE", currentMaxLife);
						ChapterHandler.this.startActivity(mainIntent);
						ChapterHandler.this.finish();
					}
				}
				else if(currentChapter == 4){
					if(ChapterHandler.this.mEngine.getSecondsElapsedTotal() > 10.0f){
						Intent mainIntent = new Intent(ChapterHandler.this, MainMenu.class);
						mainIntent.putExtra("CURRENT_CHAPTER", currentChapter);
						mainIntent.putExtra("CURRENT_XP", currentXP);
						mainIntent.putExtra("CURRENT_MAX_LIFE", currentMaxLife);
						ChapterHandler.this.startActivity(mainIntent);
						ChapterHandler.this.finish();
					}
				}
				
				/*if(ChapterHandler.this.mEngine.getSecondsElapsedTotal() > 5.0f && !menuFlag){
					menuFlag = true;
					ChapterHandler.this.mMainScene.setChildScene(mMenuScene, false, true, true);
				}*/
					
			}
		}));
		
		
		return this.mMainScene;
	}

	@Override
	public void onLoadComplete() {
		
	}
	
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if(DEBUG_MODE) Log.d(TAG, "Menu Button Pressed" );			
		
			Intent mainIntent = new Intent(ChapterHandler.this, MainMenu.class);			
			ChapterHandler.this.startActivity(mainIntent);
			ChapterHandler.this.finish();
			
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================
	


	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
