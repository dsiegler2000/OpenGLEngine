package textures;

import java.io.File;

public class TextureBuilder {

	private boolean clampEdges = false;
	private boolean mipmap = false;
	private boolean anisotropic = true;
	private boolean nearest = false;

	private File file;

	protected TextureBuilder(String textureFile) {
	
		this.file = new File(textureFile);
	
	}

	public GenericTexture create() {

		TextureData textureData = TextureUtils.decodeTextureFile(file);
		int textureId = TextureUtils.loadTextureToOpenGL(textureData, this);
	
		return new GenericTexture(textureId, textureData.getWidth());
	
	}

	public TextureBuilder clampEdges() {

		this.clampEdges = true;
	
		return this;
	
	}

	public TextureBuilder normalMipMap() {

		this.mipmap = true;
		this.anisotropic = false;
	
		return this;
	
	}

	public TextureBuilder nearestFiltering() {

		this.mipmap = false;
		this.anisotropic = false;
		this.nearest = true;
	
		return this;
	
	}

	public TextureBuilder anisotropic() {

		this.mipmap = true;
		this.anisotropic = true;
	
		return this;
	
	}

	protected boolean isClampEdges() {

		return clampEdges;
	
	}

	protected boolean isMipmap() {

		return mipmap;
	
	}

	protected boolean isAnisotropic() {

		return anisotropic;
	
	}

	protected boolean isNearest() {

		return nearest;
	
	}

}
