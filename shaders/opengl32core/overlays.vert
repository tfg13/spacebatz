#version 150 core

uniform mat4 projectionM;
uniform mat4 modelM;

in vec4 in_Position;
in vec2 in_TextureCoord;

out vec2 pass_TextureCoord;

void main() {

    gl_Position = projectionM * modelM * in_Position;

    pass_TextureCoord = in_TextureCoord;

}