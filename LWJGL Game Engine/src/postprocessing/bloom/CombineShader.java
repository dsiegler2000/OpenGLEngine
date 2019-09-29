package postprocessing.bloom;

import shaders.ShaderProgram;

public class CombineShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/postprocessing/bloom/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "/postprocessing/bloom/combineFragment.txt";

	private int locationColorTexture;
	private int locationHighlightTexture;
	
	protected CombineShader() {
		
		super(VERTEX_FILE, FRAGMENT_FILE);
						
	}

	@Override
	protected void getAllUniformLocations() {

		locationColorTexture = super.getUniformLocation("colorTexture");
		locationHighlightTexture = super.getUniformLocation("highlightTexture");
	
	}

	protected void connectTextureUnits() {

		super.loadInt(locationColorTexture, 0);
		super.loadInt(locationHighlightTexture, 1);
	
	}

	@Override
	protected void bindAttributes() {

		super.bindAttribute(0, "position");
	
	}

}
