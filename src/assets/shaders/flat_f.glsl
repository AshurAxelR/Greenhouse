#version 330 core

#define TILE_SIZE 6

const vec4 WHITE = vec4(1, 1, 1, 1);

uniform vec3 cameraPosition;

uniform sampler2D texDiffuse;
uniform sampler2D texNormal;
uniform sampler2D texSpecMask;
// r: specular power (1.0 - 256.0)
// g: specular intensity
// b: modulate light
// a: 1 - self illumination
uniform sampler2D texMtlMask;
// r : mtl id
// g : modulate primary light color
// b: white specular (0 - from diffuse, 1 - white)
uniform sampler2DArray mtlStack;
// 0.rgba: diffuse
// 1.rgb: normal
// 2.r: specular power (1.0 - 256.0)
// 2.g: specular intensity
// 2.b: specular white
uniform sampler2D texSeeThrough;

uniform sampler2D texLightColors;

uniform vec4 ambientColor;
uniform vec4 localLightColor = vec4(0, 0, 0, 0);
uniform float localLightRange = 1.5;

uniform float fogNear = 10;
uniform float fogFar = 50;
uniform vec4 fogColor = vec4(0.4, 0.6, 0.9, 0);

uniform vec2 mtlTiling;

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
vec4 transLight;
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
		float trans = pow(max(dot(viewDir, lightDir), 0), 2);
		transLight += color * trans * att;
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
	if(alpha<0.5)
		discard;
		
	vec4 mtlMask = texture(texMtlMask, pass_TexCoord);
	bool useFloorUV = mtlMask.r > 0.5;
	float mtlId = floor((useFloorUV ? 1-mtlMask.r : mtlMask.r) * 128.0) - 1;
	vec2 mtlTexCoord = useFloorUV ? vec2(pass_Position.x / 1.5, pass_Position.z / 1.5) : pass_TexCoord*mtlTiling;

	normal = toNormal(texture(texNormal, pass_TexCoord));
	vec4 specMask = texture(texSpecMask, pass_TexCoord);
	float specInt;
	float specWhite;
	vec4 transColor;
	if(mtlId<0) {
		specInt = specMask.g;
		specPower = specMask.r*255+1;
		specWhite = mtlMask.b;
		transColor = texture(texSeeThrough, pass_TexCoord);
	}
	else {
		mtlId *= 3;
		diffuseColor = texture(mtlStack, vec3(mtlTexCoord, mtlId));
		alpha = min(1, alpha+diffuseColor.a);
		normal += toNormal(texture(mtlStack, vec3(mtlTexCoord, mtlId+1)));
		normal.z *= 0.5;
		normal = normalize(normal);
		vec4 mtlSpecMask = texture(mtlStack, vec3(mtlTexCoord, mtlId+2));
		specInt = mtlSpecMask.g;
		specPower = mtlSpecMask.r*255+1;
		specWhite = mtlSpecMask.b;
		transColor = vec4(0, 0, 0, 1);
	}
	normal.z = gl_FrontFacing ? normal.z : -normal.z;
	normal = normalize(pass_TBN * normal);

	vec3 view = pass_Position.xyz - cameraPosition;
	float viewDist = length(view);
	viewDir = normalize(view);
	
	ivec2 mapSize = textureSize(texLightColors, 0);
	scaleMapCoord = vec2(1.0 / float(mapSize.x), 1.0 / float(mapSize.y));
	diffuseLight = vec4(0, 0, 0, 1);
	specLight = vec4(0, 0, 0, 1);
	transLight = vec4(0, 0, 0, 1);
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
	
	float modulate = 1 - specMask.b;
	diffuseColor *= modulate * mix(WHITE, primaryLightColor, mtlMask.g);
	vec4 specColor = modulate * specInt * mix(diffuseColor, WHITE, specWhite);
	float selfIllumination = 1 - specMask.a;

	out_Color = max(diffuseLight + ambientColor, selfIllumination) * diffuseColor;
	out_Color += transLight * transColor * 0.5;
	out_Color += specLight * specColor;
	
	if(fogFar>0 && fogFar>fogNear) {
		out_Color = mix(out_Color, fogColor, clamp((viewDist - fogNear) / (fogFar - fogNear), 0, 1));
	}
}
