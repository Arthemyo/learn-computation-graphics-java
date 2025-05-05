package game.test.com.game.voxel.model.shapes;

import game.test.com.game.voxel.model.enums.BlockType;
import game.test.com.game.voxel.model.mesh.Vertex;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Block {
    private final BlockType blockType;
    private final Vector3f position;

    private final List<Vertex> vertices;

    private final boolean isCullSelf;
    private int facesBits;

    public Block(BlockType blockType, Vector3f position) {
        this.blockType = blockType;
        this.position = position;
        this.isCullSelf = blockType == BlockType.AIR;
        vertices = new ArrayList<>();
        this.facesBits = 0;
    }

    public boolean getIsCullSelf(){
        return isCullSelf;
    }

    public BlockType getBlockType() {
        return blockType;
    }

    public void setPosition(int x, int y, int z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setFacesBits(int facesBits) {
        this.facesBits = facesBits;
    }

    public int getFacesBits() {
        return facesBits;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }
}
