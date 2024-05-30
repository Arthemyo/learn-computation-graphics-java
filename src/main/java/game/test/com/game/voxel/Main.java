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
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
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
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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
 *        <p>
 *        Modified from <a href="https://www.lwjgl.org/guide">original
 *        example</a>
 */
public class Main implements AutoCloseable, Runnable {

	private static final String windowTitle = "Hello, World!";
	private static final int windowWidth = 800;
	private static final int windowHeight = 600;
	private long windowHandle;
	private int vertexShaderId;
	private int fragementShaderId;
	private int idVAO;
	private int idEBO;

	private Shader shader;
	private IntBuffer width, height, nrChannels;
	private int textureId;
	private int texture2;

	private Camera camera;

	private float[] verticesTriangle = new float[] {
			-0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
			0.5f, -0.5f, -0.5f, 1.0f, 0.0f,
			0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
			0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
			-0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
			-0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
			-0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
			0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
			0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
			0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
			-0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
			-0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
			-0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
			-0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
			-0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
			-0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
			-0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
			-0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
			0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
			0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
			0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
			0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
			0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
			0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
			-0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
			0.5f, -0.5f, -0.5f, 1.0f, 1.0f,
			0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
			0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
			-0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
			-0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
			-0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
			0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
			0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
			0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
			-0.5f, 0.5f, 0.5f, 0.0f, 0.0f,
			-0.5f, 0.5f, -0.5f, 0.0f, 1.0f
	};

	private Vector3f[] positionCubes = {
			new Vector3f(0.0f, 0.0f, 0.0f),
	};

	private int indices[] = {
			0, 1, 3,
			3, 2, 1 };

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

		defineBuffer();

		camera = new Camera(new Vector3f(0.0f, 0.0f, 4.0f),
				new Vector3f(0.0f, 1.0f, 0.0f),
				-90.0f, 0.0f, 0.5f);

		camera.setCameraSpeed(2.5f);

		shader = new Shader("src\\common\\shaders\\vertexShader.glsl",
				"src\\common\\shaders\\fragmentShader.glsl");
		vertexShaderId = shader.getVertexShaderId();
		fragementShaderId = shader.getFragmentShaderId();

		loadTexture();
		glEnable(GL30.GL_DEPTH_TEST);

		glfwSetKeyCallback(windowHandle, (windowHandle, key, scancode, action, mods) -> {

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

			shader.bind();

			GL30.glActiveTexture(GL30.GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, textureId);

			GL30.glActiveTexture(GL30.GL_TEXTURE1);
			glBindTexture(GL_TEXTURE_2D, texture2);

			shader.setInt("texture1", 0);
			shader.setInt("texture2", 1);

			glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, idEBO);

			GL30.glBindVertexArray(idVAO);
			GL30.glDrawElements(GL30.GL_TRIANGLES, indices.length, GL30.GL_UNSIGNED_INT, 0);
			transfomation();

			shader.unbind();

			glfwSwapBuffers(windowHandle);
			glfwPollEvents();
		}
	}

	@Override
	public void close() {
		glfwFreeCallbacks(windowHandle);
		glfwDestroyWindow(windowHandle);
		GL30.glDeleteShader(vertexShaderId);
		GL30.glDeleteShader(fragementShaderId);
		glDeleteTextures(textureId);
		glDeleteTextures(texture2);
		GL30.glDeleteVertexArrays(idVAO);
		GL30.glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, 0);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	public void defineBuffer() {

		// Generate a id for my VBO (Vertex Buffer Object)
		int idVBO = glGenBuffers();
		idVAO = GL30.glGenVertexArrays();
		idEBO = GL30.glGenBuffers();

		try (MemoryStack stack = MemoryStack.stackPush()) {

			FloatBuffer positionsBuffer = stack.callocFloat(verticesTriangle.length);
			positionsBuffer.put(0, verticesTriangle);

			// Bind my VBO with real buffer
			GL30.glBindVertexArray(idVAO);
			glBindBuffer(GL_ARRAY_BUFFER, idVBO);

			glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, idEBO);

			// Put into the buffer data the vertices
			glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
			glBufferData(GL_ARRAY_BUFFER, verticesTriangle, GL_STATIC_DRAW);
			GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indices, GL30.GL_STATIC_DRAW);

			// We now tell to opengl how to interpret the verticeTriangle;
			// Position of vertex
			GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 5 * Float.BYTES, 0);
			GL30.glEnableVertexAttribArray(0);

			// Color of vertex
			GL30.glVertexAttribPointer(1, 3, GL30.GL_FLOAT, false, 5 * Float.BYTES, 12);
			GL30.glEnableVertexAttribArray(1);

			// Coordinate of textures
			// GL30.glVertexAttribPointer(2, 2, GL30.GL_FLOAT, false, 32, 24);
			// GL30.glEnableVertexAttribArray(2);

		} catch (Exception e) {
			throw new RuntimeException("Deu merda: " + e.getMessage());
		}
	}

	public void loadTexture() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			width = stack.mallocInt(1);
			height = stack.mallocInt(1);
			nrChannels = stack.mallocInt(1);

			IntBuffer width2 = stack.mallocInt(1);
			IntBuffer height2 = stack.mallocInt(1);
			IntBuffer nrChannels2 = stack.mallocInt(1);

			ByteBuffer buf = stbi_load("src\\common\\textures\\container.jpg", width, height, nrChannels, 0);
			stbi_set_flip_vertically_on_load(true);
			ByteBuffer buf2 = stbi_load("src\\common\\textures\\awesomeface.png", width2, height2, nrChannels2, 0);

			int w2 = width2.get();
			int h2 = height2.get();

			int w = width.get();
			int h = height.get();

			if (buf == null || buf2 == null) {
				throw new RuntimeException("Image file not loaded: "
						+ stbi_failure_reason());
			}

			// Generate and bind texture 1
			textureId = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, textureId);
			glTexParameteri(GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
			glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_RGB, w, h, 0,
					GL30.GL_RGB, GL_UNSIGNED_BYTE, buf);
			GL30.glGenerateMipmap(GL_TEXTURE_2D);

			// Generate and bind texture 2
			texture2 = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, texture2);
			glTexParameteri(GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
			glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_RGBA, w2, h2, 0,
					GL30.GL_RGBA, GL_UNSIGNED_BYTE, buf2);
			GL30.glGenerateMipmap(GL_TEXTURE_2D);

			stbi_image_free(buf);
			stbi_image_free(buf2);
		}
	}

	public void transfomation() {
		try (MemoryStack stack = MemoryStack.stackPush()){

			Matrix4f view = camera.getLookAt();

			float FOV = camera.getZoom();  
			float AspectRatio = windowWidth / windowHeight;

			Matrix4f projection = new Matrix4f().perspective(FOV, AspectRatio, 1.0f, 100.0f);

			for (int i = 0; i < positionCubes.length; i++) {
				Matrix4f model = new Matrix4f().identity();
				model.translate(positionCubes[i]);
				float angle = 20.0f * i;
				model.rotate((float) (glfwGetTime() * Math.toRadians(angle)), new Vector3f(1.0f, 0.0f, 0.0f));
				shader.setMat4("model", model);
				glDrawArrays(GL_TRIANGLES, 0, 36);
			}

			shader.setMat4("projection", projection);
			shader.setMat4("view", view);

			camera.processInput(windowHandle);

		}
	}

}
