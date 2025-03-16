
package game.test.com.game.voxel.model.builders;

import game.test.com.game.voxel.engine.Shader;
import game.test.com.game.voxel.model.intefaces.Entity;
import game.test.com.game.voxel.model.mesh.Mesh;
import game.test.com.game.voxel.model.mesh.Texture;
import game.test.com.game.voxel.model.mesh.Vertex;
import game.test.com.game.voxel.model.shapes.Chunk;
import game.test.com.game.voxel.model.shapes.World;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;


public class ChunksManagement implements Entity {

    private final Chunk chunk;
    private final World world;

    private final List<Vertex> vertices = new ArrayList<>();
    private final List<Texture> texture = new ArrayList<>();

    private Mesh mesh;
    private int verticesValids = 0;
    private int[] indices;

    public ChunksManagement(Chunk chunks, World world) {
        this.chunk = chunks;
        this.world = world;
        defineBlocks();
    }

    public void defineBlocks() {
        this.indices = getIndices(verticesValids);
        mesh = new Mesh(vertices, texture, this.indices);
    }

    public static int[] getIndices(int numVertices){

        int index = 0;
        List<Integer> indices = new ArrayList<>();

        for(int i = 0; i < numVertices; i+=4){
                indices.add(index);
                indices.add(index + 3);
                indices.add(index + 2);
                indices.add(index + 2);
                indices.add(index + 1);
                indices.add(index);
            index += 4;
        }

        int[] result = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            result[i] = indices.get(i);
        }

        return result;
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Shader shader) {
        glBindVertexArray(mesh.getVAO());
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

}