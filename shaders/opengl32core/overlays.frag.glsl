#version 150 core

uniform sampler2D texture;

in vec2 pass_TextureCoord;
in vec4 pass_Color;

out vec4 out_Color;

void main() {
    out_Color = texture2D(texture, pass_TextureCoord);

    out_Color = vec4(out_Color.r * pass_Color.r, out_Color.g * pass_Color.g, out_Color.b * pass_Color.b, out_Color.a * pass_Color.a);
    //out_Color = vec4(1,0,0,0.5);
}