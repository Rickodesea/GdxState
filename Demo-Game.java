package com.algodal.quicktouch;

import com.algodal.library.gdxstate.AssetLabel;
import com.algodal.library.gdxstate.GdxStateDescriptor;
import com.algodal.library.gdxstate.GdxStateRenderer;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
*Demo
**/
public class Game extends ApplicationAdapter {
	GdxStateRenderer gsr;
	
	@Override
	public void create () {
		//for convenience, create all my assets separately
		AssetLabel<Texture> badpic = new AssetLabel<>("bad", "badlogic.jpg", Texture.class);
		AssetLabel<TextureAtlas> qt = new AssetLabel<>("play", "play.pack", TextureAtlas.class);
		AssetLabel<Sound> s0 = new AssetLabel<>("hit", "sound0.wav", Sound.class);
		AssetLabel<Sound> s1 = new AssetLabel<>("finish", "sound1.wav", Sound.class);
		AssetLabel<Music> m	= new AssetLabel<>("bg", "menumusic.wav", Music.class);
		
		//add my states and their assets to the list
		GdxStateDescriptor[] gsd = new GdxStateDescriptor []{
			new GdxStateDescriptor(Play.class, qt, s0, s1, m),
			new GdxStateDescriptor(Record.class, badpic)
		};
		
		//add the list to  the initializing renderer
		gsr = new GdxStateRenderer(gsd, Color.BLACK);
		
		//let me see debug messages (will comment out for release)
		gsr.debug(true);
		
		//add all my components
		gsr.component("sprite", new SpriteBatch());
		gsr.component("shape", new ShapeRenderer());
		gsr.component("width", new Float(120f));
		gsr.component("long-height", new Float(160f));
		gsr.component("height", new Float(120f));
		gsr.component("camera", new OrthographicCamera());
		gsr.component("viewport", new FitViewport((Float)gsr.c("width"), (Float)gsr.c("long-height"), (OrthographicCamera)gsr.c("camera")));
		gsr.component("stage", new Stage((FitViewport)gsr.c("viewport")));
		gsr.component("skin", new Skin(Gdx.files.internal("uiskin.json")));
		
		//create the renderer
		gsr.create();
	}
	
	//All the remaining ApplicationListener methods MUST be filled with the renderer's
	//equivalents or else results are undetermined.

	@Override
	public void render () {
		gsr.render();
	}
	
	@Override
	public void dispose () {
		gsr.dispose();
	}

	@Override
	public void resize(int width, int height) {
		gsr.resize(width, height);
	}

	@Override
	public void pause() {
		gsr.pause();
	}

	@Override
	public void resume() {
		gsr.resume();
	}
}
