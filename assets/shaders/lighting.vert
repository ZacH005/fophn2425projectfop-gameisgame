#version 120

attribute vec4 a_position;  // Vertex position
attribute vec2 a_texCoord;  // Texture coordinate

varying vec2 v_texCoord;  // Pass the texture coordinate to the fragment shader

uniform mat4 u_projTrans;  // Projection and transformation matrix

void main() {
    gl_Position = u_projTrans * a_position;  // Transform the vertex position
    v_texCoord = a_texCoord;  // Pass the texture coordinates
}
