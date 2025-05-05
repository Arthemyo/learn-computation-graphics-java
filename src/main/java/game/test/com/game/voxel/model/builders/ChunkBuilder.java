package game.test.com.game.voxel.model.builders;

import game.test.com.game.voxel.model.enums.BlockType;
import game.test.com.game.voxel.model.shapes.Block;
import game.test.com.game.voxel.model.shapes.Chunk;
import game.test.com.game.voxel.model.shapes.World;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static game.test.com.game.voxel.model.shapes.Chunk.CHUNK_HEIGHT;
import static game.test.com.game.voxel.model.shapes.Chunk.CHUNK_WIDTH;


public class ChunkBuilder {

    public static final int CHUNK_POS_Z = 1;
    public static final int CHUNK_NEG_Z = 2;
    public static final int CHUNK_POS_X = 4;
    public static final int CHUNK_NEG_X = 8;
    public static final int CHUNK_POS_Y = 16;
    public static final int CHUNK_NEG_Y = 32;

    public static void defineBlocks(Chunk chunk) {
        for (int x = 0; x < CHUNK_WIDTH; x++) {
            for (int y = 0; y < CHUNK_HEIGHT; y++) {
                for (int z = 0; z < CHUNK_WIDTH; z++) {
                    Block block = new Block(getBlockType(x + chunk.getOrigin().x, y + chunk.getOrigin().y, z + chunk.getOrigin().z),
                            new Vector3f(x, y, z));
                    chunk.setBlock(x, y, z, block);
                }
            }
        }
    }

    public static void defineMesh(Chunk chunk, int chunksBit, World world){

        for (Block block : chunk.getBlocks()) {
            defineFaceMesh(block, chunk, chunksBit, world);
        }
    }

