#version 150 core

uniform sampler2D texture;
uniform int colorTexMode = 1;

in vec2 pass_TextureCoord;
in vec4 pass_Color;

out vec4 out_Color;

void main() {
    if (colorTexMode > 0) { // Nur Textur
        out_Color = texture2D(texture, pass_TextureCoord);
        if (colorTexMode > 1) { // Auch noch Färben
            out_Color = out_Color * pass_Color;
        }
    } else { // Nur Färben
        out_Color = pass_Color;
    }
}