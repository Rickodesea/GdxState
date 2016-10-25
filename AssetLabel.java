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

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.files.FileHandle;

/**
 * Extends libGdx's AssetDescriptor.  Provides one additional feature: label.
 * A label is a string that the user create.  It can be anything, but IT MUST BE
 * UNIQUE TO ALL OTHER LABELS.  It acts a substitute to the file name path.  The user can
 * USE EITHER THE LABEL OR THE FILENAMEPATH TO ACCESS THE ASSET FROM THE SAME METHOD.
 * Sometimes file name path can be long, such as 'assets/main/characters/johnny/johnny_jump.jpg'.
 * Label allows you to create a shorter alternative to that.  For example, one could write
 * 'jjump' as the label.  The label is optional.  You do not have to create it if you do not want.
 * 
 * @param <T> Type
 */
public final class AssetLabel <T> extends AssetDescriptor<T>{
	public final String label;
	
	public AssetLabel(FileHandle file, Class<T> assetType, AssetLoaderParameters<T> params) {
		super(file, assetType, params);
		label = null;
	}
	
	public AssetLabel(String fileName, Class<T> assetType, AssetLoaderParameters<T> params) {
		super(fileName, assetType, params);
		label = null;
	}
	
	public AssetLabel(FileHandle file, Class<T> assetType) {
		super(file, assetType);
		label = null;
	}
	
	public AssetLabel(String fileName, Class<T> assetType) {
		super(fileName, assetType);
		label = null;
	}
	
	public AssetLabel(String label, FileHandle file, Class<T> assetType, AssetLoaderParameters<T> params) {
		super(file, assetType, params);
		this.label = label;
	}
	
	public AssetLabel(String label, String fileName, Class<T> assetType, AssetLoaderParameters<T> params) {
		super(fileName, assetType, params);
		this.label = label;
	}
	
	public AssetLabel(String label, FileHandle file, Class<T> assetType) {
		super(file, assetType);
		this.label = label;
	}
	
	public AssetLabel(String label, String fileName, Class<T> assetType) {
		super(fileName, assetType);
		this.label = label;
	}
	
	final String path(String string){
		if(label != null){
			if(label.equals(string)) return fileName;
			else if(fileName.equals(string)) return fileName;
		}else{
			if(fileName.equals(string)) return fileName;
		}
		
		return null;
	}
	
	final boolean contains(String string){
		if(label != null){
			if(label.equals(string)) return true;
			else if(fileName.equals(string)) return true;
		}else{
			if(fileName.equals(string)) return true;
		}
		
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		AssetLabel<?> al = (AssetLabel<?>)obj;
		return
				fileName.equals(al.fileName) ||
				file.equals(al.file) ||
				type.equals(al.type) ||
				label == al.label;
	}
	
	
}
