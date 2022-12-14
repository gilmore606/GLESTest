package com.dlfsystems.glestest.shaders

fun tileFragShader() = """
    precision mediump float;        // We don't need high precision in the fragment shader.
    uniform sampler2D u_Texture;

    varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.
    varying float v_Visibility;

    // The entry point for our fragment shader.
    void main()
    {
        gl_FragColor = texture2D(u_Texture, v_TexCoordinate) * v_Visibility;
    }

""".trimIndent()
