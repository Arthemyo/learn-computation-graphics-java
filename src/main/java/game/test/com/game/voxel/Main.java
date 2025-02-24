package game.test.com.game.voxel;

import  static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWErrorCallback.createPrint;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Random;

import game.test.com.game.voxel.engine.Camera;
import game.test.com.game.voxel.engine.Shader;
import game.test.com.game.voxel.engine.Model;
import game.test.com.game.voxel.model.mesh.Mesh;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;


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
    private static final int windowWidth = 820;
    private static final int windowHeight = 680;
    private long windowHandle;

    private Shader shader;
    private Shader lightShader;

    private Camera camera;

    private Model planet;
    private Model rock;

    private final int amount = 10000;
    private final Matrix4f[] modalMatrices = new Matrix4f[amount];
    int intanceVBO;

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

        glEnable(GL30.GL_DEPTH_TEST);

        camera = new Camera(new Vector3f(0.0f, 3.0f, 28.0f),
                new Vector3f(0.0f, 1.0f, .0f),
                -90.0f, -10.0f, 0.5f);

        camera.setCameraSpeed(10.8f);

        shader = new Shader("src\\common\\shaders\\vertexShader.glsl",
                "src\\common\\shaders\\fragmentShader.glsl");

        lightShader = new Shader("src\\common\\shaders\\lightVertexShader.glsl",
                "src\\common\\shaders\\lightFragmentShader.glsl");


        rock = new Model("src\\common\\obj3d\\rock\\rock.obj");
        planet = new Model("src\\common\\obj3d\\planet\\planet.obj");

        Random random = new Random((long) glfwGetTime());
        float radius = 50.0f;
        float offset = 15.0f;

        for (int i = 0; i < modalMatrices.length; i++){
            Matrix4f model = new Matrix4f().identity();

            // 1. translation: displace along circle with 'radius' in range [-offset, offset]
            float angle = (float) i / (float) amount * 360.0f;
            float displacement = (random.nextInt((int) (2 * offset * 100)) / 100.0f) - offset;
            float x = (float) Math.sin(Math.toRadians(angle)) * radius + displacement;
            displacement = (random.nextInt((int) (2 * offset * 100)) / 100.0f) - offset;
            float y = displacement * 0.4f; // keep height of asteroid field smaller compared to width of x and z
            displacement = (random.nextInt((int) (2 * offset * 100)) / 100.0f) - offset;
            float z = (float) Math.cos(Math.toRadians(angle)) * radius + displacement;
            model.translate(new Vector3f(x, y, z));

            // 2. scale: Scale between 0.05 and 0.25f
            float scale = (random.nextInt(20) / 100.0f) + 0.05f;
            model.scale(scale);

            // 3. rotation: add random rotation around a (semi)randomly picked rotation axis vector
            float rotAngle = random.nextInt(360);
            model.rotate((float) Math.toRadians(rotAngle), new Vector3f(0.4f, 0.6f, 0.8f));

            // 4. now add to list of matrices
            modalMatrices[i] = model;
        }

        FloatBuffer mat4BufferInstancing = MemoryUtil.memAllocFloat(amount * 16);

        for(Matrix4f matrix : modalMatrices) {
            System.out.println(matrix);
        }

        for(Matrix4f modalMatrix : modalMatrices) {
            mat4BufferInstancing.put(modalMatrix.m00()).put(modalMatrix.m01()).put(modalMatrix.m02()).put(modalMatrix.m03())
                                .put(modalMatrix.m10()).put(modalMatrix.m11()).put(modalMatrix.m12()).put(modalMatrix.m13())
                                .put(modalMatrix.m20()).put(modalMatrix.m21()).put(modalMatrix.m22()).put(modalMatrix.m23())
                                .put(modalMatrix.m30()).put(modalMatrix.m31()).put(modalMatrix.m32()).put(modalMatrix.m33());
        }

        mat4BufferInstancing.flip();

        intanceVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, intanceVBO);
        glBufferData(GL_ARRAY_BUFFER, mat4BufferInstancing, GL_STATIC_DRAW);

        for(Mesh mesh : rock.getMeshes()){
            glBindVertexArray(mesh.getVAO());

            glVertexAttribPointer(3, 4, GL_FLOAT, false, 16 * Float.BYTES, 0);
            glVertexAttribDivisor(3, 1);
            glEnableVertexAttribArray(3);

            glVertexAttribPointer(4, 4, GL_FLOAT, false, 16 * Float.BYTES, 4 * Float.BYTES);
            glVertexAttribDivisor(4, 1);
            glEnableVertexAttribArray(4);

            glVertexAttribPointer(5, 4, GL_FLOAT, false, 16 * Float.BYTES, 2 * 4 * Float.BYTES);
            glVertexAttribDivisor(5, 1);
            glEnableVertexAttribArray(5);

            glVertexAttribPointer(6, 4, GL_FLOAT, false, 16 * Float.BYTES, 3 * 4 * Float.BYTES);
            glVertexAttribDivisor(6, 1);
            glEnableVertexAttribArray(6);

            glBindVertexArray(0);
        }
        mat4BufferInstancing.clear();

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
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
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
        lightShader.deleteShader();
        planet.getMeshes().forEach(Mesh::clear);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void draw() {
        Matrix4f view = camera.getLookAt();

        float FOV = camera.getZoom();
        float AspectRatio = (float) windowWidth / windowHeight;
        Matrix4f projection = new Matrix4f().perspective(FOV, AspectRatio, 1.0f, 400.0f);

        //Draw Planet
        shader.bind();
        shader.setMat4("projection", projection);
        shader.setMat4("view", view);

        Matrix4f model = new Matrix4f().identity();
        model.translate(0.0f, -3.0f, 0.0f);
        model.scale(4.0f, 4.0f, 4.0f);
        shader.setMat4("model", model);
        planet.draw(shader);
        shader.unbind();

        //Draw Rock
        lightShader.bind();
        lightShader.setMat4("projection", projection);
        lightShader.setMat4("view", view);

        lightShader.setInt("texture_diffuse1", 0);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, rock.getMeshes().get(0).getTextures().get(0).getId());

        for(Mesh mesh : rock.getMeshes()){
            glBindVertexArray(mesh.getVAO());
            glDrawElementsInstanced(GL_TRIANGLES, mesh.getIndices().length, GL_UNSIGNED_INT, 0, amount);
            glBindVertexArray(0);
        }
        lightShader.unbind();

        camera.processInput(windowHandle);
    }
}