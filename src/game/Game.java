package src.game;

import org.lwjgl.opengl.*;
import src.Main;
import src.rendering.*;
import src.rendering.text.FontManager;
import src.rendering.text.TextRenderer;
import src.utility.Logging;
import src.utility.MathUtils;
import src.utility.Vec2;
import src.utility.Vec2f;

import java.awt.*;
import java.io.File;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Game {
    public Window window = new Window();

    ShaderHelper sh = new ShaderHelper();
    VertexArray va = new VertexArray();
    VertexBuffer vb = new VertexBuffer();

    TextRenderer.TextObject to1;
    TextRenderer tr = new TextRenderer();

    Vec2 mousePos = new Vec2();

    double timeStarted = 0;
    int secondsElapsed = 0;
    int frameCounter = 0;
    int fps = 0;

    public void start() {
        timeStarted = System.currentTimeMillis();
        Main.startTimeStepper(Constants.DT, this);
    }

    public void createCapabilitiesAndOpen() {
        window.setupGLFWContext();
        window.setVSync(Constants.V_SYNC);

        Renderer.setupGLContext();

        window.show();
        bindEvents();
        setupShaders();
        setupBuffers();

        FontManager.init();
        FontManager.loadFont(FontManager.FONT_JACQUARD, Font.PLAIN, 42, false);
        FontManager.generateAndBindAllFonts(sh);
    }

    public void close() {
        Logging.info("Closing safely");
        window.close();
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window.handle);
    }

    public void bindEvents() {
        GL45.glDebugMessageCallback(Logging.debugCallback(), -1);

        // key inputs
        glfwSetKeyCallback(window.handle, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);
            if (key == GLFW_KEY_W && action == GLFW_PRESS) {
                to1.setString("fps: %s", fps);
            }
        });

        glfwSetCursorPosCallback(window.handle, (window, xpos, ypos) -> mousePos.set(xpos, ypos));
    }

    public void setupBuffers() {
        vb.genId();
        StripBuilder2f sb = new StripBuilder2f();
        sb.setAdditionalVerts(3);
        sb.pushSeparatedVertices(new float[] {
                200, 300, 0, 1, 1,
                200, 100, 0, 0, 1,
                700, 300, 1, 1, 1,
                700, 100, 1, 0, 1
        });
        sb.pushSeparatedVertices(new float[] {
                50,  400, 0, 1, 2,
                50,  0,   0, 0, 2,
                300, 340, 1, 1, 2,
                300, 50,  1, 0, 2
        });
        vb.bufferData(sb.getSetVertices());

        va.genId();
        VertexArray.VertexArrayLayout layout = new VertexArray.VertexArrayLayout();
        layout.setupDefaultLayout();
        va.addBuffer(vb, layout);

        tr.setupBufferObjects();
        to1 = new TextRenderer.TextObject(1, "stringg!\na STRING@!!!!\n\nit's alive!", new Vec2f(10, 100));
        tr.pushTextObject(to1);
    }

    /** Must be called after window is visible */
    public void setupShaders() {
        sh.genProgram();
        sh.attachShadersInDir(new File(Constants.SHADERS_FOLDER));
        sh.linkProgram();
        sh.bind();

        sh.uniform2f("resolution", Constants.SCREEN_SIZE.width, Constants.SCREEN_SIZE.height);

        new Texture("res/textures/explosion.png").bind(1, sh);
        new Texture("res/textures/closed.png").bind(2, sh);
    }

    public void updateFps() {
        // updates every second
        int newSeconds = (int) Math.floor(MathUtils.millisToSecond(System.currentTimeMillis()) - MathUtils.millisToSecond(timeStarted));
        if (newSeconds != secondsElapsed) {
            fps = frameCounter;
            frameCounter = 0;
            secondsElapsed = newSeconds;
        }
    }

    public void render() {
        Renderer.clearScreen();
        sh.uniform1f("time", (float) glfwGetTime());

        Renderer.draw(GL_TRIANGLE_STRIP, va, 10);
        tr.draw();

        Renderer.finish(window);
    }

    public double mainLoop(double dt) {
        double tStart = System.nanoTime();
        frameCounter++;

        glfwPollEvents();
        updateFps();
        render();

        return MathUtils.nanoToSecond(System.nanoTime() - tStart);
    }
}
