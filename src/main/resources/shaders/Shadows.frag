#version 330 core

in vec3 vertCol;
in float vertID;

uniform vec3 col;
uniform vec2 lightPos;
uniform float time;

out vec4 FragColor;

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

float snoise(vec2 p) {
    float offset = noise(vec3(lightPos*0.01, vertID + time));

    float oup;
    oup += noise(vec3(p, offset));
    mat2 temp = mat2(0.866 * 2, -0.5, 0.5, 0.866 * 2);
    oup += noise(vec3(temp * p, offset)) / 2;
    temp *= temp;
    oup += noise(vec3(temp * p, offset)) / 4;
    return oup / 1.75;
}

void main() {
    //FragColor = vec4(col*f + vec3(gl_FragCoord.z * (1.-f)), 1.0);
    //FragColor = vec4(col * vec3(1. - gl_FragCoord.z), 1.0);

    //Magic numbers™
    gl_FragDepth = 1.0 - vertCol.r * snoise(gl_FragCoord.xy * 0.06);
    FragColor = vec4(col * vertCol * mix(0.9, 1.1, snoise(gl_FragCoord.xy * 0.1)), 1);
}