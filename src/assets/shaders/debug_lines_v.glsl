#version 150 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

in vec2 in_Position;

void main(void) {
	gl_Position = projectionMatrix * viewMatrix * vec4(in_Position.x, 0.5, in_Position.y, 1);
}