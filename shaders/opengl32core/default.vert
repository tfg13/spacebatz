#version 150 core

uniform mat4 projectionViewM;
uniform mat4 modelM;

in vec4 in_Position;
in vec4 in_Color;
in vec2 in_TextureCoord;

out vec4 pass_Color;
out vec2 pass_TextureCoord;

void main() {

    gl_Position = projectionView * modelM * in_Position;

    pass_TextureCoord = in_TextureCoord;

}