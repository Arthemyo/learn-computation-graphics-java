package game.test.com.game.voxel.model.shapes;

import game.test.com.game.voxel.model.enums.BlockType;
import game.test.com.game.voxel.model.mesh.Texture;
import game.test.com.game.voxel.model.mesh.Vertex;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class Block {

    private static final float spriteWidth = 16.0f;  // The width of a sprite in the atlas
    private static final float spriteHeight = 16.0f; // The height of a sprite in the atlas

    // Assuming the texture size is the total width/height of the atlas.
    private static final float atlasWidth = 128.0f; // The total width of the atlas (e.g., 4 * 64)
    private static final float atlasHeight = 128.0f; // The total height of the atlas (e.g., 4 * 64)

    private final List<Texture> textures;
    private final BlockType blockType;
    private final Vector3f position;
    private final List<Vertex> vertices;

    private final boolean isCullSelf;

    private int facesBits;

    private static final int[] indices = {
            0, 3, 2, 2, 1, 0, // front
            4, 7, 6, 6, 5, 4, // back

            8, 11, 10, 10, 9, 8, // right
            12, 15, 14, 14, 13, 12, // left

            16, 19, 18, 18, 17, 16, // top
            20, 23, 22, 22, 21, 20 // bottom
    };

    public Block(BlockType blockType) {
        this.blockType = blockType;
        this.isCullSelf = blockType == BlockType.AIR;
        this.facesBits = 0;
        this.position = new Vector3f();
        this.textures = new ArrayList<>();
        this.vertices = new ArrayList<>();
    }

    public void defineFaces(int facesBits, Vector3i origin) {
        try {
            if (!isCullSelf) {
                if ((facesBits & Chunk.CHUNK_POS_Z) == 1) {
                    // Coordinates of the sprite in the atlas (row, column)
                    int spriteRow = 0;
                    int spriteCol = this.blockType == BlockType.GRASS ? 0 : this.blockType == BlockType.DIRT ? 1 : this.blockType == BlockType.STONE ? 3 : 0;
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

                    vertice0.setPosition(vertice0.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));
                    vertice1.setPosition(vertice1.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));
                    vertice2.setPosition(vertice2.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));
                    vertice3.setPosition(vertice3.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));

                    this.vertices.add(vertice0);
                    this.vertices.add(vertice1);
                    this.vertices.add(vertice2);
                    this.vertices.add(vertice3);
                }

                if ((facesBits & Chunk.CHUNK_NEG_Z) == 2) {
                    // Coordinates of the sprite in the atlas (row, column)
                    int spriteRow = 0;
                    int spriteCol = this.blockType == BlockType.GRASS ? 0 : this.blockType == BlockType.DIRT ? 1 : this.blockType == BlockType.STONE ? 3 : 0;
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

                    vertice0.setPosition(vertice0.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));
                    vertice1.setPosition(vertice1.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));
                    vertice2.setPosition(vertice2.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));
                    vertice3.setPosition(vertice3.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));

                    this.vertices.add(vertice0);
                    this.vertices.add(vertice1);
                    this.vertices.add(vertice2);
                    this.vertices.add(vertice3);
                }

                if ((facesBits & Chunk.CHUNK_POS_X) == 4) {
                    int spriteRow = 0;
                    int spriteCol = this.blockType == BlockType.GRASS ? 0 : this.blockType == BlockType.DIRT ? 1 : this.blockType == BlockType.STONE ? 3 : 0;
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

                    vertice0.setPosition(vertice0.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));
                    vertice1.setPosition(vertice1.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));
                    vertice2.setPosition(vertice2.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));
                    vertice3.setPosition(vertice3.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));

                    this.vertices.add(vertice0);
                    this.vertices.add(vertice1);
                    this.vertices.add(vertice2);
                    this.vertices.add(vertice3);
                }

                if ((facesBits & Chunk.CHUNK_NEG_X) == 8) {
                    int spriteRow = 0;
                    int spriteCol = this.blockType == BlockType.GRASS ? 0 : this.blockType == BlockType.DIRT ? 1 : this.blockType == BlockType.STONE ? 3 : 0;
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

                    vertice0.setPosition(vertice0.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));
                    vertice1.setPosition(vertice1.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));
                    vertice2.setPosition(vertice2.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));
                    vertice3.setPosition(vertice3.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));

                    this.vertices.add(vertice0);
                    this.vertices.add(vertice1);
                    this.vertices.add(vertice2);
                    this.vertices.add(vertice3);
                }

                if ((facesBits & Chunk.CHUNK_POS_Y) == 16) {
                    int spriteRow = 0;
                    int spriteCol = this.blockType == BlockType.GRASS ? 2 : this.blockType == BlockType.DIRT ? 1 : this.blockType == BlockType.STONE ? 3 : 0;

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

                    vertice0.setPosition(vertice0.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));
                    vertice1.setPosition(vertice1.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));
                    vertice2.setPosition(vertice2.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));
                    vertice3.setPosition(vertice3.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));

                    this.vertices.add(vertice0);
                    this.vertices.add(vertice1);
                    this.vertices.add(vertice2);
                    this.vertices.add(vertice3);
                }

                if ((facesBits & Chunk.CHUNK_NEG_Y) == 32) {
                    int spriteRow = 0;
                    int spriteCol = this.blockType == BlockType.GRASS ? 1 : this.blockType == BlockType.DIRT ? 1 : this.blockType == BlockType.STONE ? 3 : 0;
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

                    vertice0.setPosition(vertice0.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));
                    vertice1.setPosition(vertice1.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));
                    vertice2.setPosition(vertice2.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));
                    vertice3.setPosition(vertice3.getPosition().add(this.position.x + origin.x, this.position.y + origin.y, this.position.z + origin.z));

                    this.vertices.add(vertice0);
                    this.vertices.add(vertice1);
                    this.vertices.add(vertice2);
                    this.vertices.add(vertice3);
                }

                setFacesBits(facesBits);
            }
        } catch (Exception e) {
            throw new RuntimeException("Draw Cube Failure: " + e.getMessage());
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(int x, int y, int z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public List<Texture> getTextures() {
        return textures;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public BlockType getBlockType() {
        return blockType;
    }

    public void setFacesBits(int facesBits) {
        this.facesBits = facesBits;
    }
}
