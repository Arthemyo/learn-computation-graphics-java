package game.test.com.game.voxel.model.mesh;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Vertex {
    private final Vector3f position;
    private final Vector3f normal;
    private final Vector2f textureCoords;

    public Vertex(Vector3f position, Vector3f normal, Vector2f textCoords){
        this.position = position;
        this.normal = normal;
        this.textureCoords = textCoords;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public Vector2f getTextureCoords() {
        return textureCoords;
    }
}