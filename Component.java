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

import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap.Entry;

/**
 * Share data across states
 *
 */
class Component implements Disposable{
	final ArrayMap<String, Object> am;
	final Share share;
	
	public Component(){
		am = new ArrayMap<String, Object>();
		share = new Share();
	}
	
	public final class Share{
		public final Object get(String ref){
			if(am.containsKey(ref))
					return am.get(ref);
			throw new GdxRuntimeException("The is no component of that reference");
		}
	}

	@Override
	public void dispose() {
		for(Entry<String, Object> entry : am){
			if(entry.value instanceof Disposable)
				((Disposable)entry.value).dispose();
		}
	}
}
