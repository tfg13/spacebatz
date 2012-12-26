package de._13ducks.spacebatz.client.graphics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

/**
 * Läd Shader, derzeit unbenutzt, läd einen nicht verwendeten Test-Shader.
 * Für schönere Schatteneffekte aber notwendig!
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
        int[] shaders = new int[]{0};
        try {
            int shadow = createFragmentShader("shaders/shadow.glsl");
            shaders[0] = ARBShaderObjects.glCreateProgramObjectARB();
            if (shaders[0] == 0) {
                throw new RuntimeException("Error creating shader!");
            }
            ARBShaderObjects.glAttachObjectARB(shaders[0], shadow);
            ARBShaderObjects.glLinkProgramARB(shaders[0]);
            if (ARBShaderObjects.glGetObjectParameteriARB(shaders[0], ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
                System.err.println(getLogInfo(shaders[0]));
            }

            ARBShaderObjects.glValidateProgramARB(shaders[0]);
            if (ARBShaderObjects.glGetObjectParameteriARB(shaders[0], ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
                System.err.println(getLogInfo(shaders[0]));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return shaders;
    }

    /**
     * Erzeugt einen Fragment-Shader
     *
     * @param filename der Dateiname
     * @return der Shader
     * @throws Exception, wenn das Compilen nicht will
     */
    private static int createFragmentShader(String filename) throws Exception {
        int shader = 0;
        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);

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
}
