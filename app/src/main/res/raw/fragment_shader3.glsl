precision mediump float;
varying vec2 ft_Position;
uniform sampler2D sTexture;
void main() {
    lowp vec4 color = texture2D(sTexture, ft_Position);
    gl_FragColor = vec4((color.rgb + vec3(-0.5)),color.w);
}