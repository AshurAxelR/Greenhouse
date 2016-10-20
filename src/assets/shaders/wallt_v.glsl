#version 330 core

in vec3 in_Position;
in vec3 in_Normal;
in vec3 in_Tangent;
in vec2 in_TexCoord;

in vec3 ins_Position;
in float ins_RotationY;
//in vec3 ins_PivotPosition;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3 localLightOffset = vec3(0, 0, 0);

out vec4 pass_Position;
out mat3 pass_TBN;
out vec2 pass_TexCoord;

//flat out vec3 pass_PivotPosition;
flat out vec4 pass_LocalLightPosition;

mat4 translationMatrix(vec3 t) {
	mat4 m = mat4(1);
	m[3] = vec4(t, 1);
	return m;
}

mat4 rotationYMatrix(float a) {
	mat4 m = mat4(1);
	m[0][0] = cos(a);
	m[0][2] = sin(a);
	m[2][0] = -m[0][2];
	m[2][2] = m[0][0];
	return m;
}

void main(void) {
	mat4 modelMatrix = translationMatrix(ins_Position) * rotationYMatrix(ins_RotationY);
	
	pass_Position = modelMatrix * vec4(in_Position, 1);
	gl_Position = projectionMatrix * viewMatrix * pass_Position;
	
	vec3 norm = normalize(vec3(modelMatrix * vec4(in_Normal, 0)));
	vec3 tan = normalize(vec3(modelMatrix * vec4(in_Tangent, 0)));
	tan = normalize(tan - dot(tan, norm) * norm);
	pass_TBN = mat3(tan, cross(tan, norm), norm);
	
	pass_TexCoord = in_TexCoord;
//	pass_PivotPosition = ins_PivotPosition;
	
	pass_LocalLightPosition = modelMatrix * vec4(localLightOffset, 1);
}
