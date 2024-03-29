#version 410

const vec2 lightBias = vec2(0.7, 0.6); // Indicates the balance between diffuse and ambient lighting

in vec2 pass_textureCoords;
in vec3 pass_normal;

layout (location = 0) out vec4 out_Color;
layout (location = 1) out vec4 out_BrightColor;

uniform sampler2D diffuseMap;
uniform vec3 lightDirection;

void main(void){
	
	vec4 diffuseColor = texture(diffuseMap, pass_textureCoords);		
	vec3 unitNormal = normalize(pass_normal);
	float diffuseLight = max(dot(-lightDirection, unitNormal), 0.0) * lightBias.x + lightBias.y;
	
	out_Color = diffuseColor * diffuseLight;
	
	out_BrightColor = vec4(0.0);
	
}