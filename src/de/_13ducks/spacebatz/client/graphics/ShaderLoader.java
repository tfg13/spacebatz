package de._13ducks.spacebatz.client.graphics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * Der OpenGL 3.2+ Core Shaderloader
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class ShaderLoader {

    /**
     * Läd alle bekannten Shader.
     *
     * @return Links auf die Shader
     */
    public static int[] load() {
        int[] shaders = new int[1];
        try {
            linkShaders(shaders, 0, createShader("shaders/opengl32core/default.vert", GL20.GL_VERTEX_SHADER), createShader("shaders/opengl32core/default.frag", GL20.GL_FRAGMENT_SHADER));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return shaders;
    }

    /**
     * Linkt Vertex und Fragment Shader zusammen in ein Shaderprogramm
     *
     * @param shaders Die Shader-Liste
     * @param index Position des neuen Shaders
     * @param shader Der geladenene, compilierte Shader
     */
    private static void linkShaders(int[] shaders, int index, int vertexShader, int fragmentShader) {
        shaders[index] = GL20.glCreateProgram();
        if (shaders[index] == 0) {
            throw new RuntimeException("Error creating shader!");
        }
        GL20.glAttachShader(shaders[index], vertexShader);
        GL20.glAttachShader(shaders[index], fragmentShader);

        GL20.glLinkProgram(shaders[index]);
        if (GL20.glGetProgrami(shaders[index], GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            System.err.println(getLogInfoP(shaders[index]));
        }

        GL20.glBindAttribLocation(shaders[index], 0, "in_Position");
        GL20.glBindAttribLocation(shaders[index], 1, "in_TextureCoord");

        GL20.glValidateProgram(shaders[index]);
        if (GL20.glGetProgrami(shaders[index], GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
            System.err.println(getLogInfoP(shaders[index]));
        }
    }

    /**
     * Erzeugt einen Shader
     *
     * @param filename der Dateiname
     * @return der Shader
     * @throws Exception, wenn das Compilen nicht will
     */
    private static int createShader(String filename, int type) throws Exception {
        int shader = 0;
        try {
            shader = GL20.glCreateShader(type);

            if (shader == 0) {
                return 0;
            }

            GL20.glShaderSource(shader, readFileAsString(filename));
            GL20.glCompileShader(shader);

            if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                throw new RuntimeException("Error creating shader: " + getLogInfoS(shader));
            }

            return shader;
        } catch (Exception exc) {
            GL20.glDeleteShader(shader);
            throw exc;
        }
    }

    private static String getLogInfoP(int obj) {
        return GL20.glGetProgramInfoLog(obj, GL20.glGetProgrami(obj, GL20.GL_INFO_LOG_LENGTH));
    }

    private static String getLogInfoS(int obj) {
        return GL20.glGetShaderInfoLog(obj, GL20.glGetShaderi(obj, GL20.GL_INFO_LOG_LENGTH));
    }

    private static String readFileAsString(String filename) throws Exception {
        StringBuilder source = new StringBuilder();
        FileInputStream in = new FileInputStream(filename);
        Exception exception = null;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            Exception innerExc = null;
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    source.append(line).append('\n');
                }
            } catch (Exception exc) {
                exception = exc;
            } finally {
                try {
                    reader.close();
                } catch (Exception exc) {
                    if (innerExc == null) {
                        innerExc = exc;
                    } else {
                        exc.printStackTrace();
                    }
                }
            }

            if (innerExc != null) {
                throw innerExc;
            }
        } catch (Exception exc) {
            exception = exc;
        } finally {
            try {
                in.close();
            } catch (Exception exc) {
                if (exception == null) {
                    exception = exc;
                } else {
                    exc.printStackTrace();
                }
            }

            if (exception != null) {
                throw exception;
            }
        }
        return source.toString();
    }

    private ShaderLoader() {
    }
}
