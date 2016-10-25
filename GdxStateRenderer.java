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

import static com.algodal.library.gdxstate.GdxStateManager.PERMANENT;
import static com.algodal.library.gdxstate.GdxStateManager.defaultpos;
import static com.algodal.library.gdxstate.GdxStateManager.idlepos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;

/**
 * The engine of the library.  It renders all states on a stack. The topmost state
 * will render on top of the bottom-most state.  A state can be cleared from the
 * stack, hence that state will not render.  Each position on the stack has a name.
 * This name is user defined and must be unique to all other names.  Also the number
 * of positions is user defined.  Defining the render stack is optional.  If the user
 * does not define the render stack, the renderer will create one with one position to 
 * use.  The name of this position is stored in the global static variable 'defaultpos'.
 * But if the user creates his own rendering stack then this variable is not applicable.
 * The user can add data to the renderer that will be shared among all states. This data
 * is called component.  Do not dispose of any disposable data you pass to the renderer.
 * The renderer will automatically dispose of it at the appropriate time.
 * The user can activate debugging from the renderer.
 * The renderer has six methods that matches the ApplicationListener's methods in name.  These
 * methods absolutely must be called within their respective application listener's methods.
 *
 */
public final class GdxStateRenderer implements Disposable{
	//////////////////////////////////////////////////
	//NO MODIFIER FIELDS
	
	AssetManager am;
	InputMultiplexer im;
	Color color;
	Component component;
	Debug debug;
	int lw, lh; //last width and height at the resize
	boolean assetSafe;
	
	final GdxStateManager gsm;
	
	final String[] stack;
	final GdxStateDescriptor[] gsd;
	final Status[] statusEvery;
	final Status[] statusStack;
	final Array<Status> statusRunning;
	final Array<Status> statusCancelling;
	
	private boolean created;
	
	///////////////////////////////////////////////////
	//PRIVATE FIELDS
	
	private boolean gamePause;
	private int runIndex, cancelIndex; //for resumption at exact point of run or cancel
	
	//////////////////////////////////////////////////
	//CONSTRUCTOR
	
	/**
	 * 
	 * @param gsd state classes and their assets
	 * @param color clear color
	 * @param stackpos rendering stack
	 */
	public GdxStateRenderer(GdxStateDescriptor[] gsd, Color color, String[] stackpos){
		stack = stkpos(stackpos);
		this.gsd = gsdcheck(gsd);
		this.color = colorcheck(color);
		
		gsm = new GdxStateManager(this);
		component = new Component();
		
		statusEvery = new Status[gsd.length];
		statusStack = new Status[stack.length];
		statusRunning = new Array<>();
		statusCancelling = new Array<>();
		
		debug = new Debug(this);
		created = false;
		assetSafe = false;
	}
	
	////////////////////////////////
	//DEBUGGING
	
	/**
	 * output basic debug information to the console
	 * @param b on or off
	 * @return
	 */
	public GdxStateRenderer debug(boolean b){
		debug.debug = b;
		return this;
	}
	
	/**
	 * debug the rendering
	 * @param b on or off
	 * @return
	 */
	public GdxStateRenderer debugrender(boolean b){
		debug.render = b;
		return this;
	}
	
	/**
	 * print extra low level debug information
	 * @param b on or off
	 * @return
	 */
	public GdxStateRenderer debugdetail(boolean b){
		debug.detail = b;
		return this;
	}
	
	////////////////////////////////////////////////////////
	//COMPONENT
	
	/**
	 * Add a component.  Components are data that the renderer stores and allows access to
	 * by all states.  If they are disposable, the renderer will dispose of them automatically
	 * when the application disposes.  Very useful for resources like Stage, SpriteBatch, 
	 * ShapeRenderer, ModelBatch, ModelBuilder, etc which typically needs to be shared among
	 * states because they are resource heavy.
	 * @param ref User defined and unique reference name for easy access.
	 * @param object absolutely any object
	 * @return
	 */
	public GdxStateRenderer component(String ref, Object object){
		component.am.put(ref, object);
		return this;
	}
	
	/**
	 * Return a component you passed earlier
	 * @param ref user defined referenced
	 * @return
	 */
	public Object c(String ref){
		return component.am.get(ref);
	}
	
	/////////////////////////////////////////////////////////////
	//MORE CONSTRUCTORS
	
	/**
	 * 
	 * @param gsd state classes and their assets
	 */
	public GdxStateRenderer(GdxStateDescriptor[] gsd){
		this(gsd, (Color)null, null);
	}
	
	/**
	 * 
	 * @param gsd state classes and their assets
	 * @param color clear color
	 */
	public GdxStateRenderer(GdxStateDescriptor[] gsd, Color color){
		this(gsd, color, null);
	}
	
	/**
	 * 
	 * @param gsd state classes and their assets
	 * @param stackpos rendering stack
	 */
	public GdxStateRenderer(GdxStateDescriptor[] gsd, String[] stackpos){
		this(gsd, null, stackpos);
	}
	
