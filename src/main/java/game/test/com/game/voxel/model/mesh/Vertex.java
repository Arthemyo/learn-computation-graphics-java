package game.test.com.game.voxel.model.mesh;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Vertex {
    private Vector3f position;
    private final Vector3f normal;
    private final Vector2f textureCoords;
    private final int index;

    private static int indexClass = 0;

    public Vertex(Vector3f position, Vector3f normal, Vector2f textCoords){
        indexClass++;
        this.position = position;
        this.normal = normal;
        this.textureCoords = textCoords;
        this.index = indexClass - 1;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public Vector2f getTextureCoords() {
        return textureCoords;
    }

    public int getIndex() {
        return index;
    }
}
