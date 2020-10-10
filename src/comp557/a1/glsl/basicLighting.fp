// Pengnan Fan, 260768510
#version 400

// texture profile
uniform vec3 kd1; // diffuse1 constant
uniform vec3 ks1; // specular constant
uniform vec3 ka; // ambient constant
uniform float se1; // specular exponent

// light profile
uniform vec3 lightInt1; // light intensity
uniform vec3 lightDir1;

uniform vec3 lightInt2; // light intensity
uniform vec3 lightDir2;

uniform vec3 lightInt3; // light intensity
uniform vec3 lightDir3;

in vec3 normalForFP;
in vec3 object_position;

out vec4 fragColor;

// TODO: Objective 7, GLSL lighting

void main(void) {
   //fragColor = vec4( normalForFP, 1 );
   //fragColor = vec4( kd1 * dot(normalForFP.xyz, lightDir1), 1 );
   
   
   vec3 coefficient = vec3(
   					ka + 
   					kd1 * (max(0.0, dot(normalForFP, lightDir1)) + max(0.0, dot(normalForFP, lightDir2)) + max(0.0, dot(normalForFP, lightDir3))) +
   					ks1 * (pow(max(0.0, dot(normalForFP, normalize( lightDir1 - object_position ))), se1) + pow(max(0.0, dot(normalForFP, normalize( lightDir2 - object_position ))), se1) + pow(max(0.0, dot(normalForFP, normalize( lightDir3 - object_position ))), se1) )
   					);
   
   fragColor = vec4( coefficient * (lightInt1+lightInt2+lightInt3), 1 );
     
}