package src.rendering;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL45;
import src.game.Window;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    public static void setupGLContext() {
        GL.createCapabilities();
        glEnable(GL45.GL_DEBUG_OUTPUT);
        glClearColor(.0f, .0f, .0f, .0f);
    }

    public static void clearScreen() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public static void draw(int mode, VertexBuffer vbo, VertexArray vao) {

    }

    public static void finish(Window window) {
        glfwSwapBuffers(window.handle);
    }
}
