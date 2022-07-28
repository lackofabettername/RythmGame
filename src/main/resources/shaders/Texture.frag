#version 330 core

in vec2 textureCoord;

uniform sampler2D spriteTexture;

out vec4 fragColor;

void main() {
    vec3 col = vec3(1.0, 0.5, 0.2);
    float alpha = texture(spriteTexture, textureCoord).r;

    col *= alpha; //Temporary fix

    fragColor = vec4(col, alpha);
}