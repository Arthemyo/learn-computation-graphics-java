package game.test.com.game.voxel;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_U;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.glfw.GLFWErrorCallback.createPrint;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import game.test.com.game.voxel.model.shapes.Cube;

/**
 * @author Paul Nelson Baker
 * @see <a href="https://github.com/paul-nelson-baker/">GitHub</a>
 * @see <a href="https://www.linkedin.com/in/paul-n-baker/">LinkedIn</a>
 * @since 2019-05
 * <p>
 * Modified from <a href="https://www.lwjgl.org/guide">original
 * example</a>
 */
public class Main implements AutoCloseable, Runnable {

    private static final String windowTitle = "Hello, World!";
    private static final int windowWidth = 800;
    private static final int windowHeight = 600;
    private long windowHandle;

    private Shader shader;
    private Shader lightShader;

    private Camera camera;

    private Cube cubo;
    private Cube lightCube;

    private Vector3f[] cubePosition = {
            new Vector3f(0.0f, 0.0f, 0.0f),
            new Vector3f(2.0f, 5.0f, -15.0f),
            new Vector3f(-1.5f, -2.2f, -2.5f),
            new Vector3f(-3.8f, -2.0f, -12.3f),
            new Vector3f(2.4f, -0.4f, -3.5f),
            new Vector3f(-1.7f, 3.0f, -7.5f),
            new Vector3f(1.3f, -2.0f, -2.5f),
            new Vector3f(1.5f, 2.0f, -2.5f),
            new Vector3f(1.5f, 0.2f, -1.5f),
            new Vector3f(-1.3f, 1.0f, -1.5f)
    };

    public static void main(String... args) {
        try (Main main = new Main()) {
            main.run();
        }
    }

    /**
     * Convienience method that also satisfies Runnable
     */
    public void run() {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loop();
    }

    public void init() throws IOException {
        createPrint(System.err).set();
        System.out.println("Starting LWJGL " + Version.getVersion());
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        windowHandle = glfwCreateWindow(windowWidth, windowHeight, windowTitle, NULL, NULL);

        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(windowHandle, pWidth, pHeight);
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(windowHandle, (vidMode.width() - pWidth.get(0)) / 2,
                    (vidMode.height() - pHeight.get(0)) / 2);
        }
        glfwMakeContextCurrent(windowHandle);
        glfwSwapInterval(1);
        glfwShowWindow(windowHandle);
        createCapabilities();
        System.out.println("OpenGL: " + glGetString(GL_VERSION));
        glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);

        GLFW.glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
            glViewport(0, 0, width, height);
        });

        camera = new Camera(new Vector3f(0.0f, 3.0f, 8.0f),
                new Vector3f(0.0f, 1.0f, .0f),
                -90.0f, -10.0f, 0.5f);

        camera.setCameraSpeed(1.8f);

        shader = new Shader("src\\common\\shaders\\vertexShader.glsl",
                "src\\common\\shaders\\fragmentShader.glsl");

        lightShader = new Shader("src\\common\\shaders\\lightVertexShader.glsl",
                "src\\common\\shaders\\lightFragmentShader.glsl");

        cubo = new Cube(shader);
        lightCube = new Cube(lightShader);

        glEnable(GL30.GL_DEPTH_TEST);

        GLFW.glfwSetKeyCallback(windowHandle, (windowHandle, key, scancode, action, mods) -> {

            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(windowHandle, true);
            }

            if (glfwGetKey(windowHandle, GLFW_KEY_U) == GLFW_PRESS) {
                GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_LINE);
            }

            if (key == GLFW_KEY_U && action == GLFW_RELEASE) {
                GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_FILL);
            }

        });

    }

    public void loop() {
        while (!glfwWindowShouldClose(windowHandle)) {
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            draw();

            glfwSwapBuffers(windowHandle);
            glfwPollEvents();
        }
    }

    @Override
    public void close() {
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        shader.deleteShader();
        cubo.deleteBuffers();
        lightCube.deleteBuffers();
        lightShader.deleteShader();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void draw() {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            Matrix4f view = camera.getLookAt();

            float FOV = camera.getZoom();
            float AspectRatio = (float) windowWidth / windowHeight;
            Matrix4f projection = new Matrix4f().perspective(FOV, AspectRatio, 1.0f, 100.0f);

            Vector3f lightColor = new Vector3f(1.0f, 1.0f, 1.0f);
            Vector3f lightPos = new Vector3f(-0.2f, 1.0f, 2.0f);
            Vector3f colorObject = new Vector3f(1.0f, 0.5f, 0.31f);

            Vector3f diffuseColor = new Vector3f(lightColor).mul(0.5f);
            Vector3f ambientColor = new Vector3f(diffuseColor).mul(0.2f);

            // Draw Cube Object
            shader.bind();

            for (int i = 0; i < 10; i++) {

                Matrix4f model = new Matrix4f().identity();
                model.translate(cubePosition[i]);
                float angle = 20.0f * i;
                model.rotate((float) Math.toRadians(angle), new Vector3f(1.0f, 0.3f, 0.5f));

                shader.setMat4("model", model);
                cubo.drawCube();

            }

            shader.setVec3("objectColor", colorObject);
            shader.setVec3("lightColor", lightColor);
            shader.setVec3("viewPos", camera.getCameraPos());

            shader.setFloat("material.shininess", 64.0f);

            shader.setVec3("light.ambient", new Vector3f(ambientColor));
            shader.setVec3("light.diffuse", new Vector3f(diffuseColor));
            shader.setVec3("light.specular", new Vector3f(1.0f, 1.0f, 1.0f));

            shader.setVec3("light.position", camera.getCameraPos());
            shader.setVec3("light.direction", camera.getDirection());
            shader.setFloat("light.cutOff", (float) Math.cos(Math.toRadians(12.5f)));
            shader.setFloat("light.outerCutOff", (float) Math.cos(Math.toRadians(17.5f)));

            shader.setFloat("light.constant", 1.0f);
            shader.setFloat("light.linear", 0.009f);
            shader.setFloat("light.quadratic", 0.0032f);

            shader.setMat4("projection", projection);
            shader.setMat4("view", view);
            shader.unbind();

            lightShader.bind();
            lightCube.drawCube();
            Matrix4f modelLightCube = new Matrix4f().identity();
            modelLightCube.translate(lightPos);
            modelLightCube.scale(0.2f);
            lightShader.setMat4("model", modelLightCube);
            lightShader.setMat4("projection", projection);
            lightShader.setMat4("view", view);
            lightShader.unbind();

            camera.processInput(windowHandle);

        }
    }

}