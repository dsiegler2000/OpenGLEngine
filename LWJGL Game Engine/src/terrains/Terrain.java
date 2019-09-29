package terrains;

import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import renderengine.Loader;
import textures.TerrainTexturePack;
import utils.Maths;

public class Terrain {

	private static final int VERTEX_COUNT = 4096;
	public static final float SIZE = 4800;
	private static final int SEED = new Random().nextInt(1000000000);

	private float x1;
	private float z1;

	private float x2;
	private float z2;

	private RawModel model;

	private float[] maxHeightsOfLayers;

	private TerrainTexturePack heightPack;

	private HeightsGenerator generator;

	private float[][] heights;

	public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack heightPack) {

		this.x1 = gridX * SIZE;
		this.z1 = gridZ * SIZE;

		this.x2 = (gridX + 1) * SIZE;
		this.z2 = (gridZ + 1) * SIZE;

		this.heightPack = heightPack;

		this.maxHeightsOfLayers = new float[5];

		this.generator = new HeightsGenerator(gridX, gridZ, VERTEX_COUNT, SEED);
		this.model = generateTerrain(loader);

	}

	public float getX1() {

		return x1;

	}

	public float getX2() {

		return x2;

	}

	public float getZ1() {

		return z1;

	}

	public float getZ2() {

		return z2;

	}

	public float[] getMaxHeightsOfLayers() {

		return maxHeightsOfLayers;

	}

	public TerrainTexturePack getHeightPack() {

		return heightPack;

	}

	public RawModel getModel() {

		return model;

	}

	public float getHeightOfTerrain(float worldX, float worldZ) {

		float terrainX = worldX - this.x1;
		float terrainZ = worldZ - this.z1;
		float gridSquareSize = SIZE / ((float) heights.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

		if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {

			return 0;

		}

		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;

		float answer;

		if (xCoord <= (1 - zCoord)) {

			answer = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, zCoord));

		}

		else {

			answer = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1, heights[gridX + 1][gridZ + 1], 1), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, zCoord));

		}

		return answer;

	}

	public boolean isInThisChunk(float worldX, float worldZ) {

		return (worldX >= this.x1 && worldX < this.x2 && worldZ >= this.z1 && worldZ < this.z2) || (worldX <= this.x1 && worldX > this.x2 && worldZ >= this.z1 && worldZ < this.z2)
				|| (worldX >= this.x1 && worldX < this.x2 && worldZ <= this.z1 && worldZ > this.z2) || (worldX <= this.x1 && worldX > this.x2 && worldZ <= this.z1 && worldZ > this.z2);
	}

	private RawModel generateTerrain(Loader loader) {

		int vertexCount = 128;

		heights = new float[vertexCount][vertexCount];

		int count = vertexCount * vertexCount;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];

		int[] indices = new int[6 * (vertexCount - 1) * (vertexCount - 1)];

		int vertexPointer = 0;

		for (int i = 0; i < vertexCount; i++) {

			for (int j = 0; j < vertexCount; j++) {

				vertices[vertexPointer * 3] = (float) j / ((float) vertexCount - 1) * SIZE;
				float height = getHeight(j, i);
				;
				heights[j][i] = height;
				vertices[vertexPointer * 3 + 1] = height;
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) vertexCount - 1) * SIZE;

				Vector3f normal = calculateNormal(j, i);

				normals[vertexPointer * 3] = normal.x;
				normals[vertexPointer * 3 + 1] = normal.y;
				normals[vertexPointer * 3 + 2] = normal.z;

				textureCoords[vertexPointer * 2] = (float) j / ((float) vertexCount - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) vertexCount - 1);

				vertexPointer++;

			}

		}

		int pointer = 0;

		for (int gz = 0; gz < vertexCount - 1; gz++) {

			for (int gx = 0; gx < vertexCount - 1; gx++) {

				int topLeft = (gz * vertexCount) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * vertexCount) + gx;
				int bottomRight = bottomLeft + 1;

				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;

			}

		}

		return loader.loadToVAO(vertices, textureCoords, normals, indices);

	}

	private Vector3f calculateNormal(int x, int y) {

		float heightL = getHeight(x - 1, y);
		float heightR = getHeight(x + 1, y);
		float heightD = getHeight(x, y - 1);
		float heightU = getHeight(x, y + 1);

		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalise();

		return normal;

	}

	private float getHeight(int x, int y) {

		return generator.generateHeight(x, y);

	}

}
