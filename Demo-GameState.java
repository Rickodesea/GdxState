package com.algodal.quicktouch;

import com.algodal.library.gdxstate.GdxState;
import com.algodal.library.gdxstate.GdxStateManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Whenever you use the component feature of GdxState, it is always beneficial
 * to create an extension that loads all the shared data into public fields for
 * easy access.  You can now extend from this extension.  This will, for one, have 
 * you have avoid having to load each shared data in each class each time.
 * 
 * Alrick Grandison
 */
public class GameState extends GdxState{
	public final SpriteBatch sprite;
	public final ShapeRenderer shape;
	public final OrthographicCamera camera;
	public final FitViewport viewport;
	public final Float width, height, longHeight;
	public final Stage stage;
	public final Skin skin;
	
	public GameState(GdxStateManager gsm) {
		super(gsm);
		
		//It is safe to load shared data in the constructor.
		//However, anything else can crash the program.
		//Everything else must be loaded in the create method.
		sprite = (SpriteBatch)share.get("sprite");
		shape = (ShapeRenderer)share.get("shape");
		camera = (OrthographicCamera)share.get("camera");
		viewport = (FitViewport)share.get("viewport");
		width = (Float)share.get("width");
		height = (Float)share.get("height");
		stage = (Stage)share.get("stage");
		skin = (Skin)share.get("skin");
		longHeight = (Float)share.get("long-height");
		
		//it is extremely easier to write graphic algorithm when the camera's lower-left edge
		//is at the 0, 0, 0 coordinate.  We set it there by moving the camera's center (which
		//by default is at 0, 0, 0) to this position.
		camera.position.set(width / 2f, height / 2f, 0);
	}
}







