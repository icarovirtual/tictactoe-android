package apps.games.tictactoe;

import org.andengine.util.debug.Debug;
import org.andengine.entity.scene.Scene;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.engine.options.EngineOptions;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;

import android.content.Intent;
import android.annotation.SuppressLint;

public class MainActivity extends BaseGameActivity
{

	public static final int CAMERA_WIDTH = 320;
	public static final int CAMERA_HEIGHT = 480;

	private BitmapTextureAtlas bgTexture;
	private BitmapTextureAtlas titleTexture;
	private ITextureRegion bgTextureRegion;
	private ITextureRegion titleTextureRegion;

	private BuildableBitmapTextureAtlas buttonsTexture;
	private ITextureRegion singlePlayButton;
	private ITextureRegion twoPlayButton;
	private ITextureRegion exitButton;

	private Scene mainScene;

	private Camera mainCamera;

	@Override
	public EngineOptions onCreateEngineOptions()
	{
		this.mainCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		final EngineOptions engineOptions =
				new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mainCamera);

		return engineOptions;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback)
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		bgTexture = new BitmapTextureAtlas(getTextureManager(),
				CAMERA_WIDTH, CAMERA_HEIGHT, TextureOptions.DEFAULT);
		bgTextureRegion = BitmapTextureAtlasTextureRegionFactory.
				createFromAsset(bgTexture, this, "bg1.png", 0, 0);
		bgTexture.load();

		titleTexture = new BitmapTextureAtlas(getTextureManager(),
				245, 141, TextureOptions.DEFAULT);
		titleTextureRegion = BitmapTextureAtlasTextureRegionFactory.
				createFromAsset(titleTexture, this, "title.png", 0, 0);
		titleTexture.load();

		buttonsTexture = new BuildableBitmapTextureAtlas(getTextureManager(), CAMERA_WIDTH, CAMERA_HEIGHT);
		singlePlayButton = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(buttonsTexture, this, "butSingle.png");
		twoPlayButton = BitmapTextureAtlasTextureRegionFactory
			.createFromAsset(buttonsTexture, this, "butTwo.png");
		exitButton = BitmapTextureAtlasTextureRegionFactory
			.createFromAsset(buttonsTexture, this, "butExit.png");

		try
		{
		    this.buttonsTexture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
		    this.buttonsTexture.load();
		}
		catch (final TextureAtlasBuilderException e)
		{
		        Debug.e(e);
		}

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
	{
		mainScene = new Scene();
		pOnCreateSceneCallback.onCreateSceneFinished(mainScene);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback)
	{
		final Sprite bgSprite =
				new Sprite(0, 0, bgTextureRegion, getVertexBufferObjectManager());

		final int BUTTONS_SPACING = 90;
		final int TITLE_SPACING = 35;
		final Sprite singlePlaySprite =
				new Sprite(CAMERA_WIDTH/2 - singlePlayButton.getWidth()/2,
						CAMERA_HEIGHT/2 - singlePlayButton.getHeight()/2,
						singlePlayButton, getVertexBufferObjectManager());
		final Sprite twoPlaySprite =
				new Sprite(CAMERA_WIDTH/2 - twoPlayButton.getWidth()/2,
						CAMERA_HEIGHT/2 - twoPlayButton.getHeight()/2 +BUTTONS_SPACING,
						twoPlayButton, getVertexBufferObjectManager())
		{
			@SuppressLint("NewApi")
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				// CHANGE ACTIVITY ON POINTER
				if(pSceneTouchEvent.isActionDown())
				{
					startActivity(new Intent(MainActivity.this, BoardActivity.class));
				}
				return true;
			}
		};
		final Sprite exitSprite =
				new Sprite(CAMERA_WIDTH/2 - exitButton.getWidth()/2,
						CAMERA_HEIGHT/2 - exitButton.getHeight()/2 +BUTTONS_SPACING+BUTTONS_SPACING,
						exitButton, getVertexBufferObjectManager())
		{
			@SuppressLint("NewApi")
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if(pSceneTouchEvent.isActionDown())
				{
					System.exit(0);
				}
				return true;
			}
		};
		
		final Sprite titleSprite =
				new Sprite(CAMERA_WIDTH/2 - titleTexture.getWidth()/2,
						TITLE_SPACING, titleTextureRegion, getVertexBufferObjectManager());

		final ParallaxBackground mainBackground = new ParallaxBackground(0, 0, 0);
		mainBackground.attachParallaxEntity(new ParallaxEntity(0, bgSprite));

		mainScene.setBackground(mainBackground);

		mainScene.attachChild(singlePlaySprite);
		mainScene.attachChild(twoPlaySprite);
		mainScene.attachChild(exitSprite);
		mainScene.attachChild(titleSprite);

		mainScene.registerTouchArea(twoPlaySprite);
		mainScene.registerTouchArea(exitSprite);

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
}
