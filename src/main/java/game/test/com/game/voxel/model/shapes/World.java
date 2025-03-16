package game.test.com.game.voxel.model.shapes;

import game.test.com.game.voxel.engine.Shader;
import game.test.com.game.voxel.model.intefaces.Entity;
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

import static java.lang.Math.max;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class World implements Entity {

    public static final int WORLD_WIDTH = 20;

    public static final int HAS_CHUNK_POS_Z = 1;
    public static final int HAS_CHUNK_NEG_Z = 2;
    public static final int HAS_CHUNK_POS_X = 4;
    public static final int HAS_CHUNK_NEG_X = 8;

    private final Chunk[][] chunks;
    private final ExecutorService executorService;

    private Mesh mesh;
    private List<Texture> texture = new ArrayList<>();
    private int[] indices;

    public World() {
        chunks = new Chunk[WORLD_WIDTH][WORLD_WIDTH];

        executorService = Executors.newFixedThreadPool(max(1, Runtime.getRuntime().availableProcessors() / 2), r -> {
            Thread t = new Thread(r);
            t.setPriority(Thread.MIN_PRIORITY);
            t.setName("Chunk builder");
            t.setDaemon(true);
            return t;
        });

        defineMeshWorld();
    }

    public void defineMeshWorld() {
        List<Vertex> vertices = new ArrayList<>();

        executorService.execute(() -> {
            for (int i = 0; i < WORLD_WIDTH; i++) {
                for (int j = 0; j < WORLD_WIDTH; j++) {
                    chunks[i][j] = new Chunk(new Vector3i(i * Chunk.CHUNK_WIDTH, 0, j * Chunk.CHUNK_WIDTH));
                }
            }
        });
        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(1800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        for (int i = 0; i < WORLD_WIDTH; i++) {
            for (int j = 0; j < WORLD_WIDTH; j++) {
                int chunksBits = 0;

                chunksBits |= hasChunk(i, j + 1) ? HAS_CHUNK_POS_Z : 0;
                chunksBits |= hasChunk(i, j - 1) ? HAS_CHUNK_NEG_Z : 0;
                chunksBits |= hasChunk(i + 1, j) ? HAS_CHUNK_POS_X : 0;
                chunksBits |= hasChunk(i - 1, j) ? HAS_CHUNK_NEG_X : 0;

                chunks[i][j].defineMesh(chunksBits, this);
                vertices.addAll(chunks[i][j].getVertices());
            }
        }

        loadTexture("src\\common\\textures\\texture_atlases.png",
                "texture_diffuse");

        indices = Chunk.getIndices(vertices.size());
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
    public void update() {

    }

    @Override
    public void draw(Shader shader) {
        Matrix4f model = new Matrix4f().identity();
        shader.setMat4("model", model);
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
