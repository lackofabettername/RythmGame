#version 330 core

#define dontCast

layout (location = 0) in vec2 inPos;

uniform mat3 viewTransform;
uniform mat3 worldTransform;

uniform float castLength;
uniform vec2 lightPos;

out vec3 vertCol;
out float vertID;

void main() {
    vec3 pos = vec3(inPos, 1);

    mat3 lightTransform = inverse(worldTransform);// ???
    vec2 light = (lightTransform * vec3(lightPos, 1.0)).xy;

    vertCol = vec3(1.0, 0.0, 0.0);

    if (gl_VertexID % 3 == 0 || gl_VertexID % 6 == 4) {
        vec2 temp = pos.xy - light;
        temp = normalize(temp);

        float lo = 0.0;
        float hi = 5000.0;
        int iter = 10;

        float mi;
        vec2 p;
        for (int i = 0; i < iter; ++i) {
            mi = (lo+hi)/2;

            p = (viewTransform * worldTransform * (pos + vec3(temp * mi, 0.0))).xy;

            if (abs(p.x) < 1 || abs(p.y) < 1) {
                lo = mi;
            } else {
                hi = mi;
            }
        }

        pos.xy += temp * mi;
        vertCol = vec3(0.0, 0.5, 1.0) * 3;// * length(max(abs(p)-1, 0));
    }

    pos = viewTransform * worldTransform * pos;

    vertID = gl_VertexID;
    gl_Position = vec4(pos.xyz, 1.0);
}