#version 120

varying vec2 v_texCoord;  // Texture coordinate from the vertex shader

uniform sampler2D u_sceneTexture;  // Scene texture
uniform vec2 u_lightPos;           // Light position
uniform float u_lightRadius;       // Light radius

void main() {
    // Calculate the distance to the light source
    float distance = length(v_texCoord - u_lightPos);

    // Light attenuation based on distance
    float brightness = smoothstep(u_lightRadius, u_lightRadius - 0.1, distance);

    // Fetch the color from the scene texture
    vec4 sceneColor = texture2D(u_sceneTexture, v_texCoord);

    // Modulate the scene color with the brightness
    gl_FragColor = sceneColor * brightness;
}
