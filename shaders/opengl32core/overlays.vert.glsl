#version 150 core

uniform mat4 projectionM;
uniform mat4 modelM;

in vec4 in_Position;
in vec2 in_TextureCoord;
in vec4 in_Color;

out vec2 pass_TextureCoord;
out vec4 pass_Color;

void main() {

    gl_Position = projectionM * modelM * in_Position;
    //gl_Position = mat4(512,0,0,-1,0,320,0, -1,0,0,-1,0,0,0,0,1) * in_Position;
    //gl_Position = in_Position;

    pass_TextureCoord = in_TextureCoord;
    pass_Color = vec4(1 - in_Color.r, 1 - in_Color.g, 1 - in_Color.b, in_Color.a);
}