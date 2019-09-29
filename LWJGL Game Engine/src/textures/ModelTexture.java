package textures;

public class ModelTexture {

	private int textureID;
	private int normalMap;
	private int specularMap;

	private float shineDamper;
	private float reflectivity;

	private boolean hasTransparency;
	private boolean useFakeLighting;
	private boolean hasSpecularMap;

	private int numberOfRows;

	public ModelTexture(int texture) {

		this.textureID = texture;

		this.hasTransparency = false;
		this.useFakeLighting = false;
		this.hasSpecularMap = false;

		this.shineDamper = 1;
		this.reflectivity = 0;

		this.numberOfRows = 1;

	}

	public int getNumberOfRows() {

		return numberOfRows;
		
	}

	public int getNormalMap() {

		return normalMap;
		
	}

	public void setNormalMap(int normalMap) {

		this.normalMap = normalMap;
		
	}

	public void setNumberOfRows(int numberOfRows) {

		this.numberOfRows = numberOfRows;
		
	}

	public boolean isHasTransparency() {

		return hasTransparency;
		
	}

	public boolean isUseFakeLighting() {

		return useFakeLighting;
		
	}

	public void setUseFakeLighting(boolean useFakeLighting) {

		this.useFakeLighting = useFakeLighting;
		
	}

	public void setHasTransparency(boolean hasTransparency) {

		this.hasTransparency = hasTransparency;
		
	}
	
	public void setExtraInfoMap(int specMap){
		
		this.specularMap = specMap;
		this.hasSpecularMap = true;
		
	}
	
	public boolean hasSpecularMap(){
		
		return hasSpecularMap;
		
	}
	
	public int getSpecularMap(){
		
		return specularMap;
		
	}

	public int getID() {

		return textureID;
		
	}

	public float getShineDamper() {

		return shineDamper;
		
	}

	public void setShineDamper(float shineDamper) {

		this.shineDamper = shineDamper;
		
	}

	public float getReflectivity() {

		return reflectivity;
		
	}

	public void setReflectivity(float reflectivity) {

		this.reflectivity = reflectivity;
		
	}

}