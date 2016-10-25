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
package com.algodal.library.gdxstate.utils;

import java.io.StringWriter;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.utils.XmlWriter;

/**
 * Use for saving data.  The data is expected to be POJO, i.e.
 * Plain Old Java Objects which includes arrays.  The classes should only
 * contain Number types, primitive types , POJO types and their arrays.  The
 * classes should not contain complex types.  For each field, their should
 * be getters and setters of appropriate naming convention.  The classes must
 * have a null parameter constructor.  Fields should be private as well.
 * How to save:  Add your POJOs to the pojos arraymap.  String is the your unique reference
 * and Object is the POJO.  When you have added everything call the method save. Your
 * POJOs will save to the file specified by the handle.
 * How to load: call the method load.
 * If you want a fresh load, first clear the array map. Else, any new unique reference will
 * be loaded on the array map.
 */
public class Pojo {
	public final FileHandle handle;
	public final ArrayMap<String, Object> pojos;
	
	private static final String root = "savedata";
	private static final String unit = "data";
	private static final String id = "ref";
	
	private String lastSave;
	private String lastLoad;
	
	public Pojo(FileHandle handle){
		this.handle = handle;
		pojos = new ArrayMap<>();
	}
	
	public void load(){
		if(!handle.exists()) return; //save has not been done
		try{
			Json json = new Json();
			XmlReader doc = new XmlReader();
			Element xml = doc.parse(handle);
			lastLoad = xml.toString();
			for(int i = 0; i < xml.getChildCount(); i++){
				Element element = xml.getChild(i);
				String ref = element.getAttribute(id);
				Object obj = json.fromJson(Object.class, element.getText());
				pojos.put(ref, obj);
			}
		}catch(Exception e){
			throw new GdxRuntimeException(e);
		}
	}
	
	public void save(){
		try{
			Json json = new Json();
			StringWriter doc = new StringWriter();
			XmlWriter xml = new XmlWriter(doc);
			xml.element(root);
			for(Entry<String, Object> entry : pojos){
				xml.element(unit).attribute(id, entry.key).text(json.toJson(entry.value, Object.class));
				xml.pop();
			}
			xml.pop();
			lastSave = doc.toString();
			handle.writeString(lastSave, false);
			xml.close();
		}catch(Exception e){
			throw new GdxRuntimeException(e);
		}
	}
	
	public String lastSave(){
		return lastSave;
	}
	
	public String lastLoad(){
		return lastLoad;
	}
}







