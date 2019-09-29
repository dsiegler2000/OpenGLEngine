package datastructures;

import java.util.ArrayList;
import java.util.List;

public class KeyFrameData {

	public final float time;
	public final List<JointTransformData> jointTransforms;

	public KeyFrameData(float time) {
		
		this.time = time;
		jointTransforms = new ArrayList<JointTransformData>();
	
	}

	public void addJointTransform(JointTransformData transform) {

		jointTransforms.add(transform);
	
	}

}
