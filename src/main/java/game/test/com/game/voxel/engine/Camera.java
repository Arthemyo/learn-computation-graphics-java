package game.test.com.game.voxel.engine;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

public class Camera {

    private final Vector3f cameraPos;
    private final Vector3f up;
    private Vector3f cameraRight;
    private final Vector3f direction = new Vector3f();
    private float yaw;
    private float pitch;

    private float cameraSpeed;
    float deltaTime = 0.0f; // Time between current frame and last frame
    private float lastFrame = 0.0f;

    private float lastX = 400, lastY = 300;
    private boolean firstMouse = true;
    float sensitivity = 0.1f;
    private float Zoom = 45.0f;

    public Camera(Vector3f cameraPos, Vector3f up, float yaw, float pitch, float sensitivity) {
        this.cameraPos = cameraPos;
        this.up = up;
        this.yaw = yaw;
        this.pitch = pitch;
        this.cameraSpeed = sensitivity;
    }

    public Matrix4f getLookAt() {
        cameraRight = new Vector3f(direction).cross(up).normalize();

        direction.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        direction.y = (float) Math.sin(Math.toRadians(pitch));
        direction.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));

        return new Matrix4f().lookAt(
                new Vector3f(cameraPos), // Posição da câmera
                new Vector3f(cameraPos).add(new Vector3f(direction).normalize()), // Ponto para onde a câmera está olhando
                new Vector3f(up) // Vetor para cima
        );

    }

    public void processInput(long windowHandle) {

        float currentFrame = (float) glfwGetTime();
        deltaTime = currentFrame - lastFrame;
        lastFrame = currentFrame;

        float cameraSpeedFunc = cameraSpeed * deltaTime;

        GLFW.glfwSetCursorPosCallback(windowHandle, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                mouseCallback(window, xpos, ypos);
            }
        });

        glfwSetScrollCallback(windowHandle, new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                scroll_callback(window, xoffset, yoffset);
            }
        });

        if (glfwGetKey(windowHandle, GLFW_KEY_W) == GLFW_PRESS) {
            cameraPos.add(new Vector3f(direction).mul(cameraSpeedFunc));
        }

        if (glfwGetKey(windowHandle, GLFW_KEY_S) == GLFW_PRESS) {
            cameraPos.sub(new Vector3f(direction).mul(cameraSpeedFunc));
        }

        if (glfwGetKey(windowHandle, GLFW_KEY_D) == GLFW.GLFW_PRESS) {
            cameraPos.add(new Vector3f(cameraRight).mul(cameraSpeedFunc));
        }

        if (glfwGetKey(windowHandle, GLFW_KEY_A) == GLFW.GLFW_PRESS) {
            cameraPos.sub(new Vector3f(cameraRight).mul(cameraSpeedFunc));
        }
    }

    private void scroll_callback(long window, double xoffset, double yoffset) {
        Zoom -= (float) yoffset;
        if (Zoom < 1.0f)
            Zoom = 1.0f;
        if (Zoom > 45.0f)
            Zoom = 45.0f;
    }

    private void mouseCallback(long window, double xpos, double ypos) {
        if (firstMouse) // initially set to true
        {
            lastX = (float) xpos;
            lastY = (float) ypos;
            firstMouse = false;
        }

        float xoffset = (float) (xpos - lastX);
        float yoffset = (float) (lastY - ypos); // reversed: y ranges bottom to top

        lastX = (float) xpos;
        lastY = (float) ypos;

        xoffset *= sensitivity;
        yoffset *= sensitivity;

        yaw += xoffset;
        pitch += yoffset;

        if (pitch > 89.0f)
            pitch = 89.0f;
        if (pitch < -89.0f)
            pitch = -89.0f;

    }

    public float getCameraSpeed() {
        return cameraSpeed;
    }

    public void setCameraSpeed(float cameraSpeed) {
        this.cameraSpeed = cameraSpeed;
    }

    public float getZoom() {
        return (float) Math.toRadians(Zoom);
    }

    public Vector3f getCameraRight() {
        return cameraRight;
    }

    public Vector3f getCameraPos() {
        return cameraPos;
    }

    public Vector3f getDirection() {
        return direction;
    }
}
