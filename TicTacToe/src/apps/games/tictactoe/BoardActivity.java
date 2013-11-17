package apps.games.tictactoe;

import org.andengine.util.color.Color;
import org.andengine.opengl.font.Font;
import org.andengine.entity.text.Text;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.engine.camera.Camera;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.FontFactory;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.engine.options.EngineOptions;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;

import android.os.Bundle;
import android.graphics.Typeface;
import android.annotation.SuppressLint;

import apps.game.tictactoe.gameplay.IconsController;
import apps.game.tictactoe.gameplay.PositionController;
import apps.game.tictactoe.util.AlertDialogFragment;

public class BoardActivity extends BaseGameActivity
{
	private static final int CAMERA_WIDTH = 320;
	private static final int CAMERA_HEIGHT = 480;

	public static final int ICON_LENGTH = 53;
	public static final int BOARD_WIDTH = 240;
	public static final int BOARD_HEIGHT = 240;

	private BitmapTextureAtlas bgTexture;
	private BitmapTextureAtlas boardTexture;
	private ITextureRegion bgTextureRegion;
	private ITextureRegion boardTextureRegion;

	private BitmapTextureAtlas crossTexture;
	private ITextureRegion crossTextureRegion;
	private BitmapTextureAtlas circleTexture;
	private ITextureRegion circleTextureRegion;

	private Sprite crossSprites[] = new Sprite[5];
	private Sprite circleSprites[] = new Sprite[5];

	private Text crossText;
	private Text circleText;
	private Text drawText;
	private Text testText;

	private Scene boardScene;
	private Camera boardCamera;

	private AlertDialogFragment endGameDialog;

	private Bundle boardBundle;

	private IconsController iconsController = IconsController.getInstance();
	private PositionController positionController = PositionController.getInstance();


