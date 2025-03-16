#version 460 core

out vec4 FragColor;
in vec2 TexCoords;
in vec3 Normal;

struct Material {
	sampler2D texture_diffuse1;
	sampler2D texture_diffuse2;
	sampler2D texture_diffuse3;
	sampler2D texture_specular1;
	sampler2D texture_specular2;
};

uniform Material material;

void main() {

	vec3 diffuse = vec3(texture(material.texture_diffuse1, TexCoords));

	vec3 result = diffuse;

	FragColor = vec4(result, 1.0);
}