uniform float shadowLO;
uniform float shadowLU;
uniform float shadowRO;
uniform float shadowRU;
uniform float bx;
uniform float by;
uniform float pixelPerSprite;

void main(void)
{
    float midTop = mix(shadowLO, shadowRO, (gl_FragCoord.x - bx) / pixelPerSprite);
    float midBot = mix(shadowLU, shadowRU, (gl_FragCoord.x - bx) / pixelPerSprite);
    float midmid = mix(midBot, midTop, (gl_FragCoord.y - by) / pixelPerSprite);
    gl_FragColor = vec4(0.0, 0.0, 0.0, midmid);
}