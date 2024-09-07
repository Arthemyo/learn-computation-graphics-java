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

struct Light {
    vec3 direction;
    vec3 ambient;
    vec3 diffuse;
};

uniform Light globalLight;

void main() {
    vec3 lightDir = normalize(globalLight.direction);
    vec3 norm = normalize(Normal);

    float diff = max(dot(norm, lightDir), 0.0);

    vec3 ambient = globalLight.ambient * vec3(texture(material.texture_diffuse1, TexCoords));
    vec3 diffuse = globalLight.diffuse * diff * vec3(texture(material.texture_diffuse1, TexCoords));

    vec3 result = ambient + diffuse;

    FragColor = vec4(result, 1.0);
}
