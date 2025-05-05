package game.test.com.game.voxel;

import static game.test.com.game.voxel.model.shapes.Chunk.CHUNK_HEIGHT;
import static game.test.com.game.voxel.model.shapes.Chunk.CHUNK_WIDTH;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
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
import game.test.com.game.voxel.model.shapes.World;
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

    private static final int windowWidth = 1080;
    private static final int windowHeight = 720;
    public static long windowHandle;

    private Shader shader;
    private Camera camera;

    private World world;

    public static void main(String... args) {
        try (Main main = new Main()) {
            main.run();
        }
    }

    public void run() {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        loop();
    }

    public void init() throws IOException, InterruptedException {
        setupInit();

        world = new World();

        camera = new Camera(new Vector3f((float) World.WORLD_SIZE * CHUNK_WIDTH / 2, CHUNK_HEIGHT + 5, (float) World.WORLD_SIZE * CHUNK_WIDTH / 2),
                new Vector3f(0.0f, 1.0f, 0.0f),
                -90.0f, -10.0f, 0.5f);
        camera.setCameraSpeed(10f);

        shader = new Shader("src\\common\\shaders\\chunk.vert", "src\\common\\shaders\\chunk.frag");
    }

    public void loop() {
        double previousTime = glfwGetTime();
        int frameCount = 0;

        while (!glfwWindowShouldClose(windowHandle)) {
            // Measure speede
            double currentTime = glfwGetTime();
            frameCount++;

            if (currentTime - previousTime >= 1.0) {
                // Display the frame count here any way you want.
                glfwSetWindowTitle(windowHandle, String.valueOf(frameCount));

                frameCount = 0;
                previousTime = currentTime;
            }

            // If a second has passed.
            glClearColor(0.2f, 0.6f, 0.8f, 1.0f);
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
        world.clear();
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public void draw() {

        Matrix4f view = camera.getLookAt();
        float FOV = camera.getZoom();
        float AspectRatio = (float) windowWidth / windowHeight;

        Matrix4f projection = new Matrix4f().perspective(FOV, AspectRatio, 0.1f, 500.0f);

        Vector3f lightColor = new Vector3f(1.0f, 1.0f, 1.0f);

        Vector3f diffuseColor = new Vector3f(lightColor).mul(0.3f);
        Vector3f ambientColor = new Vector3f(diffuseColor).mul(0.6f);

        shader.bind();
        shader.setVec3("globalLight.direction", new Vector3f(5.0f, 16.0f, -6.0f));
        shader.setVec3("globalLight.ambient", new Vector3f(ambientColor));
        shader.setVec3("globalLight.diffuse", new Vector3f(diffuseColor));
        shader.setMat4("projection", projection);
        shader.setMat4("view", view);
        world.update(shader);
        world.draw(shader);
        shader.unbind();

        camera.processInput(windowHandle);
    }

    public void setupInit() {
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
        System.out.println("Thread Main Context" + " : " + glfwGetCurrentContext());

        GLFW.glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
            glViewport(0, 0, width, height);
        });

        glEnable(GL30.GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        GLFW.glfwSetKeyCallback(windowHandle, (windowHandle, key, scancode, action, mods) -> {

            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(windowHandle, true);
            }

            if (glfwGetKey(windowHandle, GLFW_KEY_E) == GLFW_PRESS) {
                GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_LINE);
            }

            if (key == GLFW_KEY_E && action == GLFW_RELEASE) {
                GL30.glPolygonMode(GL30.GL_FRONT_AND_BACK, GL30.GL_FILL);
            }

        });
    }
}