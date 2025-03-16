package game.test.com.game.voxel.model.shapes;

import game.test.com.game.voxel.engine.Shader;
import game.test.com.game.voxel.model.enums.BlockType;
import game.test.com.game.voxel.model.intefaces.Entity;
import game.test.com.game.voxel.model.mesh.Mesh;
import game.test.com.game.voxel.model.mesh.Texture;
import game.test.com.game.voxel.model.mesh.Vertex;
import org.joml.Matrix4f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;


public class Chunk implements Entity {

    public static final int CHUNK_WIDTH = 16;
    public static final int CHUNK_HEIGHT = 32;

    public static final int CHUNK_POS_Z = 1;
    public static final int CHUNK_NEG_Z = 2;
    public static final int CHUNK_POS_X = 4;
    public static final int CHUNK_NEG_X = 8;
    public static final int CHUNK_POS_Y = 16;
    public static final int CHUNK_NEG_Y = 32;

    private int CHUNKS_AROUND = 0;

    private final Block[] blocks;
    private final Vector3i origin;

    private World world;

    private Mesh mesh;
    private int[] indices;

    private final List<Vertex> vertices;
    private final List<Texture> texture;

    public Chunk(Vector3i origin) {
        this.blocks = new Block[CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_WIDTH];

        vertices = new ArrayList<>();
        texture = new ArrayList<>();

        this.origin = origin;

        defineBlocks();
    }

    public void defineBlocks() {
        for (int x = 0; x < CHUNK_WIDTH; x++) {
            for (int y = 0; y < CHUNK_HEIGHT; y++) {
                for (int z = 0; z < CHUNK_WIDTH; z++) {
                    Block block = new Block(getBlockType(x + this.origin.x, y + this.origin.y, z + this.origin.z));
                    block.setPosition(x, y, z);
                    setBlock(x, y, z, block);
                }
            }
        }
    }

    private int getIndex(int x, int y, int z) {
        return  x + (y * CHUNK_WIDTH) + (z * CHUNK_WIDTH * CHUNK_HEIGHT); // Formula to convert 3D to 1D
    }

    public void defineMesh(int chunksBit, World world){

        this.CHUNKS_AROUND = chunksBit;
        this.world = world;

        for (Block block : blocks) {
            defineFaceMesh(block);
        }
    }

