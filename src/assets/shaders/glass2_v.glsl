#version 330 core

in vec3 in_Position;
in vec3 in_Normal;
in vec3 in_Tangent;
in vec2 in_TexCoord;

in vec3 ins_Position;
in float ins_RotationY;
// in vec3 ins_PivotPosition;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

noperspective out vec2 pass_ScreenPos;
out vec2 pass_TexCoord;

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
	
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(in_Position, 1);
	pass_TexCoord = in_TexCoord;
	
	pass_ScreenPos = vec2(gl_Position.x/gl_Position.w, gl_Position.y/gl_Position.w);
	pass_ScreenPos = (pass_ScreenPos+1)*0.5;

}
