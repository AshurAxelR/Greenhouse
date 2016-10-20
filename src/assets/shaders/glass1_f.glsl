#version 330 core

#define TILE_SIZE 6

const vec4 WHITE = vec4(1, 1, 1, 1);

uniform vec3 cameraPosition;

uniform sampler2D texDiffuse;
uniform sampler2D texNormal;
uniform sampler2D texSpecMask;
// r: specular power (1.0 - 256.0)
// g: specular intensity
// a: 1 - self illumination
uniform sampler2D texBlurMask;
// r : blur
// g : droplets

uniform sampler2D texLightColors;

uniform vec4 ambientColor;
uniform vec4 localLightColor = vec4(0, 0, 0, 0);
uniform float localLightRange = 1.5;

uniform float fogNear = 10;
uniform float fogFar = 50;
uniform vec4 fogColor = vec4(0.4, 0.6, 0.9, 0);

in vec4 pass_Position;
in mat3 pass_TBN;
in vec2 pass_TexCoord;

// flat in vec3 pass_PivotPosition;
flat in vec4 pass_LocalLightPosition;

out vec4 out_Color;

vec2 pivotPosition;
vec3 normal;
vec3 viewDir;
vec4 diffuseLight;
vec4 specLight;
float specPower;

vec2 scaleMapCoord;

void addPointLight(vec4 lightPosition, vec4 lightColor, float range) {
	vec3 lightVec = (lightPosition - pass_Position).xyz;
	float d = length(lightVec);
	if(d<range && range>0) {
		float att = smoothstep(1, 0, d/range);
		vec4 color = lightColor;
		vec3 lightDir = normalize(lightVec);
		float diffuse = max(dot(normal, lightDir), 0);
		diffuseLight += color * diffuse * att;
		float spec = pow(max(dot(viewDir, normalize(reflect(lightDir, normal))), 0), specPower);
		specLight += color * spec * att;
	}
}

vec4 addPointLight(float dx, float dy) {
	vec4 lightPosition = vec4(pivotPosition.x+dx*TILE_SIZE, 2.5, pivotPosition.y+dy*TILE_SIZE, 1);
	vec2 mapCoord = vec2((floor(lightPosition.x/6)+2.5)*scaleMapCoord.x, (floor(lightPosition.z/6)+2.5)*scaleMapCoord.y);
	vec4 lightColor = texture(texLightColors, mapCoord);
	if(lightColor.a>0)
		addPointLight(lightPosition, lightColor, 8);
	return lightColor;
}

vec3 toNormal(vec4 color) {
	vec3 n = 2 * color.xyz - vec3(1, 1, 1);
	n /= n.z;
	return n;
}

void main(void) {
	pivotPosition = floor((pass_Position.xz + vec2(1.5, 1.5)) / TILE_SIZE)*TILE_SIZE;

	vec4 diffuseColor = texture(texDiffuse, pass_TexCoord);
	float alpha = diffuseColor.a;
	vec4 specMask = texture(texSpecMask, pass_TexCoord);
	float specInt = specMask.g;
	specPower = specMask.r*255+1;
	normal = toNormal(texture(texNormal, pass_TexCoord));
	normal.z = gl_FrontFacing ? normal.z : -normal.z;
	normal = normalize(pass_TBN * normal);

	vec4 blurMask = texture(texBlurMask, pass_TexCoord);
	diffuseColor *= 1+blurMask.r*0.5;
	vec4 ambient = ambientColor*(1+blurMask.r*0.5);
	specInt *= 1+blurMask.g*10-blurMask.r*0.25;
	specPower *= 1-(blurMask.r-blurMask.g)*0.75;

	vec3 view = pass_Position.xyz - cameraPosition;
	float viewDist = length(view);
	viewDir = normalize(view);
	
	ivec2 mapSize = textureSize(texLightColors, 0);
	scaleMapCoord = vec2(1.0 / float(mapSize.x), 1.0 / float(mapSize.y));
	diffuseLight = vec4(0, 0, 0, 1);
	specLight = vec4(0, 0, 0, 1);
	addPointLight(-1, -1);
	addPointLight(0, -1);
	addPointLight(1, -1);
	addPointLight(-1, 0);
	vec4 primaryLightColor = addPointLight(0, 0);
	addPointLight(1, 0);
	addPointLight(-1, 1);
	addPointLight(0, 1);
	addPointLight(1, 1);
	if(localLightColor.a>0) {
		addPointLight(pass_LocalLightPosition, localLightColor, localLightRange);
	}
	
	out_Color = (diffuseLight + ambient) * diffuseColor;
	out_Color += specLight * specInt;
	out_Color.a = alpha + specLight.r * specInt;
	
	if(fogFar>0 && fogFar>fogNear) {
		out_Color = mix(out_Color, fogColor, clamp((viewDist - fogNear) / (fogFar - fogNear), 0, 1));
	}
}
