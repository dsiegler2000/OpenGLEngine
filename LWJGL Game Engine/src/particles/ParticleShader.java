package particles;

import org.lwjgl.util.vector.Matrix4f;

import shaders.ShaderProgram;

public class ParticleShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/particles/particleVShader.txt";
	private static final String FRAGMENT_FILE = "/particles/particleFShader.txt";

	private int locationNumberOfRows;
	private int locationProjectionMatrix;

	public ParticleShader() {
		
		super(VERTEX_FILE, FRAGMENT_FILE);
		
	}
	
	@Override
	protected void bindAttributes() {

		super.bindAttribute(0, "position");
		super.bindAttribute(1, "modelViewMatrix");
		super.bindAttribute(5, "texOffsets");
		super.bindAttribute(6, "blendFactor");
	
	}

	@Override
	protected void getAllUniformLocations() {

		locationNumberOfRows = super.getUniformLocation("numberOfRows");
		locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
		
	}

	protected void loadProjectionMatrix(Matrix4f projectionMatrix) {

		super.loadMatrix(locationProjectionMatrix, projectionMatrix);
	
	}
	
	protected void loadNumberOfRows(float nRows){
		
		super.loadFloat(locationNumberOfRows, nRows);
		
	}

}
