#version 460 core

layout(location = 0) in vec3 aPos;
layout(location = 1) in vec3 aNormal;
layout(location = 2) in vec2 aTextCords;

out vec2 TexCoords;
out vec3 Normal;
out vec3 VertexColor;

uniform mat4 view;
uniform mat4 model;
uniform mat4 projection;

void main() {
    TexCoords = aTextCords;
    Normal = aNormal;
    VertexColor = vec3(float(gl_VertexID % 256) / 255.0,
                           float((gl_VertexID / 2) % 256) / 255.0,
                           float((gl_VertexID / 3) % 256) / 255.0);
    gl_Position = projection * view * model * vec4(aPos, 1.0);
}
