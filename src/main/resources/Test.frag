#version 330 core

in vec2 screenPos;
in vec3 vertCol;

uniform sampler2D lightTexture;

out vec4 fragColor;

void main() {
    vec3 col = vertCol;

    vec3 lightCol = texture(lightTexture, screenPos).rgb;

    col *= lightCol;
    //col += vec3(step(screenPos, vec2(0.1)), 0.0);
    fragColor = vec4(col, 1.0f);
}