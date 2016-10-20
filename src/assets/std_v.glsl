#version 150 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform mat3 normalMatrix;

in vec3 in_Position;
in vec3 in_Normal;
in vec3 in_Tangent;
in vec2 in_TexCoord;

out vec4 pass_Position;
out vec4 pass_Color;
out vec3 pass_Normal;
out mat3 pass_TBN;
out vec2 pass_TexCoord;

void main(void) {
	pass_Position = viewMatrix * modelMatrix * vec4(in_Position, 1);
	gl_Position = projectionMatrix * pass_Position;
	
//	vec3 norm = normalize(normalMatrix * in_Normal);
	vec3 norm = normalize(vec3(viewMatrix * modelMatrix * vec4(in_Normal, 0)));
	pass_Normal = norm;
//	vec3 tan = normalize(normalMatrix * in_Tangent);
	vec3 tan = normalize(vec3(viewMatrix * modelMatrix * vec4(in_Tangent, 0)));
	tan = normalize(tan - dot(tan, norm) * norm);
	pass_TBN = mat3(tan, cross(tan, norm), norm);
	
	pass_TexCoord = in_TexCoord;
}