    public static void defineFaceMesh(Block block, Chunk chunk, int chunksBit, World world) {
        int faceBits = 0;

        Chunk cpz = ((chunksBit & World.HAS_CHUNK_POS_Z) == 1) ? world.getChunks()[(chunk.getOrigin().x / CHUNK_WIDTH)][(chunk.getOrigin().z / CHUNK_WIDTH) + 1] : null;
        Chunk cnz = ((chunksBit & World.HAS_CHUNK_NEG_Z) == 2) ? world.getChunks()[(chunk.getOrigin().x / CHUNK_WIDTH)][(chunk.getOrigin().z / CHUNK_WIDTH) - 1] : null;
        Chunk cpx = ((chunksBit & World.HAS_CHUNK_POS_X) == 4) ? world.getChunks()[(chunk.getOrigin().x / CHUNK_WIDTH) + 1][(chunk.getOrigin().z / CHUNK_WIDTH)] : null;
        Chunk cnx = ((chunksBit & World.HAS_CHUNK_NEG_X) == 8) ? world.getChunks()[(chunk.getOrigin().x / CHUNK_WIDTH) - 1][(chunk.getOrigin().z / CHUNK_WIDTH)] : null;

        Block bpz = (chunk.hasBlock((int) block.getPosition().x, (int) block.getPosition().y, (int) block.getPosition().z + 1) &&
                chunk.getBlock((int) block.getPosition().x, (int) block.getPosition().y, (int) block.getPosition().z + 1).getBlockType() != BlockType.AIR)
                ? chunk.getBlock((int) block.getPosition().x, (int) block.getPosition().y, (int) block.getPosition().z + 1) :
                (block.getPosition().z == CHUNK_WIDTH - 1 && cpz != null && cpz.hasBlock((int) block.getPosition().x, (int) block.getPosition().y, 0)
                        && cpz.getBlock((int) block.getPosition().x, (int) block.getPosition().y, 0).getBlockType() != BlockType.AIR)
                        ? cpz.getBlock((int) block.getPosition().x, (int) block.getPosition().y, 0) : null;

        Block bnz = (chunk.hasBlock((int) block.getPosition().x, (int) block.getPosition().y, (int) block.getPosition().z - 1) &&
                chunk.getBlock((int) block.getPosition().x, (int) block.getPosition().y, (int) block.getPosition().z - 1).getBlockType() != BlockType.AIR)
                ? chunk.getBlock((int) block.getPosition().x, (int) block.getPosition().y, (int) block.getPosition().z - 1) :
                (block.getPosition().z == 0 && cnz != null && cnz.hasBlock((int) block.getPosition().x, (int) block.getPosition().y, CHUNK_WIDTH - 1)
                        && cnz.getBlock((int) block.getPosition().x, (int) block.getPosition().y, CHUNK_WIDTH - 1).getBlockType() != BlockType.AIR)
                        ? cnz.getBlock((int) block.getPosition().x, (int) block.getPosition().y, CHUNK_WIDTH - 1) : null;

        Block bpx = (chunk.hasBlock((int) block.getPosition().x + 1, (int) block.getPosition().y, (int) block.getPosition().z) &&
                chunk.getBlock((int) block.getPosition().x + 1, (int) block.getPosition().y, (int) block.getPosition().z).getBlockType() != BlockType.AIR)
                ? chunk.getBlock((int) block.getPosition().x + 1, (int) block.getPosition().y, (int) block.getPosition().z) :
                (block.getPosition().x == CHUNK_WIDTH - 1 && cpx != null && cpx.hasBlock(0, (int) block.getPosition().y, (int) block.getPosition().z)
                        && cpx.getBlock(0, (int) block.getPosition().y, (int) block.getPosition().z).getBlockType() != BlockType.AIR)
                        ? cpx.getBlock(0, (int) block.getPosition().y, (int) block.getPosition().z) : null;

        Block bnx = (chunk.hasBlock((int) block.getPosition().x - 1, (int) block.getPosition().y, (int) block.getPosition().z) &&
                chunk.getBlock((int) block.getPosition().x - 1, (int) block.getPosition().y, (int) block.getPosition().z).getBlockType() != BlockType.AIR)
                ? chunk.getBlock((int) block.getPosition().x - 1, (int) block.getPosition().y, (int) block.getPosition().z) :
                (block.getPosition().x == 0 && cnx != null && cnx.hasBlock(CHUNK_WIDTH - 1, (int) block.getPosition().y, (int) block.getPosition().z)
                        && cnx.getBlock(CHUNK_WIDTH - 1, (int) block.getPosition().y, (int) block.getPosition().z).getBlockType() != BlockType.AIR)
                        ? cnx.getBlock(CHUNK_WIDTH - 1, (int) block.getPosition().y, (int) block.getPosition().z) : null;

        Block bpy = (chunk.hasBlock((int) block.getPosition().x, (int) block.getPosition().y + 1, (int) block.getPosition().z) &&
                    chunk.getBlock((int) block.getPosition().x, (int) block.getPosition().y + 1, (int) block.getPosition().z).getBlockType() != BlockType.AIR )
                        ? chunk.getBlock((int) block.getPosition().x, (int) block.getPosition().y + 1, (int) block.getPosition().z) : null;

        Block bny = (chunk.hasBlock((int) block.getPosition().x, (int) block.getPosition().y - 1, (int) block.getPosition().z) &&
                chunk.getBlock((int) block.getPosition().x, (int) block.getPosition().y - 1, (int) block.getPosition().z).getBlockType() != BlockType.AIR )
                        ? chunk.getBlock((int) block.getPosition().x, (int) block.getPosition().y - 1, (int) block.getPosition().z) : null;


        faceBits |= bpz == null ? CHUNK_POS_Z : 0;
        faceBits |= bnz == null ? CHUNK_NEG_Z : 0;
        faceBits |= bpx == null ? CHUNK_POS_X : 0;
        faceBits |= bnx == null ? CHUNK_NEG_X : 0;
        faceBits |= bpy == null ? CHUNK_POS_Y : 0;
        faceBits |= bny == null ? CHUNK_NEG_Y : 0;

        BlockBuilder.defineFaces(block, faceBits, chunk.getOrigin());
        chunk.getVertices().addAll(block.getVertices());
    }

    public static BlockType getBlockType(int x, int y, int z) {
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

}
