#version 150

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;

in float vertexDistance;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    fragColor = vec4(1);//vertexColor * ColorModulator * linear_fog_fade(vertexDistance, FogStart, FogEnd);
}
