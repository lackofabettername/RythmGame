#version 330 core

layout (location = 0) in vec2 inPos;
layout (location = 1) in vec2 inTextureCoord;

uniform mat3 viewTransform;
uniform mat3 worldTransform;

out vec2 textureCoord;

void main() {
    vec3 pos = viewTransform * worldTransform  * vec3(inPos, 1);
    pos.z = 0.5;
    gl_Position = vec4(pos.xyz, 1.0);

    textureCoord = inTextureCoord;
}