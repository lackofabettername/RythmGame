#version 330 core

in vec2 textureCoord;

uniform sampler2D spriteTexture;
uniform sampler2D lightTexture;

out vec4 fragColor;

const int samples = 2;
const float spread = 0.006;

void main() {
    vec3 col = texture(spriteTexture, textureCoord).rgb;

    vec3 light;
    for (int i = -samples; i <= samples; ++i) {
        for (int j = -samples; j <= samples; ++j) {
            vec2 off = vec2(float(i) * spread, float(j) * spread);
            light += texture(lightTexture, gl_FragCoord.xy + off).rgb;
        }
    }

    col = col * (1.0 + light / float((samples*2+1) * (samples*2+1)));

    col =
    texture(spriteTexture, textureCoord).rgb+
    texture(lightTexture, textureCoord).rgb+
    vec3(textureCoord * 0.2, 0.0);
    fragColor = vec4(col, 1.0f);
}