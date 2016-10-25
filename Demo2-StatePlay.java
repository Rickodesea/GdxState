package com.algodal.library.gdxstate.demogame;

import com.algodal.library.gdxstate.GdxState;
import com.algodal.library.gdxstate.GdxStateManager;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class StatePlay extends GdxState{
	FitViewport view;
	OrthographicCamera cam;
	float w, h;
	Stage stage;
	Table table;
	InputProcessor input;
	Music music;
	
	public StatePlay(GdxStateManager gsm) {
		super(gsm);
	}

	@Override
	public void create() {
		//all creation of variables must be done in this method
		w = (Float)share.get("width");
		h = (Float)share.get("height");
		stage = (Stage)share.get("stage");
		cam = new OrthographicCamera(); //camera centre is at the 0,0
		view = new FitViewport((Float)share.get("width"), (Float)share.get("height"), cam);
		cam.position.set(w / 2, h / 2, 0);
		
		/**
		 * See Loader for info
		 */
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
			
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				asset.get("snd2", Sound.class).play();
				return true;
			}
			
			@Override
			public boolean scrolled(int amount) {
				asset.get("snd0", Sound.class).play();
				return true;
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
				asset.get("snd1", Sound.class).play();
				return true;
			}
		};
	}

	@Override
	public void resize(int width, int height) {
		view.update(width, height);
		//I want the stage viewport to only affect the stage but it seems to also affect the
		//spritebatch.  I do not know why so I commented it out.  If anyone figure it out
		//give me a shout.
		//stage.getViewport().update(width, height);
	}

	@Override
	public void render(float delta) {
		//a demo loading bar that loads for 5 seconds
		SpriteBatch sprite = (SpriteBatch)share.get("sprite");
		sprite.setProjectionMatrix(cam.combined);
		sprite.begin();
		sprite.draw(asset.get("bad", Texture.class), 0, 0, w, h);
		sprite.end();
		
		stage.act(delta);
		stage.draw();
	}
	
	@Override
	public void hide() {
		stage.getActors().removeValue(table, true);
		gsm.unregister("controller");
		music.stop();
	}



	@Override
	public void show() {
		TextButton backbtn = new TextButton("Back", asset.get("skin", Skin.class));
		backbtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gsm.run("top", PERMANENT, StateLoad.class);
				gsm.cancel(StatePlay.class);
			}
		});
		table = new Table();
		table.add(backbtn).row();
		table.setBounds(0, 0, w / 16, h / 16);
		stage.getViewport().getCamera().position.set(w / 2, h / 2, 0);
		stage.addActor(table);
		gsm.register("controller", input);
		
		music = asset.get("mus2", Music.class);
		music.setLooping(true);
		music.play();
	}

	@Override
	public void pause() {
		music.pause();
	}

	@Override
	public void resume() {
		music.play();
	}
	
	

}
