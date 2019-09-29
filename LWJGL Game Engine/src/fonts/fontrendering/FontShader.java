package fonts.fontrendering;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import shaders.ShaderProgram;

public class FontShader extends ShaderProgram{

	private static final String VERTEX_FILE = "/fonts/fontrendering/fontVertex.txt";
	private static final String FRAGMENT_FILE = "/fonts/fontrendering/fontFragment.txt";
	
	private int locationColor;
	private int locationTranslation;
	
	public FontShader() {
		
		super(VERTEX_FILE, FRAGMENT_FILE);
		
	}
	
	@Override
	protected void bindAttributes() {

		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		
	}

	@Override
	protected void getAllUniformLocations() {
		
		locationColor = super.getUniformLocation("color");
		locationTranslation = super.getUniformLocation("translation");
		
	}

	protected void loadColor(Vector3f color){
		
		super.loadVector(locationColor, color);
		
	}
	
	protected void loadTranslation(Vector2f translation){
		
		super.load2DVector(locationTranslation, translation);
		
	}

}
