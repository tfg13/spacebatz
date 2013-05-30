package de._13ducks.spacebatz.client.graphics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

/**
 * Läd, compiliert und linkt alle Shader.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class LegacyShaderLoader {

    /**
     * Läd alle bekannten Shader.
     *
     * @return Links auf die Shader
     */
    public static int[] load() {
        int[] shaders = new int[2];
        try {
            linkSingleFragmentShader(shaders, 0, createShader("shaders/legacy/shadow.frag", ARBFragmentShader.GL_FRAGMENT_SHADER_ARB));
            linkShaders(shaders, 1, createShader("shaders/legacy/blend.vert", ARBVertexShader.GL_VERTEX_SHADER_ARB), createShader("shaders/blend.frag", ARBFragmentShader.GL_FRAGMENT_SHADER_ARB));
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
        shaders[index] = ARBShaderObjects.glCreateProgramObjectARB();
        if (shaders[index] == 0) {
            throw new RuntimeException("Error creating shader!");
        }
        org.lwjgl.opengl.ARBShaderObjects.glAttachObjectARB(shaders[index], vertexShader);
        ARBShaderObjects.glAttachObjectARB(shaders[index], fragmentShader);

        ARBShaderObjects.glLinkProgramARB(shaders[index]);
        if (ARBShaderObjects.glGetObjectParameteriARB(shaders[index], ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
            System.err.println(getLogInfo(shaders[index]));
        }

        ARBShaderObjects.glValidateProgramARB(shaders[index]);
        if (ARBShaderObjects.glGetObjectParameteriARB(shaders[index], ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
            System.err.println(getLogInfo(shaders[index]));
        }
    }

    /**
     * Linkt den Shader in ein Shaderprogramm
     *
     * @param shaders Die Shader-Liste
     * @param index Position des neuen Shaders
     * @param shader Der geladenene, compilierte Shader
     */
    private static void linkSingleFragmentShader(int[] shaders, int index, int shader) {
        shaders[index] = ARBShaderObjects.glCreateProgramObjectARB();
        if (shaders[index] == 0) {
            throw new RuntimeException("Error creating shader!");
        }
        ARBShaderObjects.glAttachObjectARB(shaders[index], shader);
        ARBShaderObjects.glLinkProgramARB(shaders[index]);
        if (ARBShaderObjects.glGetObjectParameteriARB(shaders[index], ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
            System.err.println(getLogInfo(shaders[index]));
        }

        ARBShaderObjects.glValidateProgramARB(shaders[index]);
        if (ARBShaderObjects.glGetObjectParameteriARB(shaders[index], ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
            System.err.println(getLogInfo(shaders[index]));
        }
    }

    /**
     * Erzeugt einen Fragment-Shader
     *
     * @param filename der Dateiname
     * @return der Shader
     * @throws Exception, wenn das Compilen nicht will
     */
    private static int createShader(String filename, int type) throws Exception {
        int shader = 0;
        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(type);

            if (shader == 0) {
                return 0;
            }

            ARBShaderObjects.glShaderSourceARB(shader, readFileAsString(filename));
            ARBShaderObjects.glCompileShaderARB(shader);

            if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) {
                throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
            }

            return shader;
        } catch (Exception exc) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            throw exc;
        }
    }

    private static String getLogInfo(int obj) {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
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

    private LegacyShaderLoader() {
    }
}
