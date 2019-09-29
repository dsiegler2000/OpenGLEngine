package animations.colladaparser.datastructures;

/**
 * Contains the extracted data for an animation, which includes the length of
 * the entire animation and the data for all the keyframes of the animation.
 *
 */
public class AnimationData {

	public final float lengthSeconds;
	public final KeyFrameData[] keyFrames;

	public AnimationData(float lengthSeconds, KeyFrameData[] keyFrames) {
		
		this.lengthSeconds = lengthSeconds;
		this.keyFrames = keyFrames;
	
	}

}
