package com.algodal.quicktouch;

import com.algodal.library.gdxstate.GdxStateManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class Record extends GameState{
	Table table;
	TextButton button;
	static Color textColor = Color.BLUE;
	
	public Record(GdxStateManager gsm) {
		super(gsm);
	}
	
	

	@Override
	public void create() {
		button = new TextButton("Play Again", skin);
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gsm.run(defaultpos, PERMANENT, Play.class);
			}
		});
		
		table = new Table(skin);
		table.setBounds(0, 0, width, longHeight);
		table.add(new Label("Thanks libGDX", new LabelStyle(skin.getFont("small-font"), textColor))).expandX().row();
		table.add(new Label("for a nice library.", new LabelStyle(skin.getFont("small-font"), textColor))).expandX().row();
		table.add(new Label("I hope the", new LabelStyle(skin.getFont("small-font"), textColor))).expandX().row();
		table.add(new Label("community likes", new LabelStyle(skin.getFont("small-font"), textColor))).expandX().row();
		table.add(new Label("GdxState", new LabelStyle(skin.getFont("small-font"), textColor))).expandX().row();
		table.add(button);
		table.left().top();
	}

	

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}



	@Override
	public void hide() {
		stage.getActors().removeValue(table, true);
	}



	@Override
	public void show() {
		stage.addActor(table);
	}



	@Override
	public void render(float delta) {
		sprite.setProjectionMatrix(camera.combined);
		sprite.begin();
		sprite.draw(asset.get("bad", Texture.class), 0, 0, width, longHeight);
		sprite.end();
		
		stage.draw();
		stage.act(delta);
	}
	
	

}
