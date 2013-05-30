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
    //float factorX = (gl_FragCoord.x - bx) / pixelPerSprite;
    //float factorY = (gl_FragCoord.y - by) / pixelPerSprite;
    //float interpolateLU = sqrt(((1 - factorX) * (1 - factorX)) + ((1 - factorY) * (1 - factorY))) * shadowLU;
    //float interpolateLO = sqrt(((1 - factorX) * (1 - factorX)) + (factorY * factorY)) * shadowLO;
    //float interpolateRU = sqrt((factorX * factorX) + ((1 - factorY) * (1 - factorY))) * shadowRU;
    //float interpolateRO = sqrt((factorX * factorX) + (factorY * factorY)) * shadowRO;
    //gl_FragColor = vec4(0.0, 0.0, 0.0, (interpolateRO * interpolateRU * interpolateLO * interpolateLU));
    gl_FragColor = vec4(0.0, 0.0, 0.0, midmid);
}