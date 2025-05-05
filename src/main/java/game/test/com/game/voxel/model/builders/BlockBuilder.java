package game.test.com.game.voxel.model.builders;

import game.test.com.game.voxel.model.enums.BlockType;
import game.test.com.game.voxel.model.mesh.Texture;
import game.test.com.game.voxel.model.mesh.Vertex;
import game.test.com.game.voxel.model.shapes.Block;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class BlockBuilder {

    private static final float spriteWidth = 16.0f;  // The width of a sprite in the atlas
    private static final float spriteHeight = 16.0f; // The height of a sprite in the atlas

    // Assuming the texture size is the total width/height of the atlas.
    private static final float atlasWidth = 128.0f; // The total width of the atlas (e.g., 4 * 64)
    private static final float atlasHeight = 128.0f; // The total height of the atlas (e.g., 4 * 64)

    private static final int[] indices = {
            0, 3, 2, 2, 1, 0, // front
            4, 7, 6, 6, 5, 4, // back

            8, 11, 10, 10, 9, 8, // right
            12, 15, 14, 14, 13, 12, // left

            16, 19, 18, 18, 17, 16, // top
            20, 23, 22, 22, 21, 20 // bottom
    };

    public static void defineFaces(Block block, int facesBits, Vector3i origin) {
        try {
            if (!block.getIsCullSelf()) {
                if ((facesBits & ChunkBuilder.CHUNK_POS_Z) == 1) {
                    // Coordinates of the sprite in the atlas (row, column)
                    int spriteRow = 0;
                    int spriteCol = block.getBlockType() == BlockType.GRASS ? 0 : block.getBlockType() == BlockType.DIRT ? 1 : block.getBlockType() == BlockType.STONE ? 3 : 0;
                    float x1 = (spriteCol * spriteWidth) / atlasWidth;
                    float y1 = (spriteRow * spriteHeight) / atlasHeight;
                    float x2 = ((spriteCol + 1) * spriteWidth) / atlasWidth;
                    float y2 = ((spriteRow + 1) * spriteHeight) / atlasHeight;

                    Vertex vertice0 = new Vertex(new Vector3f(0.0f, 0.0f, 0.0f),
                            new Vector3f(0.0f, 0.0f, 1.0f),
                            new Vector2f(x1, y2));

                    Vertex vertice1 = new Vertex(new Vector3f(0.0f, 1.0f, 0.0f),
                            new Vector3f(0.0f, 0.0f, 1.0f),
                            new Vector2f(x1, y1));

                    Vertex vertice2 = new Vertex(new Vector3f(1.0f, 1.0f, 0.0f),
                            new Vector3f(0.0f, 0.0f, 1.0f),
                            new Vector2f(x2, y1));

                    Vertex vertice3 = new Vertex(new Vector3f(1.0f, 0.0f, 0.0f),
                            new Vector3f(0.0f, 0.0f, 1.0f),
                            new Vector2f(x2, y2));

                    vertice0.setPosition(vertice0.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));
                    vertice1.setPosition(vertice1.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));
                    vertice2.setPosition(vertice2.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));
                    vertice3.setPosition(vertice3.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));

                    block.getVertices().add(vertice0);
                    block.getVertices().add(vertice1);
                    block.getVertices().add(vertice2);
                    block.getVertices().add(vertice3);
                }

                if ((facesBits & ChunkBuilder.CHUNK_NEG_Z) == 2) {
                    // Coordinates of the sprite in the atlas (row, column)
                    int spriteRow = 0;
                    int spriteCol = block.getBlockType() == BlockType.GRASS ? 0 : block.getBlockType() == BlockType.DIRT ? 1 : block.getBlockType() == BlockType.STONE ? 3 : 0;
                    float x1 = (spriteCol * spriteWidth) / atlasWidth;
                    float y1 = (spriteRow * spriteHeight) / atlasHeight;
                    float x2 = ((spriteCol + 1) * spriteWidth) / atlasWidth;
                    float y2 = ((spriteRow + 1) * spriteHeight) / atlasHeight;

                    Vertex vertice0 = new Vertex(new Vector3f(1.0f, 0.0f, -1.0f),
                            new Vector3f(0.0f, 0.0f, -1.0f),
                            new Vector2f(x1, y2));

                    Vertex vertice1 = new Vertex(new Vector3f(1.0f, 1.0f, -1.0f),
                            new Vector3f(0.0f, 0.0f, -1.0f),
                            new Vector2f(x1, y1));

                    Vertex vertice2 = new Vertex(new Vector3f(0.0f, 1.0f, -1.0f),
                            new Vector3f(0.0f, 0.0f, -1.0f),
                            new Vector2f(x2, y1));

                    Vertex vertice3 = new Vertex(new Vector3f(0.0f, 0.0f, -1.0f),
                            new Vector3f(0.0f, 0.0f, -1.0f),
                            new Vector2f(x2, y2));

                    vertice0.setPosition(vertice0.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));
                    vertice1.setPosition(vertice1.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));
                    vertice2.setPosition(vertice2.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));
                    vertice3.setPosition(vertice3.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));

                    block.getVertices().add(vertice0);
                    block.getVertices().add(vertice1);
                    block.getVertices().add(vertice2);
                    block.getVertices().add(vertice3);
                }

                if ((facesBits & ChunkBuilder.CHUNK_POS_X) == 4) {
                    int spriteRow = 0;
                    int spriteCol = block.getBlockType() == BlockType.GRASS ? 0 : block.getBlockType() == BlockType.DIRT ? 1 : block.getBlockType() == BlockType.STONE ? 3 : 0;
                    float x1 = (spriteCol * spriteWidth) / atlasWidth;
                    float y1 = (spriteRow * spriteHeight) / atlasHeight;
                    float x2 = ((spriteCol + 1) * spriteWidth) / atlasWidth;
                    float y2 = ((spriteRow + 1) * spriteHeight) / atlasHeight;

                    Vertex vertice0 = new Vertex(new Vector3f(1.0f, 0.0f, 0.0f),
                            new Vector3f(1.0f, 0.0f, 0.0f),
                            new Vector2f(x1, y2));

                    Vertex vertice1 = new Vertex(new Vector3f(1.0f, 1.0f, 0.0f),
                            new Vector3f(1.0f, 0.0f, 0.0f),
                            new Vector2f(x1, y1));

                    Vertex vertice2 = new Vertex(new Vector3f(1.0f, 1.0f, -1.0f),
                            new Vector3f(1.0f, 0.0f, 0.0f),
                            new Vector2f(x2, y1));

                    Vertex vertice3 = new Vertex(new Vector3f(1f, 0.0f, -1.0f),
                            new Vector3f(1.0f, 0.0f, 0.0f),
                            new Vector2f(x2, y2));

                    vertice0.setPosition(vertice0.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));
                    vertice1.setPosition(vertice1.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));
                    vertice2.setPosition(vertice2.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));
                    vertice3.setPosition(vertice3.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));

                    block.getVertices().add(vertice0);
                    block.getVertices().add(vertice1);
                    block.getVertices().add(vertice2);
                    block.getVertices().add(vertice3);
                }

                if ((facesBits & ChunkBuilder.CHUNK_NEG_X) == 8) {
                    int spriteRow = 0;
                    int spriteCol = block.getBlockType() == BlockType.GRASS ? 0 : block.getBlockType() == BlockType.DIRT ? 1 : block.getBlockType() == BlockType.STONE ? 3 : 0;
                    float x1 = (spriteCol * spriteWidth) / atlasWidth;
                    float y1 = (spriteRow * spriteHeight) / atlasHeight;
                    float x2 = ((spriteCol + 1) * spriteWidth) / atlasWidth;
                    float y2 = ((spriteRow + 1) * spriteHeight) / atlasHeight;

                    Vertex vertice0 = new Vertex(new Vector3f(0.0f, 0.0f, -1.0f),
                            new Vector3f(-1.0f, 0.0f, 0.0f),
                            new Vector2f(x1, y2));

                    Vertex vertice1 = new Vertex(new Vector3f(0.0f, 1.0f, -1.0f),
                            new Vector3f(-1.0f, 0.0f, 0.0f),
                            new Vector2f(x1, y1));

                    Vertex vertice2 = new Vertex(new Vector3f(0.0f, 1.0f, 0.0f),
                            new Vector3f(-1.0f, 0.0f, 0.0f),
                            new Vector2f(x2, y1));

                    Vertex vertice3 = new Vertex(new Vector3f(0.0f, 0.0f, 0.0f),
                            new Vector3f(-1.0f, 0.0f, 0.0f),
                            new Vector2f(x2, y2));

                    vertice0.setPosition(vertice0.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));
                    vertice1.setPosition(vertice1.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));
                    vertice2.setPosition(vertice2.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));
                    vertice3.setPosition(vertice3.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));

                    block.getVertices().add(vertice0);
                    block.getVertices().add(vertice1);
                    block.getVertices().add(vertice2);
                    block.getVertices().add(vertice3);
                }

                if ((facesBits & ChunkBuilder.CHUNK_POS_Y) == 16) {
                    int spriteRow = 0;
                    int spriteCol = block.getBlockType() == BlockType.GRASS ? 2 : block.getBlockType() == BlockType.DIRT ? 1 : block.getBlockType() == BlockType.STONE ? 3 : 0;

                    float x1 = (spriteCol * spriteWidth) / atlasWidth;
                    float y1 = (spriteRow * spriteHeight) / atlasHeight;
                    float x2 = ((spriteCol + 1) * spriteWidth) / atlasWidth;
                    float y2 = ((spriteRow + 1) * spriteHeight) / atlasHeight;

                    Vertex vertice0 = new Vertex(new Vector3f(0.0f, 1.0f, 0.0f),
                            new Vector3f(0.0f, 1.0f, 0.0f),
                            new Vector2f(x1, y2));

                    Vertex vertice1 = new Vertex(new Vector3f(0.0f, 1.0f, -1.0f),
                            new Vector3f(0.0f, 1.0f, 0.0f),
                            new Vector2f(x1, y1));

                    Vertex vertice2 = new Vertex(new Vector3f(1.0f, 1.0f, -1.0f),
                            new Vector3f(0.0f, 1.0f, 0.0f),
                            new Vector2f(x2, y1));

                    Vertex vertice3 = new Vertex(new Vector3f(1.0f, 1.0f, 0.0f),
                            new Vector3f(0.0f, 1.0f, 0.0f),
                            new Vector2f(x2, y2));

                    vertice0.setPosition(vertice0.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));
                    vertice1.setPosition(vertice1.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));
                    vertice2.setPosition(vertice2.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));
                    vertice3.setPosition(vertice3.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));

                    block.getVertices().add(vertice0);
                    block.getVertices().add(vertice1);
                    block.getVertices().add(vertice2);
                    block.getVertices().add(vertice3);
                }

                if ((facesBits & ChunkBuilder.CHUNK_NEG_Y) == 32) {
                    int spriteRow = 0;
                    int spriteCol = block.getBlockType() == BlockType.GRASS ? 1 : block.getBlockType() == BlockType.DIRT ? 1 : block.getBlockType() == BlockType.STONE ? 3 : 0;
                    float x1 = (spriteCol * spriteWidth) / atlasWidth;
                    float y1 = (spriteRow * spriteHeight) / atlasHeight;
                    float x2 = ((spriteCol + 1) * spriteWidth) / atlasWidth;
                    float y2 = ((spriteRow + 1) * spriteHeight) / atlasHeight;

                    Vertex vertice0 = new Vertex(new Vector3f(0.0f, 0.0f, -1.0f),
                            new Vector3f(0.0f, -1.0f, 0.0f),
                            new Vector2f(x1, y2));

                    Vertex vertice1 = new Vertex(new Vector3f(0.0f, 0.0f, 0.0f),
                            new Vector3f(0.0f, -1.0f, 0.0f),
                            new Vector2f(x1, y1));

                    Vertex vertice2 = new Vertex(new Vector3f(1.0f, 0.0f, 0.0f),
                            new Vector3f(0.0f, -1.0f, 0.0f),
                            new Vector2f(x2, y1));

                    Vertex vertice3 = new Vertex(new Vector3f(1.0f, 0.0f, -1.0f),
                            new Vector3f(0.0f, -1.0f, 0.0f),
                            new Vector2f(x2, y2));

                    vertice0.setPosition(vertice0.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));
                    vertice1.setPosition(vertice1.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));
                    vertice2.setPosition(vertice2.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));
                    vertice3.setPosition(vertice3.getPosition().add(block.getPosition().x + origin.x, block.getPosition().y + origin.y, block.getPosition().z + origin.z));

                    block.getVertices().add(vertice0);
                    block.getVertices().add(vertice1);
                    block.getVertices().add(vertice2);
                    block.getVertices().add(vertice3);
                }

                block.setFacesBits(facesBits);
            }
        } catch (Exception e) {
            throw new RuntimeException("Draw Cube Failure: " + e.getMessage());
        }
    }

}
