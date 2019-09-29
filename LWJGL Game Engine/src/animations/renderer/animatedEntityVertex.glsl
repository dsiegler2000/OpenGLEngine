#version 150

const int MAX_JOINTS = 50; // Max joints allowed in a skeleton
const int MAX_WEIGHTS = 3; // Max number of joints that can affect a vertex

in vec3 position;
in vec2 textureCoords;
in vec3 normal;
in ivec3 jointIndices;
in vec3 weights;

out vec2 pass_textureCoords;
out vec3 pass_normal;

uniform mat4 jointTransforms[MAX_JOINTS];
uniform mat4 projectionViewMatrix;

void main(void){
	
	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);
	
	for(int i = 0; i < MAX_WEIGHTS; i++){
	
		mat4 jointTransform = jointTransforms[jointIndices[i]];
		vec4 posePosition = jointTransform * vec4(position, 1.0);
		totalLocalPos += posePosition * weights[i];
		
		vec4 worldNormal = jointTransform * vec4(normal, 0.0);
		totalNormal += worldNormal * weights[i];
	
	}
	
	gl_Position = projectionViewMatrix * totalLocalPos;
	pass_normal = totalNormal.xyz;
	pass_textureCoords = textureCoords;

}