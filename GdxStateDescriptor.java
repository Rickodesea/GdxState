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

/**
 * An information wrapper that wraps around the state class and its assets and pass the
 * information to the renderer.
 *
 */
public final class GdxStateDescriptor {
	public final Class<?> clazz;
	public final AssetLabel<?>[] a;
	
	/**
	 * 
	 * @param clazz State class
	 * @param assets assets of the state
	 */
	public <T extends GdxState>GdxStateDescriptor(Class<T> clazz, AssetLabel<?> ... assets){
		this.clazz = clazz;
		a = assets;
	}

	@Override
	public boolean equals(Object obj) {
		return clazz.equals(((GdxStateDescriptor)obj).clazz);
	}

	@Override
	public String toString() {
		String clz = clazz.toString();
		if(a.length > 0) clz += ": ";
		for(int i = 0; i < a.length; i++){
			AssetLabel<?> l = a[i];
			if(l.label != null) clz += l.label + "<-->" + l.fileName;
			else clz += l.fileName;
			if(i < (a.length - 1)) clz += ", ";
		}
		return clz;
	}
	
}
