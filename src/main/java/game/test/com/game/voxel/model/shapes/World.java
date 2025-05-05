package game.test.com.game.voxel.model.shapes;

import game.test.com.game.voxel.engine.Shader;
import game.test.com.game.voxel.model.builders.ChunkBuilder;
import game.test.com.game.voxel.model.intefaces.Render;
import game.test.com.game.voxel.model.mesh.Mesh;
import game.test.com.game.voxel.model.mesh.Texture;
import game.test.com.game.voxel.model.mesh.Vertex;
import org.joml.Matrix4f;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static game.test.com.game.voxel.model.shapes.Chunk.CHUNK_WIDTH;
import static java.lang.Math.max;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class World implements Render {

    public static final int WORLD_SIZE = 2;

    public static final int HAS_CHUNK_POS_Z = 1;
    public static final int HAS_CHUNK_NEG_Z = 2;
    public static final int HAS_CHUNK_POS_X = 4;
    public static final int HAS_CHUNK_NEG_X = 8;

    private final Chunk[][] chunks;

    private Mesh mesh;
    private final List<Texture> texture = new ArrayList<>();
    private int[] indices;

    public World() {
        chunks = new Chunk[WORLD_SIZE][WORLD_SIZE];

        defineMeshWorld();
    }

    public void defineMeshWorld() {
        List<Vertex> vertices = new ArrayList<>();

        for (int i = 0; i < WORLD_SIZE; i++) {
            for (int j = 0; j < WORLD_SIZE; j++) {
                chunks[i][j] = new Chunk(new Vector3i(i * CHUNK_WIDTH, 0, j * CHUNK_WIDTH));
                ChunkBuilder.defineBlocks(chunks[i][j]);
            }
        }

        for (int i = 0; i < WORLD_SIZE; i++) {
            for (int j = 0; j < WORLD_SIZE; j++) {
                int chunksBits = 0;

                chunksBits |= hasChunk(i, j + 1) ? HAS_CHUNK_POS_Z : 0;
                chunksBits |= hasChunk(i, j - 1) ? HAS_CHUNK_NEG_Z : 0;
                chunksBits |= hasChunk(i + 1, j) ? HAS_CHUNK_POS_X : 0;
                chunksBits |= hasChunk(i - 1, j) ? HAS_CHUNK_NEG_X : 0;

                ChunkBuilder.defineMesh(chunks[i][j], chunksBits, this);
                vertices.addAll(chunks[i][j].getVertices());
            }
        }

        loadTexture("src\\common\\textures\\texture_atlases.png",
                "texture_diffuse");

        indices = ChunkBuilder.getIndices(vertices.size());
        mesh = new Mesh(vertices, texture, indices);
    }

    public boolean hasChunk(int x, int y) {
        try {
            return chunks[x][y] != null;
        } catch (Exception e) {
            return false;
        }
    }

    public void loadTexture(String path, String typeTexture) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer nrChannels = stack.mallocInt(1);

            ByteBuffer buf = stbi_load(path, width, height, nrChannels, 4);
            int w = width.get();
            int h = height.get();

            if (buf == null) {
                throw new RuntimeException("Image file not loaded: " + stbi_failure_reason());
            }

            Texture texture = new Texture(glGenTextures(), typeTexture, path);

            glBindTexture(GL_TEXTURE_2D, texture.getId());

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0,
                    GL_RGBA, GL_UNSIGNED_BYTE, buf);
            glGenerateMipmap(GL_TEXTURE_2D);

            stbi_image_free(buf);

            this.texture.add(texture);
        }
    }

    @Override
    public void update(Shader shader) {
        Matrix4f model = new Matrix4f().identity();
        shader.setMat4("model", model);
    }

    @Override
    public void draw(Shader shader) {
        glBindVertexArray(mesh.getVAO());
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public Chunk[][] getChunks() {
        return chunks;
    }

    public void clear() {
        mesh.clear();
    }

}
