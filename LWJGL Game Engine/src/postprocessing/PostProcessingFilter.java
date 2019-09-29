package postprocessing;


public interface PostProcessingFilter {
	
	public void render(int texture);
	
	public int getOutputTexture();
	
	public void cleanUp();

}
