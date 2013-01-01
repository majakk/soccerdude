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
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.modifier.PathModifier.Path;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;

/**
 * @author Scalar
 * Splash class!
 * 
 */

public class AndengineSplash extends BaseExample {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final boolean DEBUG_MODE = false;
	
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;

	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;
	
	private Texture mTexture;
	private TextureRegion mFaceTextureRegion;
	
	private Texture mTexture2;
	private TextureRegion mFaceTextureRegion2;
	
	private Texture mFontTexture;
	private Font mFont;

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
		this.mTexture = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFaceTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, this, "gfx/badge.png", 0, 0);
		
		this.mTexture2 = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFaceTextureRegion2 = TextureRegionFactory.createFromAsset(this.mTexture2, this, "gfx/coldstream.png", 0, 0);
		
		this.mEngine.getTextureManager().loadTextures(this.mTexture, this.mTexture2);
		
		this.mFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = new Font(this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 22, true, Color.WHITE);
		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.mEngine.getFontManager().loadFont(this.mFont);
		
	}

	@Override
	public Scene onLoadScene() {
		//this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene(1);
		scene.setBackground(new ColorBackground(0.0f, 0.0f, 0.0f));

		/* Calculate the coordinates for the face, so its centered on the camera. */
		final int centerX = (CAMERA_WIDTH - this.mFaceTextureRegion.getWidth()) / 2;
		final int centerY = (CAMERA_HEIGHT - this.mFaceTextureRegion.getHeight()) / 2;
		
		final int centerX2 = (CAMERA_WIDTH - this.mFaceTextureRegion2.getWidth()) / 2;
		final int centerY2 = (CAMERA_HEIGHT - this.mFaceTextureRegion2.getHeight()) / 2;

		/* Create the face and add it to the scene. */
		final Sprite splash1 = new Sprite(centerX, centerY, this.mFaceTextureRegion);
		splash1.setScale(0.0f);
				
		splash1.registerEntityModifier(new SequenceEntityModifier(				
				new DelayModifier(2.0f),
				new ParallelEntityModifier(
					//new AlphaModifier(1.9f, 0.0f, 1.0f),
					new ScaleModifier(0.8f, 0.0f, 1.0f)),
				new DelayModifier(2.0f),
				//new AlphaModifier(1.5f, 0.0f, 1.0f),
				new ColorModifier(0.8f,1.0f,0.0f,1.0f,0.0f,1.0f,0.0f),
				new DelayModifier(1.0f)
		));	
		
		final Sprite splash2 = new Sprite(centerX2, centerY2, this.mFaceTextureRegion2);
		splash2.setScale(0.0f);
				
		splash2.registerEntityModifier(new SequenceEntityModifier(				
				new DelayModifier(6.0f),
				//new ParallelEntityModifier(
					//new AlphaModifier(1.9f, 0.0f, 1.0f),
					//new ScaleModifier(1.0f, 0.0f, 1.0f)),
				new ScaleModifier(0.1f, 0.0f, 1.5f),
				//new ColorModifier(0.5f,0.0f,1.0f,0.0f,1.0f,0.0f,1.0f),
				new DelayModifier(2.0f),
				//new AlphaModifier(1.5f, 0.0f, 1.0f),
				new ColorModifier(1.2f,1.0f,0.0f,1.0f,0.0f,1.0f,0.0f),
				new DelayModifier(1.0f)
		));
		
		final ChangeableText splashText = new ChangeableText(0 - this.mFaceTextureRegion2.getWidth() - 32, centerY2 + this.mFaceTextureRegion2.getHeight() + 34, mFont, "Studio Coldstream");
		splashText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		splashText.setScale(0.0f);
		final Path path = new Path(2).to(0 - this.mFaceTextureRegion2.getWidth() - 32, centerY2 + this.mFaceTextureRegion2.getHeight() + 34).to(centerX2 - 30, centerY2 + this.mFaceTextureRegion2.getHeight() + 34);
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
		));
		
		scene.getLastChild().attachChild(splash1);
		scene.getLastChild().attachChild(splash2);
		scene.getFirstChild().attachChild(splashText);
		
		//Match timer?
		scene.registerUpdateHandler(new TimerHandler(1 / 2.0f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				//elapsedText.setText("Seconds elapsed: " + MainActivity.this.mEngine.getSecondsElapsedTotal());
				if(DEBUG_MODE){
					if(AndengineSplash.this.mEngine.getSecondsElapsedTotal() > 4.0f){
						Intent mainIntent = new Intent(AndengineSplash.this, MainMenu.class);
						AndengineSplash.this.startActivity(mainIntent);
						AndengineSplash.this.finish();
					}
				}
				else{
					if(AndengineSplash.this.mEngine.getSecondsElapsedTotal() > 10.0f){
						Intent mainIntent = new Intent(AndengineSplash.this, MainMenu.class);
						AndengineSplash.this.startActivity(mainIntent);
						AndengineSplash.this.finish();
					}
				}
					
			}
		}));

		return scene;
	}

	@Override
	public void onLoadComplete() {

	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
