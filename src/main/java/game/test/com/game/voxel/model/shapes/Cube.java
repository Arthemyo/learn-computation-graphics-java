package game.test.com.game.voxel.model.shapes;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import game.test.com.game.voxel.Shader;

public class Cube {
    private int idVAO;
    private int idEBO;
    private int idVBO;

    private IntBuffer width, height, nrChannels;
    private int textureId;
    private int texture2;

    private Shader shader;

    private float[] verticesTriangle = new float[] {

            // V0 - face back
            -0.5f, -0.5f, -0.5f, 0.0f, 0.3f, 0.0f, 0.0f, 0.0f,
            // V1 - face back
            0.5f, -0.5f, -0.5f, 0.0f, 0.3f, 0.0f, 1.0f, 0.0f,
            // V2 - face back
            0.5f, 0.5f, -0.5f, 0.0f, 0.3f, 0.0f, 1.0f, 1.0f,
            // V3 - face back
            -0.5f, 0.5f, -0.5f, 0.0f, 0.3f, 0.0f, 0.0f, 1.0f,

            // V4 - face front
            -0.5f, -0.5f, 0.5f, 0.0f, 0.3f, 0.0f, 1.0f, 0.0f,
            // V5 - face front
            0.5f, -0.5f, 0.5f, 0.0f, 0.3f, 0.0f, 0.0f, 0.0f,
            // V6 - face front
            0.5f, 0.5f, 0.5f, 0.0f, 0.3f, 0.0f, 0.0f, 1.0f,
            // V7 - face front
            -0.5f, 0.5f, 0.5f, 0.0f, 0.3f, 0.0f, 1.0f, 1.0f,

            // V8 - face left
            -0.5f, 0.5f, -0.5f, 0.0f, 0.3f, 0.0f, 1.0f, 1.0f,
            // V9 - face left
            -0.5f, 0.5f, 0.5f, 0.0f, 0.3f, 0.0f, 0.0f, 1.0f,
            // V10 - face left
            -0.5f, -0.5f, 0.5f, 0.0f, 0.3f, 0.0f, 0.0f, 0.0f,
            // V11 - face left
            -0.5f, -0.5f, -0.5f, 0.0f, 0.3f, 0.0f, 1.0f, 0.0f,

            // V12 - face right
            0.5f, 0.5f, -0.5f, 0.0f, 0.3f, 0.0f, 1.0f, 1.0f,
            // V13 - face right
            0.5f, 0.5f, 0.5f, 0.0f, 0.3f, 0.0f, 0.0f, 1.0f,
            // V14 - face right
            0.5f, -0.5f, 0.5f, 0.0f, 0.3f, 0.0f, 0.0f, 0.0f,
            // V15 - face right
            0.5f, -0.5f, -0.5f, 0.0f, 0.3f, 0.0f, 1.0f, 0.0f,

            // V16 - face top
            0.5f, 0.5f, -0.5f, 0.0f, 0.3f, 0.0f, 0.0f, 1.0f,
            // V17 - face top
            -0.5f, 0.5f, -0.5f, 0.0f, 0.3f, 0.0f, 1.0f, 1.0f,
            // V18 - face top
            0.5f, 0.5f, 0.5f, 0.0f, 0.3f, 0.0f, 0.0f, 0.0f,
            // V19 - face top
            -0.5f, 0.5f, 0.5f, 0.0f, 0.3f, 0.0f, 1.0f, 0.0f,

            // V20 - face bottom
            -0.5f, -0.5f, -0.5f, 0.0f, 0.3f, 0.0f, 1.0f, 1.0f,
            // V21 - face bottom
            -0.5f, -0.5f, 0.5f, 0.0f, 0.3f, 0.0f, 1.0f, 0.0f,
            // V22 - face bottom
            0.5f, -0.5f, 0.5f, 0.0f, 0.3f, 0.0f, 0.0f, 0.0f,
            // V23 - face bottom
            0.5f, -0.5f, -0.5f, 0.0f, 0.3f, 0.0f, 0.0f, 1.0f
    };

    private int indices[] = {
            // Back face
            3, 1, 0, 2, 1, 3,
            // Front face
            5, 6, 7, 7, 4, 5,
            // Left face
            9, 8, 10, 8, 11, 10,
            // Right face
            12, 13, 14, 14, 15, 12,
            // Top face
            16, 17, 19, 19, 18, 16,
            // Bottom face
            20, 23, 22, 22, 21, 20
    };

    public Cube(Shader shader) {
        idVBO = glGenBuffers();
        idVAO = glGenVertexArrays();
        idEBO = glGenBuffers();
        this.defineBuffers();
        this.loadTexture();
        this.shader = shader;
    }

    public void drawCube() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, texture2);

        shader.setInt("texture1", 0);
        shader.setInt("texture2", 1);
        
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
    }

    public void defineBuffers() {

        try (MemoryStack stack = MemoryStack.stackPush()) {

            FloatBuffer positionsBuffer = stack.callocFloat(verticesTriangle.length);
            System.out.println(verticesTriangle.length);
            positionsBuffer.put(0, verticesTriangle);
            
            // Bind my VBO with real buffer
            glBindBuffer(GL_ARRAY_BUFFER, idVBO);
            glBindVertexArray(idVAO);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idEBO);        

            // // Put into the buffer data the vertices
            glBufferData(GL_ARRAY_BUFFER, verticesTriangle, GL_STATIC_DRAW);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

            // We now tell to opengl how to interpret the verticeTriangle;
            // Position of vertex
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0 * Float.BYTES);
            glEnableVertexAttribArray(0);

            // Color of vertex
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
            glEnableVertexAttribArray(1);

            //Coordinate of textures
            glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
            glEnableVertexAttribArray(2);

        } catch (Exception e) {
            throw new RuntimeException("Draw Cube Failure: " + e.getMessage());
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
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w, h, 0,
                    GL_RGB, GL_UNSIGNED_BYTE, buf);
            glGenerateMipmap(GL_TEXTURE_2D);

            // Generate and bind texture 2
            texture2 = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texture2);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w2, h2, 0,
                    GL_RGBA, GL_UNSIGNED_BYTE, buf2);
            glGenerateMipmap(GL_TEXTURE_2D);

            stbi_image_free(buf);
            stbi_image_free(buf2);
        }
    }

    public void deleteBuffers() {
        glDeleteVertexArrays(idVAO);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
        glDeleteTextures(textureId);
        glDeleteTextures(texture2);
    }
}
