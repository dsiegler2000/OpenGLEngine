package textures;

public class TerrainTexturePack {
	
	private TerrainTexture heightTextures;
	private float[] maxHeightOfLayers;

	public TerrainTexturePack(TerrainTexture heightTextures, float[] maxHeightOfLayers) {

		this.heightTextures = heightTextures;
		this.maxHeightOfLayers = maxHeightOfLayers;
		
	}
	
	public TerrainTexture getHeightTextures() {
		
		return heightTextures;
		
	}

	public float[] getMaxHeightOfLayers() {
	
		return maxHeightOfLayers;
		
	}

}
