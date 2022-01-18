#version 330 core

uniform vec3 col;

out vec4 FragColor;

const float f = 0.5001;

void main() {
    //FragColor = vec4(col*f + vec3(gl_FragCoord.z * (1.-f)), 1.0);
    //FragColor = vec4(col * vec3(1. - gl_FragCoord.z), 1.0);

    FragColor = vec4(col, 1.0);
}