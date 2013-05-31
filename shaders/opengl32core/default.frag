#version 150 core

uniform sampler2D texture;

in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;

void main() {
    out_Color = texture2D(texture, pass_TextureCoord);
}