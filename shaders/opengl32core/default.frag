#version 150 core

uniform sampler2D texture;
uniform vec4 color;

in vec2 pass_TextureCoord;

out vec4 out_Color;

void main() {
    out_Color = texture2D(texture, pass_TextureCoord);
    //out_Color = vec4(1.0, 0, 0, 0.5);
}