	/**
	 * 
	 * @param gsd state classes and their assets
	 * @param stackpos rendering stack
	 * @param color clear color
	 */
	public GdxStateRenderer(GdxStateDescriptor[] gsd, String[] stackpos, Color color){
		this(gsd, color, stackpos);
	}
	
	///////////////////////////////////////////
	//HELPER METHODS
	
	private final String[] stkpos(String[] s){
		if(s == null) return new String[]{defaultpos};
		if(s.length == 0)  return new String[]{defaultpos};
		return s;
	}
	
	private final GdxStateDescriptor[] gsdcheck(GdxStateDescriptor[] gsd){
		if(gsd == null) throw new GdxRuntimeException("GdxStateDescriptor array can not be null.");
		if(gsd.length == 0)  throw new GdxRuntimeException("GdxStateDescriptor array can not be empty.");
		Array<GdxStateDescriptor> gsda = new Array<GdxStateDescriptor>();
		for(GdxStateDescriptor d : gsd){
			if(gsda.contains(d, false))
				throw new GdxRuntimeException("All GdxStateDescriptor state classes must be unique to each other.");
			gsda.add(d);
		}
		return gsd;
	}
	
	private final Color colorcheck(Color color){
		if(color == null) return Color.RED;
		return color;
	}
	
	private final void initSize(){
		lw = Gdx.graphics.getWidth();
		lh = Gdx.graphics.getHeight();
	}
	
