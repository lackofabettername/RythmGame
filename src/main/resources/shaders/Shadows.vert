#version 330 core

#define PI 3.14159265359
#define ID (gl_VertexID % 3)

layout (location = 0) in vec2 inPos;

uniform mat3 viewTransform;
uniform mat3 worldTransform;

uniform vec2 lightPos;
uniform float time;

uniform int pass;

out vec3 vertCol;
out float vertID;

float mod289(float x){ return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec4 mod289(vec4 x){ return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec4 perm(vec4 x){ return mod289(((x * 34.0) + 1.0) * x); }

float noise(vec3 p){
    vec3 a = floor(p);
    vec3 d = p - a;
    d = d * d * (3.0 - 2.0 * d);

    vec4 b = a.xxyy + vec4(0.0, 1.0, 0.0, 1.0);
    vec4 k1 = perm(b.xyxy);
    vec4 k2 = perm(k1.xyxy + b.zzww);

    vec4 c = k2 + a.zzzz;
    vec4 k3 = perm(c);
    vec4 k4 = perm(c + 1.0);

    vec4 o1 = fract(k3 * (1.0 / 41.0));
    vec4 o2 = fract(k4 * (1.0 / 41.0));

    vec4 o3 = o2 * d.z + o1 * (1.0 - d.z);
    vec2 o4 = o3.yw * d.x + o3.xz * (1.0 - d.x);

    return o4.y * d.y + o4.x * (1.0 - d.y);
}

void main() {
    vec3 pos = vec3(inPos, 1);

    mat3 lightTransform = inverse(worldTransform);// ???
    vec2 light = (lightTransform * vec3(lightPos, 1.0)).xy;

    vertCol = vec3(1.0, 0.0, 0.0);

    //if (gl_VertexID % 3 == 0 || gl_VertexID % 6 == 4) {
    if (ID == (pass / 2) || ID == (pass / 2) + (pass % 2)) {
        vec2 dir = pos.xy - light;

        float ang = noise(vec3(lightPos*0.01, gl_VertexID + time)) * PI;
        dir += vec2(cos(ang), sin(ang)) * 9;

        dir = normalize(dir);

        float lo = 0.0;
        float hi = 5000.0;
        int iter = 10;

        float mi;
        vec2 p;
        for (int i = 0; i < iter; ++i) {
            mi = (lo+hi)/2;

            p = (viewTransform * worldTransform * (pos + vec3(dir * mi, 0.0))).xy;

            if (any(lessThan(abs(p), vec2(1.05)))) {
                lo = mi;
            } else {
                hi = mi;
            }
        }

        pos.xy += dir * mi;
        vertCol = vec3(-1, 0.0, 0.0) * noise(vec3(lightPos*0.01, gl_VertexID + time));
    }

    pos = viewTransform * worldTransform * pos;

    vertID = gl_VertexID;
    gl_Position = vec4(pos.xyz, 1.0);
}