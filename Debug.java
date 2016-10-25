/*******************************************************************************
 * Copyright 2016 Alrick Grandison (Algodal) <alrickgrandison@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.algodal.library.gdxstate;

import java.util.Iterator;

import com.algodal.library.gdxstate.GdxStateRenderer.Status;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap.Entry;

/**
 * Debug info
 *
 */
class Debug {
	private final GdxStateRenderer gsr; 
	
	public boolean debug;
	public boolean render;
	public boolean detail;
	
	public Debug(GdxStateRenderer gsr){
		this.gsr = gsr;
	}
	
	public void initialize(){
		if(debug){
			final String cr = "initialize";
			
			Gdx.app.log(cr, "color: " + gsr.color);
			Gdx.app.log(cr, "rendering positions: " + prnstk());
			Gdx.app.log(cr, "GdxStateDescriptor[]: " + prngsd());
			Gdx.app.log(cr, "Loader state: " + gsr.statusEvery[0].gs.getClass());
			Gdx.app.log(cr, "Components: " + prncmp());
		}
	}
	
	private String prnstk(){
		String s = gsr.stack[0];
		for(int i = 1; i < gsr.stack.length; i ++){
			s += ", " + gsr.stack[i];
		}
		return s;
	}
	
	private String prngsd(){
		String s = gsr.gsd[0].toString();
		for(int i = 1; i < gsr.gsd.length; i++){
			s += ", " + gsr.gsd[i];
		}
		return s;
	}
	
	private String prncmp(){
		String s = "";
		if(gsr.component.am.size == 0) return s;
		Iterator<Entry<String, Object>> iterator = gsr.component.am.iterator();
		for(int i = 0; i < gsr.component.am.size; i++){
			Entry<String, Object> entry = iterator.next();
			s += entry.key + "->" + entry.value.getClass();
			if(i < (gsr.component.am.size - 1)) s += ", ";
		}
		return s;
	}
	
	private String pos(Status status){
		String pos;
		if(status.position.equals(GdxStateManager.idlepos)){
			pos = "GdxState's Idle Position";
		}else if(status.position.equals(GdxStateManager.defaultpos)){
			pos = "GdxState's Default Position";
		}else{
			pos = status.position;
		}
		
		return "( " + pos + " ) ";
	}
	
	public void resize(Status status, int width, int height){
		if(debug){
			final String rs = "resize";
			Gdx.app.log(rs, pos(status) + status.gsd.clazz.toString() + "= " + width + "x" + height);
		}
	}
	
	public void show(Status status){
		if(debug){
			final String s = "show";
			Gdx.app.log(s, pos(status) + status.gsd.clazz.toString());
		}
	}
	
	public void assetload(Status status){
		if(debug){
			final String s = "asset";
			Gdx.app.log(s, pos(status) + status.gsd.clazz.toString() + " loading...");
		}
	}
	
	/**
	 * Unused - I didn't find a use for this method, but I didn't want to delete it either
	 * @param status
	 */
	public void assetunload(Status status){
		if(debug){
			final String s = "asset";
			Gdx.app.log(s, pos(status) + status.gsd.clazz.toString() + " unloading...");
		}
	}
	
	public void render(Status status, float delta){
		if(debug && render){
			final String s = "render";
			Gdx.app.log(s, pos(status) + status.gsd.clazz.toString() + "= " + Float.toString(delta));
		}
	}
	
	public void hide(Status status){
		if(debug){
			final String s = "hide";
			Gdx.app.log(s, pos(status) + status.gsd.clazz.toString());
		}
	}
	
	public void pause(Status status){
		if(debug){
			final String s = "pause";
			Gdx.app.log(s, pos(status) + status.gsd.clazz.toString());
		}
	}
	
	public void resume(Status status){
		if(debug){
			final String s = "resume";
			Gdx.app.log(s, pos(status) + status.gsd.clazz.toString());
		}
	}
	
	public void run(Status status){
		if(debug && detail){
			final String s = "run";
			Gdx.app.log(s, pos(status) + status.gsd.clazz.toString());
		}
	}
	
	public void cancel(Status status){
		if(debug && detail){
			final String s = "cancel";
			Gdx.app.log(s, pos(status) + status.gsd.clazz.toString());
		}
	}
	
	public void idle(Status status){
		if(debug && detail){
			final String s = "idle";
			Gdx.app.log(s, pos(status) + status.gsd.clazz.toString());
		}
	}
	
	public void deinitialize(){
		if(debug){
			if(gsr.component.am.size == 0) return;
			final Array<String> d = new Array<String>();
			for(Entry<String, Object> entry : gsr.component.am){
				if(entry.value instanceof Disposable) d.add(entry.key);
			}
			if(d.size == 0) return;
			String s = d.get(0);
			for(int i = 1; i < d.size; i++){
				s += ", " + d.get(i);
			}
			Gdx.app.log("disposed", "components{ " + s + " }");
		}
	}
}









