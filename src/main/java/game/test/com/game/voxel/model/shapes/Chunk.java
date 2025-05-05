package game.test.com.game.voxel.model.shapes;

import game.test.com.game.voxel.model.mesh.Vertex;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class Chunk {

    public static final int CHUNK_WIDTH = 16;
    public static final int CHUNK_HEIGHT = 32;

    private final Block[] block;
    private final Vector3i origin;

    private final List<Vertex> vertices;

    public Chunk(Vector3i origin){
        this.origin = origin;
        vertices = new ArrayList<>();
        this.block = new Block[CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_WIDTH];
    }

    public Block getBlock(int x, int y, int z){
        return this.block[getIndex(x, y, z)];
    }

    public int getIndex(int x, int y, int z) {
        return  x + (y * CHUNK_WIDTH) + (z * CHUNK_WIDTH * CHUNK_HEIGHT); // Formula to convert 3D to 1D
    }

    public boolean hasBlock(int x, int y, int z) {
        try {
            Block block = this.block[getIndex(x, y, z)];
            return block.getPosition().x == x && block.getPosition().y == y && block.getPosition().z == z;
        } catch (Exception e) {
            return false;
        }
    }

    public void setBlock(int x, int y, int z, Block block){
        int index = getIndex(x, y, z);
        this.block[index] = block;
    }

    public Block[] getBlocks() {
        return block;
    }

    public Vector3i getOrigin() {
        return origin;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }
}
