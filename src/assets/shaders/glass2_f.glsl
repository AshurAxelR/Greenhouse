#version 330 core

#define TILE_SIZE 6

const vec4 WHITE = vec4(1, 1, 1, 1);

uniform vec3 cameraPosition;

uniform sampler2D texNormal;
uniform sampler2D texBlurMask;
// r : blur
// g : droplets
uniform sampler2D texBuffer;
uniform sampler2D texBlur;

uniform float refraction = 0.01;

uniform vec4 ambientColor;

uniform float fogNear = 10;
uniform float fogFar = 50;
uniform vec4 fogColor = vec4(0.4, 0.6, 0.9, 0);

noperspective in vec2 pass_ScreenPos;
in vec2 pass_TexCoord;

out vec4 out_Color;

vec3 toNormal(vec4 color) {
	vec3 n = 2 * color.xyz - vec3(1, 1, 1);
	n /= n.z;
	return n;
}

void main(void) {
	vec4 blurMask = texture(texBlurMask, pass_TexCoord);
	vec3 normal = toNormal(texture(texNormal, pass_TexCoord));
	normal.z = gl_FrontFacing ? normal.z : -normal.z;
	normal.x *= 1+blurMask.g*10;
	normal.y *= 1+blurMask.g*10;
	vec2 bufCoord = pass_ScreenPos - vec2(normal.x, normal.y)*refraction;
	out_Color = mix(texture(texBuffer, bufCoord), texture(texBlur, bufCoord), blurMask.r);
}
