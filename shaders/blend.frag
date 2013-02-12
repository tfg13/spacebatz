uniform float tex1deltaX;
uniform float tex1deltaY;
uniform float tex2deltaX;
uniform float tex2deltaY;
uniform sampler2D Texture0;

void main()
{  
   vec4 texel0, texel1, texel2;

   texel0 = texture2D(Texture0, gl_TexCoord[0]);
   texel1 = texture2D(Texture0, vec2(gl_TexCoord[0].x + tex1deltaX, gl_TexCoord[0].y + tex1deltaY));
   texel2 = texture2D(Texture0, vec2(gl_TexCoord[0].x + tex2deltaX, gl_TexCoord[0].y + tex2deltaY));

   //gl_FragColor = texel1;
   //gl_FragColor = vec4(0f,1f,0f,texel2.a);
   gl_FragColor = mix(texel0, texel1, texel2.a);
}