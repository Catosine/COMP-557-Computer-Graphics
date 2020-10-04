#version 400

// texture profile
uniform vec3 kd; // diffuse constant
uniform vec3 ks; // specular constant
uniform vec3 ka; // ambient constant
uniform float se; // specular exponent

// light profile
uniform vec3 lightInt; // light intensity
uniform vec3 lightDir;

in vec3 normalForFP;
in vec3 object_position;

out vec4 fragColor;

// TODO: Objective 7, GLSL lighting

void main(void) {
   //fragColor = vec4( normalForFP, 1 );
   //fragColor = vec4( kd * dot(normalForFP.xyz, lightDir), 1 );
   
   
   vec3 coefficient = vec3(
   					ka + 
   					kd * max(0.0, dot(normalForFP, lightDir)) + 
   					ks * pow(max(0.0, dot(normalForFP, normalize( lightDir - object_position ))), se)
   					);
   
   fragColor = vec4( coefficient * lightInt, 1 );
     
}