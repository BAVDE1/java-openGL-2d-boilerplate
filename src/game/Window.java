package game;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryStack;
import utility.Logging;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Some simple stuff for window management
 */
public class Window {
    public long handle;

    public Window() {
        GLFWErrorCallback.createPrint(System.err).set();  // print errors please *-*

        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // Configure window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        // gl version 4.5
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // Create the window
        handle = glfwCreateWindow(Constants.SCREEN_SIZE.width, Constants.SCREEN_SIZE.height, "ARGG IT HURTS", NULL, NULL);
        if (handle == NULL) throw new RuntimeException("Failed to create the GLFW window");
    }

    public void setupGLFWContext() {
        // center the window (if it can)
        if (glfwGetPlatform() != GLFW_PLATFORM_WAYLAND) {  // cause wayland is stupid
            try (MemoryStack stack = stackPush()) {  // Get the thread stack and push a new frame
                IntBuffer winWidth = stack.mallocInt(1);
                IntBuffer winHeight = stack.mallocInt(1);

                glfwGetWindowSize(handle, winWidth, winHeight);
                GLFWVidMode screen = glfwGetVideoMode(glfwGetPrimaryMonitor());
                if (screen == null) {
                    Logging.danger("An error occurred when attempting to get the screen");
                    return;
                }

                glfwSetWindowPos(
                        handle,
                        (screen.width() - winWidth.get(0)) / 2,
                        (screen.height() - winHeight.get(0)) / 2
                );
            }
        }

        // make context current
        glfwMakeContextCurrent(handle);
        Logging.debug("GLFW context created and current");
    }

    public void show() {
        Logging.mystical("Opening window:\n--- glfw: '%s'\n--- openGL: '%s'",  glfwGetVersionString(), GL45.glGetString(GL11.GL_VERSION));
        glfwShowWindow(handle);
    }

    public void close() {
        Logging.info("Closing safely");
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public void setVSync(boolean vSync) {
        glfwSwapInterval(vSync ? 1 : 0);
    }
}
