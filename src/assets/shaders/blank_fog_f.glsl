#version 150 core

uniform vec3 cameraPosition;

uniform vec4 color = vec4(1, 1, 1, 1);

uniform float fogNear = 10;
uniform float fogFar = 50;
uniform vec4 fogColor = vec4(0, 0, 0, 1);

in vec4 pass_Position;

out vec4 out_Color;

void main(void) {
	out_Color = color;
	vec3 view = pass_Position.xyz - cameraPosition;
	float viewDist = length(view);
	if(fogFar>0 && fogFar>fogNear) {
		out_Color = mix(out_Color, fogColor, clamp((viewDist - fogNear) / (fogFar - fogNear), 0, 1));
	}
}
