package game.test.com.game.voxel.model.builders;

import game.test.com.game.voxel.engine.Shader;
import game.test.com.game.voxel.model.enums.BlockType;
import game.test.com.game.voxel.model.enums.FaceType;
import game.test.com.game.voxel.model.intefaces.Model;
import game.test.com.game.voxel.model.shapes.Block;
import game.test.com.game.voxel.model.shapes.Chunk;
import org.joml.Matrix4f;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Math.max;

public class ChunkMesh implements Model {

    private final Chunk chunk;
    private final BlockType[][][] blockTypes;

    public ChunkMesh(Chunk chunk) {
        this.chunk = chunk;
        this.blockTypes = new BlockType[this.chunk.getChunkWidth()][this.chunk.getChunkHeight()][this.chunk.getChunkDepth()];
    }

    public void defineBlocks() {
        for (int x = 0; x < chunk.getChunkWidth(); x++) {
            for (int y = 0; y < chunk.getChunkHeight(); y++) {
                for (int z = 0; z < chunk.getChunkDepth(); z++) {
                    this.blockTypes[x][y][z] = getBlock(x, y, z);
                    if (getBlock(x, y, z) != BlockType.AIR) {
                        Block block = new Block(getBlock(x, y, z));
                        block.setPosition(x, y, z);
                        this.chunk.getBlocks().add(block);
                    }
                }
            }
        }

       ExecutorService executorService = Executors.newFixedThreadPool(max(1, Runtime.getRuntime().availableProcessors() / 2), r -> {
            Thread t = new Thread(r);
            t.setPriority(Thread.MIN_PRIORITY);
            t.setName("Chunk builder");
            t.setDaemon(true);
            return t;
        });

        for (Block block : this.chunk.getBlocks()) {
            this.defineFaceMesh(block);
        }

        this.chunk.getBlocks().forEach(Block::defineMesh);
    }

    public BlockType getBlock(int x, int y, int z) {
        float frequence = 0.1f;
        float amplitude = 10;

        float xOffSet = (float) (Math.sin(x * frequence) * amplitude);
        float zOffSet = (float) (Math.sin(z * frequence) * amplitude);

        float surfaceY = 16 + xOffSet + zOffSet;

        return (y > surfaceY) ? BlockType.AIR : BlockType.STONE;
    }

    public boolean hasBlock(int x, int y, int z) {
        try {
            for (Block block : this.chunk.getBlocks()) {
                if ((x == block.getPosition().x) &&
                        (y == block.getPosition().y) &&
                        (z == block.getPosition().z)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void defineFaceMesh(Block block) {
        if (!hasBlock((int) block.getPosition().x, (int) block.getPosition().y, (int) block.getPosition().z + 1)) {
            block.defineFaces(FaceType.FRONT);
        }
        if (!hasBlock((int) block.getPosition().x, (int) block.getPosition().y, (int) block.getPosition().z - 1)) {
            block.defineFaces(FaceType.BACK);
        }
        if (!hasBlock((int) block.getPosition().x + 1, (int) block.getPosition().y, (int) block.getPosition().z)) {
            block.defineFaces(FaceType.RIGHT);
        }
        if (!hasBlock((int) block.getPosition().x - 1, (int) block.getPosition().y, (int) block.getPosition().z)) {
            block.defineFaces(FaceType.LEFT);
        }
        if (!hasBlock((int) block.getPosition().x, (int) block.getPosition().y + 1, (int) block.getPosition().z)) {
            block.defineFaces(FaceType.TOP);
        }
        if (!hasBlock((int) block.getPosition().x, (int) block.getPosition().y - 1, (int) block.getPosition().z)) {
            block.defineFaces(FaceType.BOTTOM);
        }
    }

    @Override
    public void draw(Shader shader) {
        this.chunk.getBlocks().forEach(block -> {
            Matrix4f model = new Matrix4f().identity();
            shader.setMat4("model", model);
            block.draw(shader);
        });
    }

    public void clearUp() {
        this.chunk.getBlocks().forEach(Block::clear);
        this.chunk.getBlocks().clear();
    }
}