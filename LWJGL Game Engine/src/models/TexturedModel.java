package models;

import textures.ModelTexture;

public class TexturedModel {
	
	private RawModel rawModel;
	private ModelTexture texture;
	
	public TexturedModel(RawModel model, ModelTexture t){
		
		rawModel = model;
		
		texture  = t;
		
	}

	public RawModel getRawModel() {
		
		return rawModel;
		
	}

	public ModelTexture getTexture() {
		
		return texture;
		
	}

}
