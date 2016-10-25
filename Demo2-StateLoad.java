package com.algodal.library.gdxstate.demogame;

import com.algodal.library.gdxstate.GdxState;
import com.algodal.library.gdxstate.GdxStateManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class StateLoad extends GdxState{
	FitViewport view;
	OrthographicCamera cam;
	float w, h;
	
	Asset loadeeAsset;
	Class<? extends GdxState> nextState;
	
	public StateLoad(GdxStateManager gsm) {
		super(gsm);
	}

	@Override
	public void create() {
		//all creation of variables must be done in this method
		cam = new OrthographicCamera(); //camera centre is at the 0,0
		view = new FitViewport((Float)share.get("width"), (Float)share.get("height"), cam);
		w = (Float)share.get("width");
		h = (Float)share.get("height");
		cam.position.set(w / 2, h / 2, 0);
	}

	@Override
	public void resize(int width, int height) {
		view.update(width, height);
	}
	
	

	@Override
	public void show() {
		if(state.data() == null){
			loadeeAsset = state.asset(StateMenuMain.class);
			nextState = StateMenuMain.class;
		}else{
			//you can pass any thing as data
			//here I choose string
			String string = (String)state.data();
			System.out.println("I was told: " + string);
			loadeeAsset = state.asset(StatePlay.class);
			nextState = StatePlay.class;
		}
		
		gsm.run("bottom", PERMANENT, nextState);
		//I (StateLoad) is in the position 'top'
	}

	@Override
	public void render(float delta) {
		//a bar representing the asset loading of another state
		float barh = h * 0.1f;
		float x = 0, y = h / 2;
		ShapeRenderer shape = (ShapeRenderer)share.get("shape");
		shape.setProjectionMatrix(cam.combined);
		shape.begin(ShapeType.Filled);
		shape.setColor(Color.RED);
		shape.rect(x, y, w * loadeeAsset.progress(), barh);
		shape.end();
		
		if(loadeeAsset.done()){
			gsm.cancel(getClass()); //now 'top' will be empty while bottom renders
		}
	}
}
