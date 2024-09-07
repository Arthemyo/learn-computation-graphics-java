package game.test.com.game.voxel.model.builders;

import game.test.com.game.voxel.engine.Shader;
import game.test.com.game.voxel.model.enums.BlockType;
import game.test.com.game.voxel.model.enums.FaceType;
import game.test.com.game.voxel.model.intefaces.Model;
import game.test.com.game.voxel.model.mesh.Mesh;
import game.test.com.game.voxel.model.mesh.Texture;
import game.test.com.game.voxel.model.mesh.Vertex;
import game.test.com.game.voxel.model.shapes.Block;
import game.test.com.game.voxel.model.shapes.Chunk;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChunkMesh implements Model {

    private final Chunk chunk;
    private final BlockType[][][] blockTypes;
    private final List<Vertex> vertices;
    private final List<Texture> textures;
    private Mesh mesh;

    public final int[] indicesFace = {
            0, 1, 3, 3, 1, 2
    };

    public ChunkMesh(Chunk chunk) {
        this.chunk = chunk;
        vertices = new ArrayList<>();
        textures = new ArrayList<>();
        this.blockTypes = new BlockType[this.chunk.getChunkWidth()][this.chunk.getChunkHeight()][this.chunk.getChunkDepth()];
    }

    public void defineBlocks() {
        for (int x = 0; x < chunk.getChunkWidth(); x++) {
            for (int y = 0; y < chunk.getChunkHeight(); y++) {
                for (int z = 0; z < chunk.getChunkDepth(); z++) {
                    if (getBlock(x, y, z) != BlockType.AIR) {
                        blockTypes[x][y][z] = getBlock(x, y, z);
                        Block block = new Block(getBlock(x, y, z));
                        block.setPosition(x, y, z);
                        chunk.getBlocks().add(block);
                    }
                }
            }
        }

        chunk.getBlocks().forEach(block -> {
            defineFaceMesh(block);
            block.defineMesh();
            vertices.addAll(block.getVertices());
            textures.addAll(block.getTextures());
        });

        List<Integer> indicesVertices = new ArrayList<>();
        int maxValueIni = 3;

        for (int k : indicesFace) {
            indicesVertices.add(k);
        }

        for(int j = 0; j < (vertices.size() / 4 - 1); j++){
            for(int i = 0; i < indicesFace.length; i++){
                indicesVertices.add(indicesFace[i] + maxValueIni + 1);
                indicesFace[i] = indicesFace[i] + maxValueIni + 1;
            }
        }

        int[] indices = indicesVertices.stream().mapToInt(Integer::intValue).toArray();

        mesh = new Mesh(vertices, textures, indices);
    }

    public BlockType getBlock(int x, int y, int z) {
        float frequence = 0.1f;
        float amplitude = 10;

        float xOffSet = (float) (Math.sin(x * frequence) * amplitude);
        float zOffSet = (float) (Math.sin(z * frequence) * amplitude);

        float surfaceY = 16 + xOffSet + zOffSet;

        return (y > surfaceY) ? BlockType.AIR : BlockType.STONE;
    }

    private boolean hasBlock(int x, int y, int z) {
        try {
            return this.blockTypes[x][y][z] != null;
        } catch (Exception e) {
            return false;
        }
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
        Matrix4f model = new Matrix4f().identity();
        shader.setMat4("model", model);
        mesh.draw(shader);
    }

    public void clearUp() {
        this.chunk.getBlocks().forEach(Block::clear);
        this.chunk.getBlocks().clear();
    }
}