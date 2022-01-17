#version 330 core

layout (location = 0) in vec2 inPos;
layout (location = 1) in vec3 inCol;

uniform mat3 viewTransform;
uniform mat3 worldTransform;

out vec2 screenPos;
out vec3 vertCol;

void main() {
    vec3 pos = viewTransform * worldTransform  * vec3(inPos, 1);
    gl_Position = vec4(pos.xyz, 1.0);

    //screenPos = (pos.xy + 1.0) * 0.5;
    screenPos = (pos.xy * 0.5) + 0.5;
    vertCol = inCol;
}