    public void defineFaceMesh(Block block) {
        int faceBits = 0;

        Chunk cpz = ((CHUNKS_AROUND & World.HAS_CHUNK_POS_Z) == 1) ? world.getChunks()[(this.origin.x / CHUNK_WIDTH)][(this.origin.z / CHUNK_WIDTH) + 1] : null;
        Chunk cnz = ((CHUNKS_AROUND & World.HAS_CHUNK_NEG_Z) == 2) ? world.getChunks()[(this.origin.x / CHUNK_WIDTH)][(this.origin.z / CHUNK_WIDTH) - 1] : null;
        Chunk cpx = ((CHUNKS_AROUND & World.HAS_CHUNK_POS_X) == 4) ? world.getChunks()[(this.origin.x / CHUNK_WIDTH) + 1][(this.origin.z / CHUNK_WIDTH)] : null;
        Chunk cnx = ((CHUNKS_AROUND & World.HAS_CHUNK_NEG_X) == 8) ? world.getChunks()[(this.origin.x / CHUNK_WIDTH) - 1][(this.origin.z / CHUNK_WIDTH)] : null;

        Block bpz = (hasBlock((int) block.getPosition().x, (int) block.getPosition().y, (int) block.getPosition().z + 1) &&
                getBlock((int) block.getPosition().x, (int) block.getPosition().y, (int) block.getPosition().z + 1).getBlockType() != BlockType.AIR)
                ? getBlock((int) block.getPosition().x, (int) block.getPosition().y, (int) block.getPosition().z + 1) :
                (block.getPosition().z == CHUNK_WIDTH - 1 && cpz != null && cpz.hasBlock((int) block.getPosition().x, (int) block.getPosition().y, 0)
                        && cpz.getBlock((int) block.getPosition().x, (int) block.getPosition().y, 0).getBlockType() != BlockType.AIR)
                        ? cpz.getBlock((int) block.getPosition().x, (int) block.getPosition().y, 0) : null;

        Block bnz = (hasBlock((int) block.getPosition().x, (int) block.getPosition().y, (int) block.getPosition().z - 1) &&
                getBlock((int) block.getPosition().x, (int) block.getPosition().y, (int) block.getPosition().z - 1).getBlockType() != BlockType.AIR)
                ? getBlock((int) block.getPosition().x, (int) block.getPosition().y, (int) block.getPosition().z - 1) :
                (block.getPosition().z == 0 && cnz != null && cnz.hasBlock((int) block.getPosition().x, (int) block.getPosition().y, CHUNK_WIDTH - 1)
                        && cnz.getBlock((int) block.getPosition().x, (int) block.getPosition().y, CHUNK_WIDTH - 1).getBlockType() != BlockType.AIR)
                        ? cnz.getBlock((int) block.getPosition().x, (int) block.getPosition().y, CHUNK_WIDTH - 1) : null;

        Block bpx = (hasBlock((int) block.getPosition().x + 1, (int) block.getPosition().y, (int) block.getPosition().z) &&
                getBlock((int) block.getPosition().x + 1, (int) block.getPosition().y, (int) block.getPosition().z).getBlockType() != BlockType.AIR)
                ? getBlock((int) block.getPosition().x + 1, (int) block.getPosition().y, (int) block.getPosition().z) :
                (block.getPosition().x == CHUNK_WIDTH - 1 && cpx != null && cpx.hasBlock(0, (int) block.getPosition().y, (int) block.getPosition().z)
                        && cpx.getBlock(0, (int) block.getPosition().y, (int) block.getPosition().z).getBlockType() != BlockType.AIR)
                        ? cpx.getBlock(0, (int) block.getPosition().y, (int) block.getPosition().z) : null;

        Block bnx = (hasBlock((int) block.getPosition().x - 1, (int) block.getPosition().y, (int) block.getPosition().z) &&
                getBlock((int) block.getPosition().x - 1, (int) block.getPosition().y, (int) block.getPosition().z).getBlockType() != BlockType.AIR)
                ? getBlock((int) block.getPosition().x - 1, (int) block.getPosition().y, (int) block.getPosition().z) :
                (block.getPosition().x == 0 && cnx != null && cnx.hasBlock(CHUNK_WIDTH - 1, (int) block.getPosition().y, (int) block.getPosition().z)
                        && cnx.getBlock(CHUNK_WIDTH - 1, (int) block.getPosition().y, (int) block.getPosition().z).getBlockType() != BlockType.AIR)
                        ? cnx.getBlock(CHUNK_WIDTH - 1, (int) block.getPosition().y, (int) block.getPosition().z) : null;

        Block bpy = (hasBlock((int) block.getPosition().x, (int) block.getPosition().y + 1, (int) block.getPosition().z) &&
                        getBlock((int) block.getPosition().x, (int) block.getPosition().y + 1, (int) block.getPosition().z).getBlockType() != BlockType.AIR )
                        ? getBlock((int) block.getPosition().x, (int) block.getPosition().y + 1, (int) block.getPosition().z) : null;

        Block bny = hasBlock((int) block.getPosition().x, (int) block.getPosition().y - 1, (int) block.getPosition().z) &&
                        getBlock((int) block.getPosition().x, (int) block.getPosition().y - 1, (int) block.getPosition().z).getBlockType() != BlockType.AIR
                        ? getBlock((int) block.getPosition().x, (int) block.getPosition().y - 1, (int) block.getPosition().z) : null;


        faceBits |= bpz == null ? CHUNK_POS_Z : 0;
        faceBits |= bnz == null ? CHUNK_NEG_Z : 0;
        faceBits |= bpx == null ? CHUNK_POS_X : 0;
        faceBits |= bnx == null ? CHUNK_NEG_X : 0;
        faceBits |= bpy == null ? CHUNK_POS_Y : 0;
        faceBits |= bny == null ? CHUNK_NEG_Y : 0;

        block.defineFaces(faceBits, origin);

        vertices.addAll(block.getVertices());
    }

    public boolean hasBlock(int x, int y, int z) {
        try {
            Block block = blocks[getIndex(x, y, z)];
            return block.getPosition().x == x && block.getPosition().y == y && block.getPosition().z == z;
        } catch (Exception e) {
            return false;
        }
    }

    public BlockType getBlockType(int x, int y, int z) {
        float frequence = 0.09f;
        float amplitude = 6;

        float xOffSet = (float) (Math.sin(x * frequence) * amplitude);
        float zOffSet = (float) (Math.sin(z * frequence) * amplitude);

        float surfaceY = 12 + xOffSet + zOffSet;

        if((y > surfaceY)){
            return BlockType.AIR;
        }
        else if (y > 14){
            return BlockType.GRASS;
        }

        else if (y > 12){
            return BlockType.DIRT;
        }

        return BlockType.STONE;
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
        Matrix4f model = new Matrix4f().identity();
        shader.setMat4("model", model);
        glBindVertexArray(mesh.getVAO());
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void setBlock(int x, int y, int z, Block block){
        int index = getIndex(x, y, z);
        blocks[index] = block;
    }

    public Block getBlock(int x, int y, int z){
        return blocks[getIndex(x, y, z)];
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Texture> getTexture() {
        return texture;
    }

    public Mesh getMesh() {
        return mesh;
    }
}
