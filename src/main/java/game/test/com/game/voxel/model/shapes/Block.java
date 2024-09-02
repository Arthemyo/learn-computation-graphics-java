package game.test.com.game.voxel.model.shapes;

import game.test.com.game.voxel.engine.Shader;
import game.test.com.game.voxel.model.enums.BlockType;
import game.test.com.game.voxel.model.enums.FaceType;
import game.test.com.game.voxel.model.intefaces.Model;
import game.test.com.game.voxel.model.mesh.Mesh;
import game.test.com.game.voxel.model.mesh.Texture;
import game.test.com.game.voxel.model.mesh.Vertex;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class Block implements Model {

    private final List<Texture> textures;
    private final BlockType blockType;
    private final Vector3f position;
    private final List<Vertex> vertices;
    private Mesh mesh;

    private final int[] indices = {
            0, 1, 3, 3, 1, 2,
            4, 5, 7, 7, 5, 6,
            8, 9, 11, 11, 9, 10,
            12, 13, 15, 15, 13, 14,
            16, 17, 19, 19, 17, 18,
            20, 21, 23, 23, 21, 22
    };

    public Block(BlockType blockType) {
        this.blockType = blockType;
        this.position = new Vector3f();
        this.textures = new ArrayList<>();
        this.vertices = new ArrayList<>();
    }

    public void defineMesh() {
        this.mesh = new Mesh(vertices, textures, indices);
    }

    public void defineFaces(FaceType faceType) {
        try {
            Face face = new Face(faceType);

            if (!(this.blockType == BlockType.AIR)) {
                this.loadTexture("src\\common\\textures\\" + blockType.name() + ".png",
                        "texture_diffuse");
            }

            face.getVertice0().setPosition(face.getVertice0().getPosition().add(this.position.x,
                    this.position.y, this.position.z));
            face.getVertice1().setPosition(face.getVertice1().getPosition().add(this.position.x,
                    this.position.y, this.position.z));
            face.getVertice2().setPosition(face.getVertice2().getPosition().add(this.position.x,
                    this.position.y, this.position.z));
            face.getVertice3().setPosition(face.getVertice3().getPosition().add(this.position.x,
                    this.position.y, this.position.z));

            this.vertices.add(face.getVertice0());
            this.vertices.add(face.getVertice1());
            this.vertices.add(face.getVertice2());
            this.vertices.add(face.getVertice3());
        } catch (Exception e) {
            throw new RuntimeException("Draw Cube Failure: " + e.getMessage());
        }
    }

    public void loadTexture(String path, String typeTexture) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer nrChannels = stack.mallocInt(1);

            ByteBuffer buf = stbi_load(path, width, height, nrChannels,
                    4);

            int w = width.get();
            int h = height.get();

            if (buf == null) {
                throw new RuntimeException("Image file not loaded: "
                        + stbi_failure_reason());
            }

            Texture texture = new Texture(glGenTextures(), typeTexture);

            glBindTexture(GL_TEXTURE_2D, texture.getId());
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0,
                    GL_RGBA, GL_UNSIGNED_BYTE, buf);
            glGenerateMipmap(GL_TEXTURE_2D);

            stbi_image_free(buf);

            textures.add(texture);
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(int x, int y, int z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    @Override
    public void draw(Shader shader) {
        this.mesh.draw(shader);
    }

    public void clear() {
        this.mesh.clear();
    }
}
