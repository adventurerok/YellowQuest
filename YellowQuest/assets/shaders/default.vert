attribute vec3 inVertex;
attribute vec4 inColor;

varying vec4 outColor;

uniform mat4 uMVMatrix;
uniform mat4 uPMatrix;

void main() {
	gl_Position = uPMatrix * uMVMatrix * vec4(inVertex, 1.0);
	outColor = inColor;
}