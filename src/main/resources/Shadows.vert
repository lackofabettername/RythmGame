#version 330 core

layout (location = 0) in vec2 inPos;

uniform mat3 viewTransform;
uniform mat3 worldTransform;

uniform float castLength;
uniform vec2 lightPos;

void main() {
    vec3 pos = vec3(inPos, 1);

    if (gl_VertexID % 3 == 0 || gl_VertexID % 6 == 4) {
        pos.xy -= lightPos;
        pos.xy *= castLength;
        pos.xy += lightPos;
    }

    pos = viewTransform * worldTransform * pos;

    float v = length(inPos - lightPos);
    if (castLength > 1.) {
        v += 30.;
    }
    //v /= 400.;
    v /= v + 1.;
    pos.z = v;
    gl_Position = vec4(pos.xyz, 1.0);
}