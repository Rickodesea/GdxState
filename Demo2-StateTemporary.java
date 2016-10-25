package com.algodal.library.gdxstate.demogame;

import com.algodal.library.gdxstate.GdxState;
import com.algodal.library.gdxstate.GdxStateManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;

public class StateTemporary extends GdxState{
	Viewport view;
	Camera cam;
	Table table;
	Stage stage;
	float w, h;
	
	public StateTemporary(GdxStateManager gsm) {
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
		gsm.run("top", PERMANENT, StateLoad.class);
	}



	@Override
	public void show() {
		table = new Table();
		table.add(new Label("HELLO MY NAME IS TEMPORARY. LOADER RAN ME FOR 10 SECONDS", asset.get("skin", Skin.class)));
		table.setBounds(0,0,w, h);
		stage.addActor(table);
	}



	@Override
	public void render(float delta) {
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		view.update(width, height);	
	}
	
	
}
