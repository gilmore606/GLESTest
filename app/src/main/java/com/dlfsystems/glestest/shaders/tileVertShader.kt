package com.dlfsystems.glestest.shaders

fun tileVertShader() = """
    attribute vec3 a_Position;      // Per-vertex position information we will pass in.
    attribute vec2 a_TexCoordinate; // Per-vertex texture coordinate information we will pass in.

    varying vec2 v_TexCoordinate;   // This will be passed into the fragment shader.

    // The entry point for our vertex shader.
    void main()
    {
        // Transform the vertex into eye space.
        //v_Position = vec3(u_MVMatrix * a_Position);

        v_TexCoordinate = a_TexCoordinate;

        gl_Position = vec4(a_Position.xy, 0.0, 1.0);
    }

""".trimIndent()
