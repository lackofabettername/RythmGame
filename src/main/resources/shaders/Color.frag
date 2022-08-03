#version 330 core

in vec3 vertCol;

out vec4 fragColor;

uniform int color;
uniform sampler1D palette;
#define paletteWidth (float(textureSize(palette, 0)) - 1)

void main() {
    vec3 paletteCol = texture(palette, color / paletteWidth).rgb;
    fragColor = vec4(vertCol + paletteCol, 1.0);
}