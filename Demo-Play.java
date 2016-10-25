package com.algodal.quicktouch;

import java.util.Random;

import com.algodal.library.gdxstate.GdxStateManager;
import com.algodal.library.gdxstate.utils.Pojo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Play extends GameState instead of GdxState because GameState has all the 
 * shared data loaded into public fields.  This is for convenience.  May be you
 * can find an even more convenient method.
 * 
 * @author Alrick Grandison
 *
 */
public class Play extends GameState{
	Quickie quickie;
	Sprite background;
	Table table;
	int score;
	float seconds =  120f;
	Music music;
	Sound hitsnd, finishsnd;
	Label sL, lL;
	Save save;
	
	public Play(GdxStateManager gsm) {
		super(gsm);
	}
	
	@Override
	public void create() {
		table = new Table(skin);
		table.setBounds(0, 0, width, longHeight);
		table.add(new Label("Score", new LabelStyle(skin.getFont("small-font"), Color.BLACK))).expandX();
		table.add((sL = new Label(Integer.toString(score),  new LabelStyle(skin.getFont("small-font"), Color.BLACK)))).expandX();
		table.add(new Label("Left",  new LabelStyle(skin.getFont("small-font"), Color.BLACK))).expandX();
		table.add(lL = new Label(Integer.toString((int)(seconds)),  new LabelStyle(skin.getFont("small-font"), Color.BLACK))).expandX();
		table.top().left();
		table.padTop(15);
		
		gsm.register("stage", stage);
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	@Override
	public void show() {
		camera.position.set(width / 2, longHeight / 2, 0);
		
		save = new Save();
		quickie = new Quickie();
		save.load(quickie);
		if(quickie.dim == null || quickie.prop == null){
			quickie.dim = new Dimension();
			quickie.prop = new Properties();
		}else System.out.println("LOADED: " + save.pojo.lastLoad());
		
		if(save.newGame){
			quickie.reset();
		}
		
		loadAnimations();
		gsm.register("qin", quickie.input);
		quickie.loadPatterns();
		stage.addActor(table);
		music = asset.get("bg", Music.class);
		hitsnd = asset.get("hit", Sound.class);
		finishsnd = asset.get("sound1.wav", Sound.class); //just to show you that you can use either the label or the filename to access assets.  Note: all labels and filenames must be unique to one another.
		music.setLooping(true);
		music.play();
	}
	
	@Override
	public void hide() {
		unloadAnimations();
		gsm.unregister("qin");
		quickie.unloadPatterns();
		stage.getActors().removeValue(table, true);
		music.stop();
		music = null;
		hitsnd = null;
		finishsnd = null;
		save.save(quickie.dim, quickie.prop);
	}

	@Override
	public void render(float delta) {
		sprite.setProjectionMatrix(camera.combined);
		sprite.begin();
		quickie.draw();
		sprite.end();
		
		stage.draw();
		stage.act(delta);
		
		quickie.update(delta);
		quickie.interpretPattern();
		boxedQuickie();
		updateBG(delta);
		
		if((int)seconds <= 0){
			finishsnd.play();
			gsm.run(defaultpos, PERMANENT, Record.class);
		}
	}
	
	
	
	@Override
	public void pause() {
		music.pause();
	}

	@Override
	public void resume() {
		music.play();
	}

	private final void boxedQuickie(){
		if(quickie.dim.x < 0) quickie.dim.x = 0;
		if(quickie.dim.y < 0) quickie.dim.y = 0;
		if(quickie.dim.x > (width - quickie.dim.w)) quickie.dim.x = (width - quickie.dim.w);
		if(quickie.dim.y > (height - quickie.dim.h)) quickie.dim.y = (height - quickie.dim.h);
	}

	private final void loadAnimations(){
		TextureAtlas play = asset.get("play", TextureAtlas.class);
		Array<TextureRegion> player = new Array<>();
		player.addAll(play.findRegions("c0"));
		player.addAll(play.findRegions("c1"));
		player.addAll(play.findRegions("c2"));
		player.addAll(play.findRegions("c3"));
		Animation animation = new Animation(12f, player);
		animation.setPlayMode(PlayMode.LOOP);
		
		//I did it like this just in case I decide to add another animation set
		quickie.anime.put("idle", animation);
		quickie.play("idle");
		
		background = new Sprite(play.findRegion("bg"));
	}
	
	private final void unloadAnimations(){
		quickie.anime.clear();
		background = null;
		//the assets will be unloaded so you should not refer to them
	}
	
	final void updateBG(float delta){
		background.rotate(delta * 60f);
		sL.setText(Integer.toString(score));
		lL.setText(Integer.toString(((int)(seconds))));
		seconds -= delta;
	}
	
	final boolean withinBoundary(float x, float y){
		if(x >= 0 && x <= width){
			if(y >= 0 && y <= height){
				return true;
			}
		}
		
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////////
	//QUICKIE CLASS
	
	class Quickie implements Resetable{
		final ArrayMap<String, Animation> anime;
		final Array<String> pattern;
		final InputProcessor input;
		
		private Animation animate;
		private float count;
		private int index;
		
		Dimension dim;
		Properties prop;
		
		public Quickie(){
			anime = new ArrayMap<>();
			count = 0;
			pattern = new Array<>();
			index = 0;
			
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
					Vector2 v = viewport.unproject(new Vector2(screenX, screenY));
					if(withinBoundary(v.x, v.y)){
						if(contains(v.x, v.y)){
							hitsnd.play();
							score ++;
						}
					}
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
			};
		}
		
		private final boolean contains(float x, float y){
			if(x >= dim.x && x <= (dim.x + dim.w)){
				if(y >= dim.y && y <= (dim.y + dim.h)){
					return true;
				}
			}
			
			return false;
		}
		
		final void interpretPattern(){
			char[] items = prop.nowPattern.toCharArray();
			char c = (index < items.length)? items[index++] : items[(index = 0)];
			patternBehaviour(c);
			updatePattern();
		}
		
		final void loadPatterns(){
			pattern.addAll(
					prop.defaultPat, prop.pat1, prop.pat2, prop.pat3,
					prop.pat4, prop.pat5,
					"07YU2IB98XTR3O97256S4Y47M3375519VKOR3",
					"0P2O09EAJ0Y92V05KB484LCOGRA0828A881H7",
					"0JJE89N2OT9BMY1VY4Z23H6O7FI9BYH72W7ZY",
					"00PN75CYTZ6M5QLFE2F965BG22UEW8223GP82",
					"03J7MF8173Q3P043647MJ0A22H4L967UCDPYV",
					"0RPG1DOD2E827HA7153C5GJ0QBUW6YC35T51D",
					"04409LX22W131Y5Z2DW68ZGET0U2T0F34F5W1",
					"0679G9793M4YW921ZBZB1H0Z5S09PE3JGWX70",
					"089DO21B23J5Y1SMDNQ1U6LEZ9C1376IX549K",
					"0PP58FX04TI390VJQ4841UKVKCIF008LB9OR7",
					"0P3V5MJI525QXJ7MSCDBWAZY0EPNJUPB320W2",
					"0624S1470899UIX1U5RSZ125AOKNO62M8L2K4",
					"07Q8XWDGYD9UJ12QEOKVVM8EP3E20H1736Q95",
					"0D59IP5797677N7Z23956QQAFHPZ63U65S5FT",
					"0V6976TAK52N4DKA8P6KUC39O7S1640VZ37M1",
					"0690RT8LBFE6XSG28O8TGUWQR30263UGA76GU",
					"0DAHV77R3478B818DA6W2O7TIVX3DII7N8ARP",
					"0M040246602Y94L865GKRFMV1MP19SUSQ74PS",
					"0006592EZ26K2X4C79D1MLDQ4VO868FHO61D6",
					"04WF52OL5A8B7L3432W9CE348M8R8P3H19Q43"
					);
		}
		
		final void unloadPatterns(){
			pattern.clear();
		}
		
		final private void updatePattern(){
			if(index == 0){
				prop.nowPattern = pattern.get(new Random(TimeUtils.nanoTime()).nextInt(pattern.size));
			}
		}
		
		private final void patternBehaviour(char c){
			switch(c){
			case '0': dim.speed = 0; break;
			case '1': dim.speed = 1; break;
			case '2': dim.speed = 2; break;
			case '3': dim.speed = 3; break;
			case '4': dim.speed = 4; break;
			case '5': dim.speed = 5; break;
			case '6': dim.speed = 6; break;
			case '7': dim.speed = 7; break;
			case '8': dim.speed = 8; break;
			case '9': dim.speed = 9; break;
			case 'A': dim.dy = +3.5f; break;
			case 'B': dim.dx = +3.5f; break;
			case 'C': dim.dy = -1.5f; break;
			case 'D': dim.dx = -1.5f; break;
			case 'E': dim.dy = +1.0f; break;
			case 'F': dim.dx = +1.0f; break;
			case 'G': dim.dy = -1.0f; break;
			case 'H': dim.dx = -1.0f; break;
			case 'I': dim.dy = +0.5f; break;
			case 'J': dim.dx = +0.5f; break;
			case 'K': dim.dy = -0.5f; break;
			case 'L': dim.dx = -0.5f; break;
			case 'M': dim.dy = +2.0f; break;
			case 'N': dim.dx = +2.0f; break;
			case 'O': dim.dy = -1.3f; break;
			case 'P': dim.dx = -1.3f; break;
			case 'Q': dim.dy = +3.0f; break;
			case 'R': dim.dx = +3.0f; break;
			case 'S': dim.dy = -0.7f; break;
			case 'T': dim.dx = -0.7f; break;
			case 'U': dim.dy = +2.5f; break;
			case 'V': dim.dx = +2.5f; break;
			case 'W': dim.dy = -4.0f; break;
			case 'X': dim.dx = -4.0f; break;
			case 'Y': dim.dy = -1.6f; break;
			case 'Z': dim.dx = -1.6f; break;
			}
		}
		
		final void play(String anim){
			if(anime.containsKey(anim)) animate = anime.get(anim);
			else animate = null;
		}
		
		final TextureRegion nowFace(){
			if(animate == null) return null;
			return animate.getKeyFrame(count);
		}
		
		final void update(float delta){
			count += delta;
			quickie.dim.x += (quickie.dim.step * quickie.dim.dx * (quickie.dim.speed / quickie.dim.mod));
			quickie.dim.y += (quickie.dim.step * quickie.dim.dy * (quickie.dim.speed / quickie.dim.mod));
		}
		

		@Override
		public void reset() {
			dim.reset();
			prop.reset();
			score = 0;
			seconds = 120f;
		}
		
		public void draw(){
			if(background != null ) sprite.draw(
					background, 
					-width / 2, -height / 2, 
					width, height,
					width * 2, height * 2,
					1f, 1f,
					background.getRotation());
			else Gdx.app.log("PLAY", "background is null");
			
			if(quickie.nowFace() != null) sprite.draw(quickie.nowFace(), quickie.dim.x, quickie.dim.y, quickie.dim.w, quickie.dim.h);
			else Gdx.app.log("PLAY", "now face is null");
		}
	}
	
	/////////////////////////////////////////////////////////////
	//SAVE CLASS
	
	final class Save{
		Pojo pojo;
		Preferences prefs;
		boolean newGame;
		
		public Save(){
			pojo = new Pojo(Gdx.files.local("PlaySave.txt"));
			prefs = Gdx.app.getPreferences(getClass().toString());
			newGame = prefs.getBoolean("new-game", true);
		}
		
		final void save(Dimension dim, Properties prop){
			pojo.pojos.put("quickie-dim", dim);
			pojo.pojos.put("quickie-prop", prop);
			pojo.save();
			prefs.putBoolean("new-game", newGame);
			prefs.flush();
		}
		
		final void load(Quickie quickie){
			pojo.pojos.clear();
			pojo.load();
			quickie.dim = (Dimension)pojo.pojos.get("quickie-dim");
			quickie.prop = (Properties)pojo.pojos.get("quickie-prop");
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	//POJO CLASSES
	public static class Dimension implements Resetable{
		final float step = 3.5f;
		final float mod =15f;
		
		private float x, y;
		private float speed, dx, dy;
		
		
		
		public float getX() {
			return x;
		}

		public void setX(float x) {
			this.x = x;
		}

		public float getY() {
			return y;
		}

		public void setY(float y) {
			this.y = y;
		}

		public float getSpeed() {
			return speed;
		}

		public void setSpeed(float speed) {
			this.speed = speed;
		}

		public float getDx() {
			return dx;
		}

		public void setDx(float dx) {
			this.dx = dx;
		}

		public float getDy() {
			return dy;
		}

		public void setDy(float dy) {
			this.dy = dy;
		}

		final float w = 18, h = 24;
		
		public Dimension() {
			x = 120f / 2;
			y = 120f / 2;
			speed = 0;
			dx = +1; dy = +1;
		}

		@Override
		public void reset() {
			x = 120f / 2;
			y = 120f / 2;
			speed = 0;
			dx = +1; dy = +1;
		}
	}
	
	public static class Properties implements Resetable{
		final String defaultPat = "09NZJX9Z38M88X049S616HSZ9RL98BG4EI597";
		final String pat1 = "00J6KWCT4D2GK6RAB4S97I3XNNEM3LT5PL904";
		final String pat2 = "04W77HLFAV54H95RS93T95ZFXUBB4PNU3960X";
		final String pat3 = "0691DH40Q5L2HUW3TC8CF55NN837V8XCPU1O5";
		final String pat4 = "05G937WT88L0U68J672W5NB41VXSCQHMRK115";
		final String pat5 = "0TD5UZSE50ACJB7UWQQ4EMH5S5544C8S8B86D";
		
		private String nowPattern;
		
		
		
		public String getNowPattern() {
			return nowPattern;
		}

		public void setNowPattern(String nowPattern) {
			this.nowPattern = nowPattern;
		}

		public Properties() {
			nowPattern = pat1;
		}
		
		@Override
		public void reset() {
			nowPattern = defaultPat;
		}
	}

}








