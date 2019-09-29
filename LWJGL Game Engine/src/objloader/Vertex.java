package objloader;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

public class Vertex {

	private static final int NO_INDEX = -1;

	private Vector3f position;
	
	private int textureIndex;
	private int normalIndex;
	
	private Vertex duplicateVertex;
	
	private int index;
	private float length;
	
	private List<Vector3f> tangents;
	private Vector3f averagedTangent;

	public Vertex(int index, Vector3f position) {
		
		this.index = index;
		this.position = position;
		this.length = position.length();
		
		this.textureIndex = NO_INDEX;
		this.normalIndex = NO_INDEX;
		
		this.duplicateVertex = null;
		
		this.tangents = new ArrayList<Vector3f>();
		this.averagedTangent = new Vector3f(0, 0, 0);
		
	}

	public void addTangent(Vector3f tangent) {

		tangents.add(tangent);
		
	}

	public void averageTangents() {

		if (tangents.isEmpty()) {
			
			return;
			
		}
		
		for (Vector3f tangent : tangents) {
			
			Vector3f.add(averagedTangent, tangent, averagedTangent);
		
		}
		
		averagedTangent.normalise();
	
	}

	public Vector3f getAverageTangent() {

		return averagedTangent;
		
	}

	public int getIndex() {

		return index;
		
	}

	public float getLength() {

		return length;
		
	}

	public boolean isSet() {

		return textureIndex != NO_INDEX && normalIndex != NO_INDEX;
	
	}

	public boolean hasSameTextureAndNormal(int textureIndexOther, int normalIndexOther) {

		return textureIndexOther == textureIndex && normalIndexOther == normalIndex;
	
	}

	public void setTextureIndex(int textureIndex) {

		this.textureIndex = textureIndex;
	
	}

	public void setNormalIndex(int normalIndex) {

		this.normalIndex = normalIndex;
	
	}

	public Vector3f getPosition() {

		return position;
	
	}

	public int getTextureIndex() {

		return textureIndex;
	
	}

	public int getNormalIndex() {

		return normalIndex;
	
	}

	public Vertex getDuplicateVertex() {

		return duplicateVertex;
	
	}

	public void setDuplicateVertex(Vertex duplicateVertex) {

		this.duplicateVertex = duplicateVertex;
	
	}

}