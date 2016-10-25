package com.algodal.library.gdxstate.demogame;

import com.algodal.library.gdxstate.AssetLabel;
import com.algodal.library.gdxstate.GdxStateDescriptor;
import com.algodal.library.gdxstate.GdxStateRenderer;
import com.algodal.library.gdxstate.utils.Pojo;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class DemoGame implements ApplicationListener {
	GdxStateRenderer gsr;
	
	@Override
	public void create() {
		//create assets
		AssetLabel<Texture> bad = new AssetLabel<>("bad", "badlogic.jpg", Texture.class);
		AssetLabel<Music> ml = new AssetLabel<>("mus0", "loadmusic.wav", Music.class);
		AssetLabel<Music> mm = new AssetLabel<>("mus1", "menumusic.wav", Music.class);
		AssetLabel<Music> mp = new AssetLabel<>("mus2", "playmusic.wav", Music.class);
		AssetLabel<Sound> sa = new AssetLabel<>("snd0", "sound0.wav", Sound.class);
		AssetLabel<Sound> sb = new AssetLabel<>("snd1", "sound1.wav", Sound.class);
		AssetLabel<Sound> sc = new AssetLabel<>("snd2", "sound2.wav", Sound.class);
		AssetLabel<Skin> skn = new AssetLabel<>("skin", "uiskin.json", Skin.class);
		
		//create the GdxStateDescriptor array and add state classes and their assets
		GdxStateDescriptor[] gsd = new GdxStateDescriptor[]{
				//The first state class in the list is automatically run by the renderer
				//in the first available position on the rendering stack. Hence,
				//it is dub the loader state. An asset is allowed to be given to more than one
				//states.  It is advisable that the loader state has no asset because the renderer
				//will not show the state until all its assets are load.  If you must have assets
				//associated with your loader state, ensure that that are small in size.
				new GdxStateDescriptor(Loader.class, ml, sa),
				new GdxStateDescriptor(StateTemporary.class, skn),
				new GdxStateDescriptor(StateLoad.class), 
				new GdxStateDescriptor(StateMenuMain.class, mm, sb, skn),
				new GdxStateDescriptor(StatePlay.class, bad, sa, sb, sc, mp, skn)
		};
		
		//optional: create rendering stack
		//string array leftmost items is highest, rightmost is lowest on the stack
		String[] stack = new String[]{"top", "bottom"};
		/* Additional Info on the rendering stack:
		 * The rendering stack is design to allow the user to determine the number
		 * of states that he/she wants to render at one time.  By default(if you do
		 * not pass a rendering stack to the renderer), the renderer creates one with
		 * one position whose name is stored in the global static variable defaultpos.
		 * When the Manager run method is called, it must be passed the position to render
		 * the state.  The name of the position must match that which was passed to the renderer
		 * in the rendering stack.  If a state is alreading rendering in that position,
		 * the renderer will cancel that state, and render the state you passed the manager
		 * to render in that position.
		 * 
		 * Let's say you want to render two states at the same time: RainState, GrassState.
		 * RainState contains rendering algorithm of a rainfall and GrassState contains a
		 * rendering algorithm of a grass landscape.
		 * So what you is for the overall render to give raining on the grass landscape.
		 * First you will always need one state that the renderer refers to as the Loader State.
		 * That is the first state you pass the renderer in the GdxStateDescriptor Array.
		 * The renderer will automatically run this state for you in the highest position on
		 * your rendering stack.  You will let this state run the two states you want to run
		 * in the respective positions.  Since the loader state is in one of the two, it will
		 * be cancelled automatically by the renderer.
		 * 
		 * Your code would look something like this (It shortened for brevity):
		 * String[] stack = new String[]{'sky', 'ground'};
		 * 
		 * AssetLabel<Texture> grass = AssetLabel<>('grass', 'grass.jpg', Texture.class);
		 * AssetLabel<Texture> rain = AssetLabel<>('rain', 'rain.jpg', Texture.class);
		 * 
		 * GdxStateDescriptor[] gsd = new GdxStateDescriptor[]{
		 * 		new GdxStateDiscriptor(Loader.class),
		 * 		new GdxStateDiscriptor(RainState.class, rain),
		 * 		new GdxStateDiscriptor(GrassState.class, grass)
		 * };
		 * 
		 * public class Loader{
		 * 		@Override
		 * 		public void render(float delta){
		 * 			gsm.run("sky", PERMANENT, RainState.class);
		 * 			gsm.run("ground", PERMANENT, GrassState.class);
		 *			//Loader will be automatically cancelled since it is in sky
		 *			//ground will render first, sky will be render last in each rendering
		 * 		}
		 */
		
		//optional: create clear color
		Color color = Color.BLUE;
		
		//create the GdxStateRenderer object
		gsr = new GdxStateRenderer(gsd, stack, color);
		
		//optional: set renderer to print debug info
		gsr.debug(true).debugdetail(true);
		
		//optional: create any object that is to be shared among all states 
		//and add them to the renderer as components.
		ShapeRenderer sren = new ShapeRenderer();
		SpriteBatch sbat = new SpriteBatch();
		Float w = 100F;
		Float h = 100F;
		Stage stg = new Stage();
		Pojo pojo = new Pojo(Gdx.files.local("demogame.save"));
		RandomDataA a = new RandomDataA();
		RandomDataB b = new RandomDataB();
		RandomDataC c = new RandomDataC();
		
		gsr.component("shape", sren);
		gsr.component("sprite", sbat);
		gsr.component("width", w);
		gsr.component("height", h);
		gsr.component("stage", stg);
		gsr.component("pojo", pojo);
		gsr.component("a", a);
		gsr.component("b", b);
		gsr.component("c", c);
		
		//mandatory, must be called last: process renderer
		gsr.create();
	}

	@Override
	public void resize(int width, int height) {
		//mandatory
		gsr.resize(width, height);
	}

	@Override
	public void render() {
		//mandatory
		gsr.render();
	}

	@Override
	public void pause() {
		//mandatory
		gsr.pause();
	}

	@Override
	public void resume() {
		//mandatory
		gsr.resume();
	}

	@Override
	public void dispose() {
		//mandatory
		gsr.dispose();
	}
}