	/**
	 * LOADER STATE
	 */
	private final void runLoader(){
		statusEvery[0].run(stack[0], PERMANENT, null);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	//APPLICATION LISTENER METHODS
	
	/**
	 * Must be called in the ApplicationListener's create() method last.
	 * That is, after any other call to renderer's methods like component,
	 * debug, etc.
	 */
	public void create() {
		am = new AssetManager();
		im = new InputMultiplexer();
		
		Gdx.input.setInputProcessor(im);
		
		//instantiate status
		for(int i = 0; i < statusEvery.length; i++){
			Status status = new Status(gsd[i]);
			statusEvery[i] = status;
		}
		
		//create status
		for(Status status : statusEvery){
			status.gs.create();
		}
		
		initSize(); //stores the window size
		runLoader(); //renderer runs the user's first state class
		debug.initialize(); //debug reporting tool initializes
		created = true; //signal creation
		assetSafe = true; //signal assets can now be loaded
	}

	/**
	 * Must be called within ApplicationListener's resize
	 * @param width
	 * @param height
	 */
	public void resize(int width, int height) {
		for(Status status : statusRunning) status.resizing(width, height);
		lw = width; lh = height;
	}

	/**
	 * Must be called inside ApplicationListener's render
	 */
	public void render() {
		if(!gamePause){
			Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			float delta = Gdx.graphics.getDeltaTime();
			
			render(delta);
			
			//updates asset manager and resume index
			am.update();
			runIndex = (runIndex >= statusRunning.size) ? 0 : runIndex;
			cancelIndex = (cancelIndex >= statusCancelling.size) ? 0 : cancelIndex;
		}
	}
	
	//render states on the stack
	private final void render(float delta){
		//copies from run to render
		reverseCopy();
		
		//renders all states in the rendering stack
		for(int i = runIndex; i < statusRunning.size; runIndex = ++i){
			Status status = statusRunning.get(i);
			
			if(status.status == Status.RUNNING) status.running();
			else if(status.status == Status.SHOWING) status.showing();
			else if(status.status == Status.RENDERING) status.rendering(delta);
		}
		
		//cancels all states that signal cancelling
		for(int i = cancelIndex; i < statusCancelling.size; cancelIndex = ++i){
			Status status = statusCancelling.get(i);
			
			if(status.status == Status.CANCELLING) status.cancelling();
			else if(status.status == Status.HIDING) status.hiding();
			else if(status.status == Status.IDLING) status.idling();
		}
	}

	/**
	 * Must be called within ApplicationListener's pause
	 */
	public void pause() {
		if(!created) throw new GdxRuntimeException("you must call the renderer's create() method!");
		gamePause = true;
		for(Status status : statusRunning) status.pausing();
		
	}

	/**
	 * Must be called within ApplicationListener's resume
	 */
	public void resume() {
		for(Status status : statusRunning) status.resuming();
		gamePause = false;
	}

	@Override
	/**
	 * Absolutely must be called within ApplicationListener's dispose
	 */
	public void dispose() {
		am.dispose();
		component.dispose();
		debug.deinitialize(); 
	}
	
	///////////////////////////////////////////////
	//RENDERER METHODS
	
	final <T extends GdxState> Status status(Class<T> clazz){
		for(Status status : statusEvery){
			if(status.gsd.clazz.equals(clazz)) return status;
		}
		
		throw new GdxRuntimeException(clazz + " is not a gdxstate class");
	}
	
	final int index(String s){
		if(s.equals(idlepos)) return -1;
		
		for(int i = 0; i < stack.length; i++){
			if(stack[i].equals(s)) return i;
		}
		
		throw new GdxRuntimeException(s + " is not a render position");
	}
	
	final void reverseCopy(){
		statusRunning.clear();
		for(int i = statusStack.length - 1; i > -1; i--){
			if(statusStack[i] != null) statusRunning.add(statusStack[i]);
		}
	}
	
	///////////////////////////////////////////////
	//STATUS CLASS
	
	final class Status{
		public static final int RUNNING = 1;
		public static final int SHOWING = 2;
		public static final int RENDERING = 3;
		public static final int CANCELLING = 4;
		public static final int HIDING = 5;
		public static final int IDLING = 0;
		
		public final GdxState gs;
		public final GdxStateDescriptor gsd;
		
		int status;
		float time;
		String position;
		Object data;
		float accumulate;
		boolean assetReady;
		
		public Status(GdxStateDescriptor gsd){
			try{
				Constructor cstr = ClassReflection.getConstructor(gsd.clazz, GdxStateManager.class);
				gs = (GdxState) cstr.newInstance(gsm);
				this.gsd = gsd;
				
				status = IDLING;
				time = PERMANENT;
				accumulate = 0;
				position = idlepos;
				assetReady = false;
			}catch(Exception e){
				throw new GdxRuntimeException(e);
			}
		}
		
		/////////////////////////////////////////////////////////////
		//SIGNAL METHODS
		
		void reset(){
			accumulate = 0;
		}
		
		void cancel(){
			status = CANCELLING; //signals cancel
			remove(); //removes from the rendering stack
		}
		
		void run(String pos, float time, Object data){
			this.time = time; //stores time to run
			this.data = data; //stores data
			
			Status status = statusStack[index(pos)]; //get the current status in the position
			if(status != null) if(status != this) status.cancel(); //cancel now obsolete status
			statusStack[index(pos)] = this; //store our status in the position
			
			int index = index(position); //get the position our status is coming from
			if(index > -1) statusStack[index] = null; //if it is on the stack then empty it
			
			position = pos; //store our new position on the stack
			
			//checks the last status
			switch(this.status){
			//if the state is already running, continue
			//this is because show() is allowed to be called once
			//for each time it is placed on the rendering stack
			case RENDERING: this.status = RENDERING; 
			
			//otherwise, run the state
			default: this.status = RUNNING;
			}
		}
		
		/**
		 * Removes this status from the rendering stack
		 */
		private void remove(){
			int index = index(position); // get position
			if(index > -1){ //if it is on the rendering stack
				Status who = statusStack[index];
				if(who == this) statusStack[index(position)] = null; //do not accidentally remove someone else
				statusCancelling.add(this);
			}
		}
		
		void expire(){
			if(time > PERMANENT) if(time < accumulate) cancel();
		}
		
		public void load(){
			for(AssetLabel<?> asset : gsd.a) am.load(asset);
		}
		
		public boolean done(){
			for(AssetLabel<?> asset : gsd.a) if(!am.isLoaded(asset.fileName)) return false;
			return true;
		}
		
		public void unload(){
			for(AssetLabel<?> asset : gsd.a) am.unload(asset.fileName);
		}
		
		public float progress(){
			final int all = gsd.a.length;
			int sum = 0;
			for(AssetLabel<?> asset : gsd.a){
				if(am.isLoaded(asset.fileName)) sum ++;
			}
			return (float)(sum + 1) / (float)(all + 1);
		}
		
		public <T> T get(String string, Class<T> clazz){
			for(AssetLabel<?> asset : gsd.a){
				if(asset.contains(string)){
					return am.get(asset.fileName, clazz);
				}
			}
			
			throw new GdxRuntimeException("The state " + clazz + " does not contain the asset " + string);
		}
		
		public boolean has(String string){
			for(AssetLabel<?> asset : gsd.a){
				if(asset.contains(string)) return true;
			}
			return false;
		}
		
		public boolean has(){
			return gsd.a.length > 0;
		}
		
		///////////////////////////////////////////////////////////////////
		//STATUS METHODS

		private void running(){
			status = SHOWING; //set next status
			debug.run(this);
		}
		
		private void showing(){
			//begin loading the assets
			load();
			
			//keep loading until done
			if(done()){
				status = RENDERING; //next status
				reset(); //re-initialize status variables: very important
				gs.resize(lw, lh); //update the state's resizing matrix: very important
				assetReady = true;
				gs.show(); //called once each new time placed on the rendering stack
				debug.show(this);
			}else debug.assetload(this);
		}
		
		private void rendering(float delta){
			gs.render(delta);
			accumulate += delta;
			expire();
			debug.render(this, delta);
		}
		
		private void cancelling(){
			status = HIDING;
			debug.cancel(this);
		}
		
		private void hiding(){
			gs.hide();
			assetReady = false;
			unload();
			status = IDLING;
			debug.hide(this);
		}
		
		private void idling(){
			position = idlepos;
			statusCancelling.removeValue(this, true); //remove from the cancelling stack
			debug.idle(this);
		}
		
		private void pausing(){
			gs.pause();
			debug.pause(this);
		}
		
		private void resuming(){
			gs.resume();
			debug.resume(this);
		}
		
		private void resizing(int w, int h){
			gs.resize(w, h);
			debug.resize(this, w, h);
		}
	}
}
