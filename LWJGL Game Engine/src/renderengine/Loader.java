package renderengine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.PNGDecoder;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import models.RawModel;
import textures.TextureData;

public class Loader {

	private List<Integer> vaos;
	private List<Integer> vbos;

	private List<Integer> textures;

	public Loader() {

		vaos = new ArrayList<>();
		vbos = new ArrayList<>();

		textures = new ArrayList<>();

	}

	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {

		int vaoID = createVAO();

		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		unbindVAO();

		return new RawModel(vaoID, indices.length);

	}

	public RawModel loadToVAO(float[] positions, int dimensions) {

		int vaoID = createVAO();

		storeDataInAttributeList(0, dimensions, positions);
		unbindVAO();

		return new RawModel(vaoID, positions.length / 2);

	}

	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, float[] tangents, int[] indices) {

		int vaoID = createVAO();

		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		storeDataInAttributeList(3, 3, tangents);

		unbindVAO();

		return new RawModel(vaoID, indices.length);

	}

	public int loadToVAO(float[] positions, float[] textureCoords) {

		int vaoID = createVAO();

		storeDataInAttributeList(0, 2, positions);
		storeDataInAttributeList(1, 2, textureCoords);

		unbindVAO();

		return vaoID;

	}

	public int createEmptyVbo(int floatCount) {

		int vbo = GL15.glGenBuffers();
		vbos.add(vbo);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		return vbo;

	}

	public void addInstanceAttribute(int vao, int vbo, int attribute, int dataSize, int instancedDataLength, int offset) {

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL30.glBindVertexArray(vao);
		GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, instancedDataLength * 4, offset * 4);
		GL33.glVertexAttribDivisor(attribute, 1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

	}

	public void updateVbo(int vbo, float[] data, FloatBuffer buffer) {

		buffer.clear();
		buffer.put(data);
		buffer.flip();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * 4, GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

	}

	public int loadCubeMap(String[] textureFiles) {

		int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

		for (int i = 0; i < textureFiles.length; i++) {

			TextureData data = decodeTextureFile(textureFiles[i]);
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());

		}

		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		textures.add(texID);

		return texID;

	}

	private TextureData decodeTextureFile(String fileName) {

		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;

		try {

			InputStream in = Class.class.getResourceAsStream("/res/" + fileName + ".png");
			PNGDecoder decoder = new PNGDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, PNGDecoder.RGBA);
			buffer.flip();
			in.close();

		} catch (Exception e) {

			e.printStackTrace();
			System.err.println("Error loading " + fileName + "!");
			System.exit(1);

		}

		return new TextureData(buffer, width, height);

	}

	public int loadTexture(String fileName) {

		Texture texture = null;

		try {

			texture = TextureLoader.getTexture("PNG", Class.class.getResourceAsStream("/res/" + fileName + ".png"));

			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);

			if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {

				float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);

			}

			else {

				System.out.println("Anisotropic filtering not supported");

			}

		} catch (IOException e) {

			e.printStackTrace();

		}

		int textureID = texture.getTextureID();
		textures.add(textureID);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		return textureID;

	}

	public int loadTextures(String... fileNames) {

		ByteBuffer buf = null;
		
		int width = 256;
		
		try {
			
			width = TextureLoader.getTexture("PNG", Class.class.getResourceAsStream("/res/" + fileNames[0] + ".png")).getImageWidth();
		
		} catch (IOException e) {

			e.printStackTrace();
			System.err.println("Failed to load textures " + Arrays.toString(fileNames));
			System.exit(1);
			
		}
		
		int height = width;

		int texId = GL11.glGenTextures();
		GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, texId);
		GL42.glTexStorage3D(GL30.GL_TEXTURE_2D_ARRAY, 1, GL11.GL_RGBA8, 1024, 1024, fileNames.length); 
		GL42.glTexStorage3D(GL30.GL_TEXTURE_2D_ARRAY, fileNames.length, GL11.GL_RGBA8, width, height, fileNames.length);

		for (int i = 0; i < fileNames.length; i++) {
			
			try {
				
				// Open the PNG file as an InputStream
				InputStream in = Class.class.getResourceAsStream("/res/" + fileNames[i] + ".png");
				// Link the PNG decoder to this stream
				PNGDecoder decoder = new PNGDecoder(in);

				// Decode the PNG file in a ByteBuffer
				buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
				decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.RGBA);
				buf.flip();

				in.close();

				GL12.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, 0, 0, i, width, height, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);

			} catch (IOException e) {
				
				e.printStackTrace();
				System.exit(1);
				
			}
			
		}
		
		GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		return texId;
		
	}

	public void cleanUp() {

		for (int vao : vaos) {

			GL30.glDeleteVertexArrays(vao);

		}

		for (int vbo : vbos) {

			GL15.glDeleteBuffers(vbo);

		}

		for (int texture : textures) {

			GL11.glDeleteTextures(texture);

		}

	}

	private int createVAO() {

		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);

		return vaoID;

	}

	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {

		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

		FloatBuffer buffer = storeDataInFloatBuffer(data);

		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

	}

	private void unbindVAO() {

		GL30.glBindVertexArray(0);

	}

	private IntBuffer storeDataInIntBuffer(int[] data) {

		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);

		buffer.put(data);
		buffer.flip();

		return buffer;

	}

	private FloatBuffer storeDataInFloatBuffer(float[] data) {

		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);

		buffer.put(data);
		buffer.flip();

		return buffer;

	}

	private void bindIndicesBuffer(int[] indices) {

		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

	}

}
