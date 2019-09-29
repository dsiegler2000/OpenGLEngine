package postprocessing.contrastchanger;

import shaders.ShaderProgram;

public class ContrastShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/postprocessing/contrastchanger/contrastVertex.txt";
	private static final String FRAGMENT_FILE = "/postprocessing/contrastchanger/contrastFragment.txt";

	public ContrastShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {

	}

	@Override
	protected void bindAttributes() {

		super.bindAttribute(0, "position");

	}

}
