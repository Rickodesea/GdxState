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

import com.algodal.library.gdxstate.GdxStateRenderer.Status;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Interface between the state and the renderer.
 *
 */
public final class GdxStateManager {
	//////////////////////////////////////////////
	//PUBLIC FIELDS
	
	public static final float PERMANENT = 0;
	public static final String idlepos = "com.algodal.library.gdxstate.GdxStateRenderer.Status.idlepos";
	public static final String defaultpos = "com.algodal.library.gdxstate.GdxStateRenderer.Status.defaultpos";
	
	final GdxStateRenderer gsr;
	final ArrayMap<String, InputProcessor> rip;
	
	/////////////////////////////////////////////////
	//CONSTRUCTOR
	
	public GdxStateManager(GdxStateRenderer gsr){
		this.gsr = gsr;
		rip = new ArrayMap<String, InputProcessor>();
	}
	
	//////////////////////////////////////
	//MANAGER METHODS
	
	/**
	 * Use this to add a state to the rendering stack. When a state is added to a rendering
	 * stack it will render.
	 * @param pos The position on the stack.
	 * @param time The time length to render the state.  Pass PERMANENT to render it permanently.
	 * @param clazz The state class to render.
	 */
	public final <T extends GdxState> void run(String pos, float time, Class<T> clazz){
		run(pos, time, clazz, null);
	}
	
	/**
	 * Use this to add a state to the rendering stack. When a state is added to a rendering
	 * stack it will render.  You can optionally pass data to the state as well.
	 * @param pos The position on the stack.
	 * @param time The time length to render the state.  Pass PERMANENT to render it permanently.
	 * @param clazz The state class to render.
	 * @param data a private data to send to the state.
	 */
	public final <T extends GdxState> void run(String pos, float time, Class<T> clazz, Object data){
		Status status = gsr.status(clazz);
		status.run(pos, time, data);
	}
	
	/**
	 * To cancel a state that is running.
	 * @param clazz The state class.
	 */
	public final <T extends GdxState> void cancel(Class<T> clazz){
		Status status = gsr.status(clazz);
		status.cancel();
	}
	
	/**
	 * To register a input processor with the renderer.  This includes Scene2d stages.
	 * @param ref A user defined unique reference name for easy access.
	 * @param input  The input processor.
	 */
	public final void register(String ref, InputProcessor input){
		if(ref == null) throw new GdxRuntimeException("Ref can not be null.");
		if(input == null) throw new GdxRuntimeException("Input can not be null.");
		
		if(rip.containsKey(ref)) gsr.im.removeProcessor(rip.get(ref));
		rip.put(ref, input);
		gsr.im.addProcessor(input);
	}
	
	/**
	 * To unregister a previously registered input processor.  Throws GdxRuntimeException
	 * if it was not previously registered.
	 * @param ref
	 */
	public final void unregister(String ref){
		if(ref == null) throw new GdxRuntimeException("Ref can not be null.");
		if(!rip.containsKey(ref)) throw new GdxRuntimeException("No input is registered under the ref " + ref);
		gsr.im.removeProcessor(rip.get(ref));
		rip.removeKey(ref);
	}
	
	public final boolean registered(String ref){
		return rip.containsKey(ref);
	}
}
