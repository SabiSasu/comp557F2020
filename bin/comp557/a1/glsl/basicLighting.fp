#version 400

uniform vec3 kd;
uniform vec3 lightDir;
uniform vec3 lightColor;
uniform vec3 viewPos;

in vec3 normalForFP;
in vec3 fragPos;

out vec4 fragColor;


// TODO: Objective 7, GLSL lighting

void main(void) {
	float specularStrength = 0.3;
	float ambientStrength = 0.8;

    vec3 ambient = ambientStrength * lightColor;
    
    vec3 norm = normalize(normalForFP);

	float diff = max(dot(norm, lightDir), 0.0);
	vec3 diffuse = diff * lightColor;
	
	vec3 viewDir = normalize(viewPos - fragPos);
	vec3 reflectDir = reflect(-lightDir, norm);  
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), 8);
	vec3 specular = specularStrength * spec * lightColor;  
	
	vec3 result = (ambient + diffuse + specular) * kd;
	
    fragColor = vec4(result, 1.0);
}
