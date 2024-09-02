package game.test.com.game.voxel;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWErrorCallback.createPrint;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Objects;

import game.test.com.game.voxel.engine.Camera;
import game.test.com.game.voxel.engine.Shader;
import game.test.com.game.voxel.model.builders.ChunkMesh;
import game.test.com.game.voxel.model.shapes.Chunk;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

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

    private static final int windowWidth = 1800;
    private static final int windowHeight = 820;
    private long windowHandle;

    private Shader shader;
    private Camera camera;

    private ChunkMesh chunk;

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
        String windowTitle = "Hello, World!";
        windowHandle = glfwCreateWindow(windowWidth, windowHeight, windowTitle, NULL, NULL);

        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(windowHandle, pWidth, pHeight);
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            assert vidMode != null;
            glfwSetWindowPos(windowHandle, (vidMode.width() - pWidth.get(0)) / 2,
                    (vidMode.height() - pHeight.get(0)) / 2);
        }

        glfwMakeContextCurrent(windowHandle);
        glfwSwapInterval(0);
        glfwShowWindow(windowHandle);
        createCapabilities();
        System.out.println("OpenGL: " + glGetString(GL_VERSION));
        glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);

        GLFW.glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
            glViewport(0, 0, width, height);
        });

        Chunk chunk1 = new Chunk(16, 32, 16);
        chunk = new ChunkMesh(chunk1);
        chunk.defineBlocks();
        System.out.println(this.chunk.hasBlock(0, 23, 0));

        camera = new Camera(new Vector3f(chunk1.getChunkWidth(), chunk1.getChunkHeight(), chunk1.getChunkDepth() + 6),
                new Vector3f(0.0f, 1.0f, .0f),
                -90.0f, -10.0f, 0.5f);

        camera.setCameraSpeed(2.8f);

        shader = new Shader("src\\common\\shaders\\vertexShader.glsl",
                "src\\common\\shaders\\fragmentShader.glsl");

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
        double previousTime = glfwGetTime();
        int frameCount = 0;

        while (!glfwWindowShouldClose(windowHandle)) {

            // Measure speed
            double currentTime = glfwGetTime();
            frameCount++;

            // If a second has passed.
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            draw();

            glfwSwapBuffers(windowHandle);
            glfwPollEvents();

            if (currentTime - previousTime >= 1.0) {
                // Display the frame count here any way you want.
                glfwSetWindowTitle(windowHandle, String.valueOf(frameCount));

                frameCount = 0;
                previousTime = currentTime;
            }
        }
    }

    @Override
    public void close() {
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        shader.deleteShader();
        this.chunk.clearUp();
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public void draw() {

        Matrix4f view = camera.getLookAt();

        float FOV = camera.getZoom();
        float AspectRatio = (float) windowWidth / windowHeight;
        Matrix4f projection = new Matrix4f().perspective(FOV, AspectRatio, 1.0f, 100.0f);

        Vector3f lightColor = new Vector3f(1.0f, 1.0f, 1.0f);

        Vector3f diffuseColor = new Vector3f(lightColor).mul(0.3f);
        Vector3f ambientColor = new Vector3f(diffuseColor).mul(0.6f);

        // Draw Cube Object
        shader.bind();

        shader.setVec3("globalLight.direction", new Vector3f(5.0f, 16.0f, -6.0f));
        shader.setVec3("globalLight.ambient", new Vector3f(ambientColor));
        shader.setVec3("globalLight.diffuse", new Vector3f(diffuseColor));

        this.chunk.draw(shader);
        shader.setMat4("projection", projection);
        shader.setMat4("view", view);

        shader.unbind();

        camera.processInput(windowHandle);

    }
}