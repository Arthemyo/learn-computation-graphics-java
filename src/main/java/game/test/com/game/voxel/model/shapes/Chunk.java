package game.test.com.game.voxel.model.shapes;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chunk {

    private int chunkHeight = 1;
    private int chunkWidth = 1;
    private int chunkDepth = 1;

    private List<Block> blocks;
    private Vector3f origin;

    public Chunk() {
        this.blocks = new ArrayList<>();
    }

    public Chunk(int chunkWidth, int chunkHeight, int chunkDepth) {
        this.chunkWidth = chunkWidth;
        this.chunkHeight = chunkHeight;
        this.chunkDepth = chunkDepth;
        this.blocks = new ArrayList<>();
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public int getChunkWidth() {
        return chunkWidth;
    }

    public int getChunkHeight() {
        return chunkHeight;
    }

    public int getChunkDepth() {
        return chunkDepth;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }
}
