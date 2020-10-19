#version 400

uniform vec3 kd;
uniform vec3 ks;
uniform vec3 lightPos;
uniform float shininess;
uniform vec3 lightColor;
uniform bool enableLighting;

uniform sampler2D shadowMap; 
uniform float sigma;

in vec3 positionForFP;
in vec3 normalForFP;
in vec4 positionLightCVV;

out vec4 fragColor;

void main(void) {
	vec3 rgb = kd;
	if ( enableLighting ) {
		vec3 lightDirection = normalize( lightPos - positionForFP );
		float diffuse = max( dot( normalForFP, lightDirection), 0 );
		vec3 viewDirection = normalize( - positionForFP );
		vec3 halfVector = normalize( lightDirection + viewDirection );
		float specular = max( 0, dot( normalForFP, halfVector ) );
		if (diffuse == 0.0) {
		    specular = 0.0;
		} else {
	   		specular = pow( specular, shininess );
		}
		vec3 scatteredLight = kd * lightColor * diffuse;
		vec3 reflectedLight = ks * lightColor * specular;
		
		// TODO: compute position in light CCV
		vec4 pln = positionLightCVV / positionLightCVV.w ;   // frag pos in light normalize by w
	    vec3 plnr = pln.xyz * 0.5  + vec3( 0.5, 0.5, 0.5 );  // frag pos in light normalized and remapped to values from 0 to 1
	    float shadow = 1.0;
	    if ( positionLightCVV.w > 0.0 ) { 	   		
  			float distanceFromLight = texture2D( shadowMap, plnr.xy ).r + sigma; // avoid self shadowing
       		shadow -= distanceFromLight < plnr.z ? 1 : 0 ;
    	}
	    rgb = min( shadow * (scatteredLight + reflectedLight), vec3(1,1,1) );
 	} 
	fragColor = vec4( rgb ,1 );
}