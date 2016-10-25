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

import com.algodal.library.gdxstate.Component.Share;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * For every game state you make for your game, it has to extend this class.
 * This is what the renderer recognizes and will operate from.  Seven methods
 * are provided by the class to override with user codes.  The class also provides
 * four important variables and three important constants.
 *
 */
public class GdxState {
	/////////////////////////////////////
	//PUBLIC FIELDS
	
	public static final float PERMANENT = GdxStateManager.PERMANENT; //time
	public static final String defaultpos = GdxStateManager.defaultpos; //position
	public static final String idlepos = GdxStateManager.idlepos; //position
	
	/**The state's manager **/ 
	public final GdxStateManager gsm; //running & canceling states
	
	/**The state's asset informant **/ 
	public final Asset asset; //accessing states asset information
	
	/**The state's access to shared data **/ 
	public final Share share; //sharing data across states
	
	/**The state's access to general information **/ 
	public final State state; //information about the states
	
	////////////////////////////////////
	//CONSTRUCTOR
	
	/**
	 * You can use the constructors to get shared components but everything else
	 * can only be safely done inside the create method.  This is because the create method
	 * is guaranteed to call only after all states have been completely initialize.  If,
	 * for example you call the method, state.asset(Class) inside the constructor the result
	 * will most likely cause a crash.
	 * @param gsm interface between this state and its renderer.  The state is not allowed direct
	 * access to the renderer.
	 */
	public GdxState(GdxStateManager gsm){
		this.gsm = gsm;//store the manager
		asset = new Asset();//access to assets
		share = gsm.gsr.component.share;//access to shared data
		state = new State();//access to general information
	}
	
	///////////////////////////////////
	//USER DEFINED METHODS
	
	/**
	 * Called absolutely once.  Use this to initialize variables that must be initialize
	 * once.  For example Scene2d Tables, InputProcessors and Cameras.
	 */
	public void create(){}
	
	/**
	 * Called each time the state is loaded on the rendering stack.  Use it for
	 * restart mechanisms.  Such as reinitializing a integer variable to zero.
	 */
	public void show(){}
	
	/**
	 * Called each time the state is unloaded from the rendering stack.  Use it for
	 * termination mechanisms.  Such as ending an animation.
	 */
	public void hide(){}
	
	/**
	 * Called each time the application is interrupted from outside.  Use it to save progress.
	 * No rendering is done while the application is paused.
	 */
	public void pause(){}
	
	/**
	 * Called each time the application returns from a interruption.  Use it to load progress.
	 * Rendering resumes after this method returns.
	 */
	public void resume(){}
	
	/**
	 * Called repeatedly according to libGdx ApplicationListener's render rate.  This is where
	 * all your game logic and drawing algorithms go.
	 */
	public void render(float delta){}
	
	/**
	 * Called each time the application screen size changes.  This is where your camera or viewport
	 * logic goes.
	 * @param width the new application screen width
	 * @param height the new application screen height
	 */
	public void resize(int width, int height){}
	
	//////////////////////////////////////////
	//ASSET CLASS
	
	/**
	 * An assister class to GdxState.  Provides information about the state's assets.
	 * @author Alrick Grandison (Algodal) <alrickgrandison@gmail.com>
	 * @since October 21, 2016
	 * @version 0.0.1
	 *
	 */
	public final class Asset{
		/**
		 * Tells if all the states assets have been loaded.
		 * @return yes or no.
		 */
		public final boolean done(){
			return gsm.gsr.status(GdxState.this.getClass()).done();
		}
		
		/**
		 * Tells the percentage amount of the state's assets that have been loaded.
		 * @return between 0 and 1 inclusive
		 */
		public final float progress(){
			return gsm.gsr.status(GdxState.this.getClass()).progress();
		}
		
		/**
		 * Get an asset that belongs to the state.  GdxRuntimeException is thrown if
		 * the asset you ask for does not exist.
		 * Please note: IT IS IMPOSSIBLE TO ACCESS THE ASSETS IN THE CREATE METHOD
		 * because the assets are loaded just before the show() method but after the create()
		 * method.  Therefore, if you try to access the assets in the create() method, the
		 * asset manager is going to throw an exception.  If you absolutely NEED to have access
		 * to the asset in the create method you then should not use it as an asset.  You should instead
		 * use it as a component.  However, as a component, you do not get the benefit of asynchonous loading.
		 * @param s Either a label you assigned for the asset or the asset file path
		 * @param clazz The class of the asset
		 * @return The asset object.
		 */
		public final <T> T get(String s, Class<T> clazz){
			if(!gsm.gsr.assetSafe) throw new GdxRuntimeException("You can only retrieve assets after the state's create() method is called.  Therefore, you can not call asset.get() inside of a state's create() method.");
			if(!gsm.gsr.status(GdxState.this.getClass()).assetReady) throw new GdxRuntimeException("You can not recieve this state's asset object because it is not on the rendering stack, hence, its assets are not loaded.");
			return gsm.gsr.status(GdxState.this.getClass()).get(s, clazz);
		}
		
		/**
		 * Checks if the asset exist.
		 * @param s Either a label you assigned for the asset or the asset file path
		 * @return yes or no
		 */
		public final boolean has(String s){
			return gsm.gsr.status(GdxState.this.getClass()).has(s);
		}
		
		/**
		 * Checks if this state has any asset.
		 * @return yes or no
		 */
		public final boolean has(){
			return gsm.gsr.status(GdxState.this.getClass()).has();
		}
	}
	
	/////////////////////////////////////////////////
	//STATE CLASS
	
	/**
	 * An assister class to GdxState.  Provides general information and access for
	 * this state.
	 * @author Alrick Grandison (Algodal) <alrickgrandison@gmail.com>
	 * @since October 21, 2016
	 * @version 0.0.1
	 *
	 */
	public final class State{
		/**
		 * How long the state has been running.  Limited by the size of the number the
		 * variable can hold.  The variable will eventually overflow and you start getting
		 * negative results.  This may be overcome by using it 'relatively'.
		 * @return seconds
		 */
		public final float duration(){
			return gsm.gsr.status(GdxState.this.getClass()).accumulate;
		}
		
		/**
		 * Access to another state's assets variable.  Useful if you want to use one state
		 * to track the loading of assets of another state.  When you use this method to 
		 * get another state's asset class, the only methods you can safely use are progress,
		 * done and has.  get() under this condition is unreliable because the owner state
		 * may not necessarily be loaded: on the rendering stack. So be careful when you use it this way.
		 * @param clazz The class of the state you want the assets variable to.
		 * @return
		 */
		public final Asset asset(Class<? extends GdxState> clazz){
			return gsm.gsr.status(clazz).gs.asset;
		}
		
		/**
		 * The private data shared between this state and the state that ran it.  It is
		 * optional, so check for null before use.
		 * @return data set by the last state that ran this state
		 */
		public final Object data(){
			return gsm.gsr.status(GdxState.this.getClass()).data;
		}
	}
}
