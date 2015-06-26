#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord0;

uniform float     u_time;
uniform sampler2D u_texture;
uniform vec2      u_resolution;

void main() {
	vec4 texColor = texture2D(u_texture, v_texCoord0);

	float squares = 0.05;
	vec2 uv = v_texCoord0.xy;
	uv.xy -= u_time / 100.;
	vec2 moduv = mod(uv, squares) - (squares/2.);

	float dist = abs(moduv.x) + abs(moduv.y);
	if (dist < squares/2.)
		gl_FragColor = vec4(.35, .44, 0.75 + 0.25 * sin(u_time), 1.0);
	else
		gl_FragColor = vec4(.129, .224, .70 + 0.10 * sin(uv.y * 10. + 2. * u_time), 1.0);
}