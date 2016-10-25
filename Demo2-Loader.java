package com.algodal.library.gdxstate.demogame;

import com.algodal.library.gdxstate.GdxState;
import com.algodal.library.gdxstate.GdxStateManager;
import com.algodal.library.gdxstate.utils.Pojo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Loader extends GdxState{
	FitViewport view;
	OrthographicCamera cam;
	float w, h;
	Music music;
	InputProcessor input;
	
	public Loader(GdxStateManager gsm) {
		super(gsm);
	}

	@Override
	public void create() {
		//all creation of variables must be done in this method
		cam = new OrthographicCamera(); //camera centre is at the 0,0
		view = new FitViewport((Float)share.get("width"), (Float)share.get("height"), cam);
		//add the stage to the input processor handler
		//the string 'globalstage' is used as a reference
		gsm.register("globalstage", (Stage)share.get("stage"));
		w = (Float)share.get("width");
		h = (Float)share.get("height");
		cam.position.set(w / 2, h / 2, 0);
		
		//How is this possible? You can not access assets inside the create() method.
		//Technically the asset is being access inside the InputProcessor body which
		//is not executed inside the create method - only initialized.
		input = new InputProcessor() {
			
			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				// TODO Auto-generated method stub
				return false;
			}
			
			
			//CODE IS HERE ////////////////////////////////////////
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				asset.get("snd0", Sound.class).play();
				return true;
			}
			////////////////////////////////////////////////////////
			
			@Override
			public boolean scrolled(int amount) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean keyUp(int keycode) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean keyTyped(char character) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean keyDown(int keycode) {
				// TODO Auto-generated method stub
				return false;
			}
		};
	}

	@Override
	public void resize(int width, int height) {
		view.update(width, height);System.out.println("RESIZE LOADER");
	}

	@Override
	public void render(float delta) {
		//a demo loading bar that loads for 5 seconds
		float w = (Float)share.get("width");
		float h = (Float)share.get("height");
		float barh = h * 0.1f;
		float x = 0, y = h / 2;
		float grow = (w / 5) * state.duration();
		ShapeRenderer shape = (ShapeRenderer)share.get("shape");
		shape.setProjectionMatrix(cam.combined);
		shape.begin(ShapeType.Filled);
		shape.setColor(Color.ORANGE);
		shape.rect(x, y, grow, barh);
		shape.end();
		
		//run StateTemporary at top position and cancel any state that
		//was running in that position, which is loader.  After 10 seconds
		//Temporary will be cancelled.
		if(grow >= w) gsm.run("top", 10.0f, StateTemporary.class);
	}

	@Override
	public void hide() {
		music.stop();
		gsm.unregister("input");
	}

	@Override
	public void pause() {
		music.pause();
	}

	@Override
	public void resume() {
		music.play();
	}

	@Override
	public void show() {
		music = asset.get("mus0", Music.class);
		music.setLooping(true);
		music.play();
		gsm.register("input", input);
		
		RandomDataA a = (RandomDataA)share.get("a");
		RandomDataB b = (RandomDataB)share.get("b");
		RandomDataC c = (RandomDataC)share.get("c");
		Pojo pojo = (Pojo)share.get("pojo");
		pojo.load();
		if(pojo.pojos.size == 0){
			pojo.pojos.put("a", a);
			pojo.pojos.put("b", b);
			pojo.pojos.put("c", c);
		}
		
		Gdx.app.log("LAST LOAD", pojo.lastLoad());
	}
	
	

}