	@Override
	public EngineOptions onCreateEngineOptions()
	{
		this.boardCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		final EngineOptions engineOptions =
			new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
          	new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), boardCamera);

		return engineOptions;
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback)
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		bgTexture = new BitmapTextureAtlas(getTextureManager(), 320, 480, TextureOptions.DEFAULT);
		bgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bgTexture, this, "bg1.png", 0, 0);
		bgTexture.load();

		boardTexture = new BitmapTextureAtlas(getTextureManager(), BOARD_WIDTH, BOARD_HEIGHT, TextureOptions.DEFAULT);
		boardTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(boardTexture, this, "board1.png", 0, 0);
		boardTexture.load();

		crossTexture = new BitmapTextureAtlas(getTextureManager(), 53, 53, TextureOptions.DEFAULT);
		crossTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(crossTexture, this, "cross.png", 0, 0);
		crossTexture.load();
		circleTexture = new BitmapTextureAtlas(getTextureManager(), 53, 53, TextureOptions.DEFAULT);
		circleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(circleTexture, this, "circle.png", 0, 0);
		circleTexture.load();

		Sprite mSprite;
		for (int i = 0; i < 5; i++)
		{
			mSprite = new Sprite(0, 0, crossTextureRegion, getVertexBufferObjectManager());
			crossSprites[i] = mSprite;
			crossSprites[i].setVisible(false);
			mSprite = new Sprite(0, 0, circleTextureRegion, getVertexBufferObjectManager());
			circleSprites[i] = mSprite;
			circleSprites[i].setVisible(false);
		}

		positionController.calculateArea(CAMERA_WIDTH, CAMERA_HEIGHT);

		final Font mFont = FontFactory.create(getFontManager(), getTextureManager(), 256, 256, Typeface.SANS_SERIF, 32);
		mFont.load();

		crossText = new Text(0, 0, mFont, "CROSS Victory", getVertexBufferObjectManager());
		circleText = new Text(0, 0, mFont, "CIRCLE Victory", getVertexBufferObjectManager());
		drawText = new Text(0, 0, mFont, "DRAW", getVertexBufferObjectManager());
		testText = new Text(50, CAMERA_HEIGHT - 50, mFont, "HELLO", getVertexBufferObjectManager());

		crossText.setPosition(CAMERA_WIDTH / 2 - crossText.getWidth() / 2, CAMERA_HEIGHT / 2 - crossText.getHeight() / 2);
		circleText.setPosition(CAMERA_WIDTH / 2 - circleText.getWidth() / 2, CAMERA_HEIGHT / 2 - circleText.getHeight() / 2);
		drawText.setPosition(CAMERA_WIDTH / 2 - drawText.getWidth() / 2, CAMERA_HEIGHT / 2 - drawText.getHeight() / 2);

		crossText.setColor(Color.WHITE);
		crossText.setVisible(false);
		circleText.setVisible(false);
		circleText.setColor(Color.WHITE);
		drawText.setVisible(false);
		drawText.setColor(Color.WHITE);

		boardBundle = new Bundle();
		boardBundle.putCharSequence("positive", "Restart");
		boardBundle.putCharSequence("negative", "Exit");

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
	{
		boardScene = new Scene();
		restartGame();
		pOnCreateSceneCallback.onCreateSceneFinished(boardScene);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback)
	{
		final Sprite bgSprite = new Sprite(0, 0, bgTextureRegion, getVertexBufferObjectManager())
		{
			@SuppressLint("NewApi")
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				// DRAW SPRITES ON POINTER
				if (pSceneTouchEvent.isActionDown())
				{
					int posX = positionController.checkBoardCollumn((int) pTouchAreaLocalX);
					int posY = positionController.checkBoardLine((int) pTouchAreaLocalY);

					if (posX != PositionController.NONE && posY != PositionController.NONE)
					{
						iconsController.nextTurn(posX, posY);
						if (iconsController.currentIcon == IconsController.CROSS)
						{

							crossSprites[iconsController.crossIndex].setX(PositionController.currentX);
							crossSprites[iconsController.crossIndex].setY(PositionController.currentY);

							crossSprites[iconsController.crossIndex].setVisible(true);

							if (!crossSprites[iconsController.crossIndex].hasParent())
								boardScene.attachChild(crossSprites[iconsController.crossIndex]);
						} else if (iconsController.currentIcon == IconsController.CIRCLE)
						{
							circleSprites[iconsController.circleIndex].setX(PositionController.currentX);
							circleSprites[iconsController.circleIndex].setY(PositionController.currentY);

							circleSprites[iconsController.circleIndex].setVisible(true);

							if (!circleSprites[iconsController.circleIndex].hasParent())
								boardScene.attachChild(circleSprites[iconsController.circleIndex]);
						} else if (iconsController.currentIcon == IconsController.NONE)
						{
							System.out.println("\nEnd game\n");
						}

						iconsController.endTurn();
						updateGameState();
					}
				}
				return true;
			}
		};

		final Sprite boardSprite = new Sprite(CAMERA_WIDTH/2 - BOARD_WIDTH/2, CAMERA_HEIGHT/2 - BOARD_HEIGHT/2,
				boardTextureRegion, getVertexBufferObjectManager());

		final ParallaxBackground boardBackground = new ParallaxBackground(0, 0, 0);
		boardBackground.attachParallaxEntity(new ParallaxEntity(0, bgSprite));

		boardScene.attachChild(boardSprite);
		boardScene.attachChild(crossText);
		boardScene.attachChild(circleText);
		boardScene.attachChild(drawText);
		boardScene.attachChild(testText);

		boardScene.setBackground(boardBackground);
		boardScene.registerTouchArea(bgSprite);

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@SuppressLint("NewApi")
	private void updateGameState()
	{
		boolean restart = false;

		if (iconsController.currentState == IconsController.CROSS_WIN)
		{
//			endGameDialog = EndGameDialogFragment.newInstance("CROSS Victory!");
			boardBundle.putCharSequence("state", "CROSS Victory!");
			endGameDialog = AlertDialogFragment.newInstance(boardBundle);
			restart = true;
		}
		else if(iconsController.currentState == IconsController.CIRCLE_WIN)
		{
//			endGameDialog = EndGameDialogFragment.newInstance("CIRCLE Victory!");
			boardBundle.putCharSequence("state", "CIRCLE Victory!");
			endGameDialog = AlertDialogFragment.newInstance(boardBundle);
			restart = true;
		}
		else if(iconsController.currentState == IconsController.DRAW)
		{
//			endGameDialog = EndGameDialogFragment.newInstance("DRAW!!");
			boardBundle.putCharSequence("state", "DRAW!!");
			endGameDialog = AlertDialogFragment.newInstance(boardBundle);
			restart = true;
		}

		if (restart)
			endGameDialog.show(getFragmentManager(), "end");
	}

	public void restartGame()
	{
		for (int i = 0; i < 5; i++)
		{
			crossSprites[i].setVisible(false);
			circleSprites[i].setVisible(false);
		}

		crossText.setVisible(false);
		circleText.setVisible(false);
		drawText.setVisible(false);

		iconsController.resetBoard();
		positionController.resetPositions();
	}

	public void doPositiveClick()
	{
		restartGame();
	}

	public void doNegativeClick()
	{
		finish();
	}
}