#version 330 core

in vec2 screenPos;
in vec4 vertCol;

uniform sampler2D lightTexture;

out vec4 fragColor;

const int samples = 2;
const float spread = 0.006;

void main() {
    vec3 col = vertCol.rgb;

    vec3 light;
    for (int i = -samples; i <= samples; ++i) {
        for (int j = -samples; j <= samples; ++j) {
            vec2 off = vec2(float(i) * spread, float(j) * spread);
            light += texture(lightTexture, screenPos + off).rgb;
        }
    }

    col = col * (1.0 + light / float((samples*2+1) * (samples*2+1)));
    fragColor = vec4(col, 1.0f);
}