package com.algodal.library.gdxstate.demogame;

import java.util.Random;

import com.algodal.library.gdxstate.GdxState;
import com.algodal.library.gdxstate.GdxStateManager;
import com.algodal.library.gdxstate.utils.Pojo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;

public class StateMenuMain extends GdxState{
	Viewport view;
	Camera cam;
	Table table;
	Stage stage;
	float w, h;
	Music music;
	
	public StateMenuMain(GdxStateManager gsm) {
		super(gsm);
	}
	

	@Override
	public void create() {
		stage = (Stage)share.get("stage");
		view = stage.getViewport();
		cam = view.getCamera();
		w = (Float)share.get("width");
		h = (Float)share.get("height");
		cam.position.set(w / 2, h / 2, 0);
	}

	

	@Override
	public void hide() {
		stage.getActors().removeValue(table, true);
		music.stop();
		gsm.unregister("menubutton");
	}



	@Override
	public void show() {
		TextButton button0 = new TextButton("Play", asset.get("skin", Skin.class));
		TextButton button1 = new TextButton("Home", asset.get("skin", Skin.class));
		TextButton button2 = new TextButton("Quit", asset.get("skin", Skin.class));
		
		button0.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gsm.run("top", PERMANENT, StateLoad.class, "START THE DEMO GAME");
				gsm.cancel(StateMenuMain.class);
			}
		});
		
		button1.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gsm.run("top", PERMANENT, Loader.class);
				gsm.cancel(StateMenuMain.class);
			}
		});
		
		button2.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();
			}
		});
		
		table = new Table();
		table.add(button0).row();
		table.add(button1).row();
		table.add(button2).row();
		table.setBounds(0, 0, w, h);
		stage.addActor(table);
		
		music = asset.get("mus1", Music.class);
		music.setLooping(true);
		music.play();
		
		gsm.register("menubutton", new InputProcessor() {
			
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
				asset.get("snd1", Sound.class).play();
				return true;
			}
			
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
		});
		
		Random random = new Random();
		RandomDataA a = (RandomDataA)((Pojo)share.get("pojo")).pojos.get("a");
		RandomDataB b = (RandomDataB)((Pojo)share.get("pojo")).pojos.get("b");
		RandomDataC c = (RandomDataC)((Pojo)share.get("pojo")).pojos.get("c");
		
		a.setCake(random.nextBoolean());
		a.setGuests(random.nextInt(100));
		a.setSoda(random.nextBoolean());
		
		char[] abc = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		
		b.setName("B" + abc[random.nextInt(26)] + abc[random.nextInt(26)] + abc[random.nextInt(26)]);
		b.setWhat(random.nextFloat());
		b.setWho(random.nextLong());
		
		c.setA(a);
		c.setCount(150);
		c.setList(new String[]{"goat", "cow", b.getName()});
		
		((Pojo)share.get("pojo")).save();
		Gdx.app.log("LAST SAVE", ((Pojo)share.get("pojo")).lastSave());
	}
	
	@Override
	public void render(float delta) {
		stage.act(delta);
		stage.draw();
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
	public void resize(int width, int height) {
		view.update(width, height);
	}
	
	
}
