package particles;


public class ParticleTexture {

	private int textureID;
	private int numberOfRows;
	private boolean addative;
	
	public ParticleTexture(int textureID, int numberOfRows, boolean add) {

		this.textureID = textureID;
		this.numberOfRows = numberOfRows;
		this.addative = add;
	
	}

	public int getTextureID() {
	
		return textureID;
		
	}

	
	public int getNumberOfRows() {
	
		return numberOfRows;
		
	}
	
	public boolean usesAddativeBlending(){
		
		return addative;
		
	}
	
}
