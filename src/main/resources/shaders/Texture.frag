#version 330 core

in vec2 textureCoord;

uniform sampler2D spriteTexture;

out vec4 fragColor;

#define dither 4
#if dither==1
const float dithering[1] = float[](
0.0
);
#elif dither==2
const float dithering[4] = float[](
0.0, 1.0,
2.0, 3.0
);
#elif dither==3
const float dithering[9] = float[](
0, 5, 3,
6, 1, 7,
4, 8, 2
);
#else
const float dithering[16] = float[](
0, 8, 2, 10,
12, 4, 14, 6,
3, 11, 1, 9,
15, 7, 13, 5
);
#endif

void main() {
    //#F21B3F
    vec3 col1 = vec3(0.9453125, 0.10546875, 0.24609375);
    vec3 col2 = vec3(0.796875, 0.98046875, 0.9921875);
    vec3 alphaSrc = texture(spriteTexture, textureCoord, 0).rgb;

    vec3 col = (col1*alphaSrc.rrr + col2*alphaSrc.ggg) / dot(alphaSrc.rgb, vec3(1));

    float alpha = max(max(alphaSrc.r, alphaSrc.g), alphaSrc.b);

    #ifdef dither
    int ind = int(dot(mod(gl_FragCoord.xy, dither), vec2(1, dither)));
    if (alpha*dither*dither < dithering[ind] + 0.5){
        alpha = 0;
    } else {
        alpha = 1;
    }
    #endif

    fragColor = vec4(col, alpha);
}