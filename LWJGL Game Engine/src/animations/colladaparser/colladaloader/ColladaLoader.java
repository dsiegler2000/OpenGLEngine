package animations.colladaparser.colladaloader;

import animations.colladaparser.datastructures.AnimatedModelData;
import animations.colladaparser.datastructures.AnimationData;
import animations.colladaparser.datastructures.MeshData;
import animations.colladaparser.datastructures.SkeletonData;
import animations.colladaparser.datastructures.SkinningData;
import animations.colladaparser.xmlparser.XmlNode;
import animations.colladaparser.xmlparser.XmlParser;

public class ColladaLoader {

	public static AnimatedModelData loadColladaModel(String colladaFile, int maxWeights) {

		XmlNode node = XmlParser.loadXmlFile(colladaFile);

		SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), maxWeights);
		SkinningData skinningData = skinLoader.extractSkinData();

		SkeletonLoader jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"), skinningData.jointOrder);
		SkeletonData jointsData = jointsLoader.extractBoneData();

		GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.verticesSkinData);
		MeshData meshData = g.extractModelData();

		return new AnimatedModelData(meshData, jointsData);
	
	}

	public static AnimationData loadColladaAnimation(String colladaFile) {

		XmlNode node = XmlParser.loadXmlFile(colladaFile);
		XmlNode animNode = node.getChild("library_animations");
		XmlNode jointsNode = node.getChild("library_visual_scenes");
		AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
		AnimationData animData = loader.extractAnimation();
		
		return animData;
	
	}

}
