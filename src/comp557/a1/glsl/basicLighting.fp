#version 400

uniform vec3 kd;
uniform vec3 lightPos;
uniform vec3 lightPos2;
uniform vec3 lightPos3;
uniform vec3 lightColor;
uniform vec3 viewPos;

in vec3 normalForFP;
in vec3 fragPos;

out vec4 fragColor;


// TODO: Objective 7, GLSL lighting
// Followed the openGL tutorial online to implement lighting for this part
// There might be code snippets taken from that tutorial
// https://learnopengl.com/Lighting/Basic-Lighting

void main(void) {
	float specularStrength = 0.3;
	float ambientStrength = 0.8;

    vec3 ambient = ambientStrength * lightColor;
    
    vec3 norm = normalize(normalForFP);

	vec3 lightDir = normalize(lightPos - fragPos);
	float diff = max(dot(norm, lightDir), 0.0);
	vec3 diffuse = diff * lightColor;
	
	vec3 lightDir2 = normalize(lightPos2 - fragPos);
	float diff2 = max(dot(norm, lightDir2), 0.0);
	vec3 diffuse2 = diff2 * lightColor;
	
	vec3 lightDir3 = normalize(lightPos3 - fragPos);
	float diff3 = max(dot(norm, lightDir3), 0.0);
	vec3 diffuse3 = diff3 * lightColor;
	
	vec3 viewDir = normalize(viewPos - fragPos);
	vec3 reflectDir = reflect(-lightDir, norm);  
	vec3 reflectDir2 = reflect(-lightDir2, norm);  
	vec3 reflectDir3 = reflect(-lightDir3, norm);  
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), 8);
	float spec2 = pow(max(dot(viewDir, reflectDir2), 0.0), 32);
	float spec3 = pow(max(dot(viewDir, reflectDir3), 0.0), 16);
	vec3 specular = specularStrength * spec * lightColor;  
	vec3 specular2 = specularStrength * spec2 * lightColor; 
	vec3 specular3 = specularStrength * spec3 * lightColor;  
	
	vec3 result = (ambient + (diffuse3 + diffuse + diffuse2)* kd + (specular2 + specular + specular3));
	
    fragColor = vec4(result, 1.0);
}
