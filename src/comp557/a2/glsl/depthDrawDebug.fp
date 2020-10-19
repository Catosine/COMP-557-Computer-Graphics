#version 400

uniform float alpha;
uniform sampler2D depthTexture; 

in vec2 texCoordForFP;

out vec4 fragColor;

void main(void) {
    vec4 c = texture2D( depthTexture, texCoordForFP.xy );
    fragColor = vec4( c.rrr, alpha );
}