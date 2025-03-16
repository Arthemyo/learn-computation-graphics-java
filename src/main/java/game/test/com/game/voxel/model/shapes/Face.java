package game.test.com.game.voxel.model.shapes;

import game.test.com.game.voxel.model.mesh.Vertex;
import game.test.com.game.voxel.model.enums.FaceType;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.BitSet;

public class Face {
    private final int facesBits;

    private Vertex vertice0;
    private Vertex vertice1;
    private Vertex vertice2;
    private Vertex vertice3;

    public Face(int faceBits){
        this.facesBits = faceBits;
        this.defineFace();
    }

    public void defineFace() {
        if ((this.facesBits & Chunk.CHUNK_POS_Z) == 1) {
            vertice0 = new Vertex(new Vector3f(0.0f, 0.0f, 0.0f),
                    new Vector3f(0.0f, 0.0f, 1.0f),
                    new Vector2f(0.0f, 0.0f));

            vertice1 = new Vertex(new Vector3f(0.0f, 1.0f, 0.0f),
                    new Vector3f(0.0f, 0.0f, 1.0f),
                    new Vector2f(0.0f, 1.0f));

            vertice2 = new Vertex(new Vector3f(1.0f, 1.0f, 0.0f),
                    new Vector3f(0.0f, 0.0f, 1.0f),
                    new Vector2f(1.0f, 1.0f));

            vertice3 = new Vertex(new Vector3f(1.0f, 0.0f, 0.0f),
                    new Vector3f(0.0f, 0.0f, 1.0f),
                    new Vector2f(1.0f, 0.0f));
        }

        if ((this.facesBits & Chunk.CHUNK_NEG_Z) == 2) {
            vertice0 = new Vertex(new Vector3f(1.0f, 0.0f, -1.0f),
                    new Vector3f(0.0f, 0.0f, -1.0f),
                    new Vector2f(0.0f, 0.0f));

            vertice1 = new Vertex(new Vector3f(1.0f, 1.0f, -1.0f),
                    new Vector3f(0.0f, 0.0f, -1.0f),
                    new Vector2f(0.0f, 1.0f));

            vertice2 = new Vertex(new Vector3f(0.0f, 1.0f, -1.0f),
                    new Vector3f(0.0f, 0.0f, -1.0f),
                    new Vector2f(1.0f, 1.0f));

            vertice3 = new Vertex(new Vector3f(0.0f, 0.0f, -1.0f),
                    new Vector3f(0.0f, 0.0f, -1.0f),
                    new Vector2f(1.0f, 0.0f));
        }

        if ((this.facesBits & Chunk.CHUNK_POS_X) == 4) {
            vertice0 = new Vertex(new Vector3f(1.0f, 0.0f, 0.0f),
                    new Vector3f(1.0f, 0.0f, 0.0f),
                    new Vector2f(0.0f, 0.0f));

            vertice1 = new Vertex(new Vector3f(1.0f, 1.0f, 0.0f),
                    new Vector3f(1.0f, 0.0f, 0.0f),
                    new Vector2f(0.0f, 1.0f));

            vertice2 = new Vertex(new Vector3f(1.0f, 1.0f, -1.0f),
                    new Vector3f(1.0f, 0.0f, 0.0f),
                    new Vector2f(1.0f, 1.0f));

            vertice3 = new Vertex(new Vector3f(1f, 0.0f, -1.0f),
                    new Vector3f(1.0f, 0.0f, 0.0f),
                    new Vector2f(1.0f, 0.0f));
        }

        if ((this.facesBits & Chunk.CHUNK_NEG_X) == 8) {
            vertice0 = new Vertex(new Vector3f(0.0f, 0.0f, -1.0f),
                    new Vector3f(-1.0f, 0.0f, 0.0f),
                    new Vector2f(0.0f, 0.0f));

            vertice1 = new Vertex(new Vector3f(0.0f, 1.0f, -1.0f),
                    new Vector3f(-1.0f, 0.0f, 0.0f),
                    new Vector2f(0.0f, 1.0f));

            vertice2 = new Vertex(new Vector3f(0.0f, 1.0f, 0.0f),
                    new Vector3f(-1.0f, 0.0f, 0.0f),
                    new Vector2f(1.0f, 1.0f));

            vertice3 = new Vertex(new Vector3f(0.0f, 0.0f, 0.0f),
                    new Vector3f(-1.0f, 0.0f, 0.0f),
                    new Vector2f(1.0f, 0.0f));
        }

        if ((this.facesBits & Chunk.CHUNK_POS_Y) == 16) {
            vertice0 = new Vertex(new Vector3f(0.0f, 1.0f, 0.0f),
                    new Vector3f(0.0f, 1.0f, 0.0f),
                    new Vector2f(0.0f, 0.0f));

            vertice1 = new Vertex(new Vector3f(0.0f, 1.0f, -1.0f),
                    new Vector3f(0.0f, 1.0f, 0.0f),
                    new Vector2f(0.0f, 1.0f));

            vertice2 = new Vertex(new Vector3f(1.0f, 1.0f, -1.0f),
                    new Vector3f(0.0f, 1.0f, 0.0f),
                    new Vector2f(1.0f, 1.0f));

            vertice3 = new Vertex(new Vector3f(1.0f, 1.0f, 0.0f),
                    new Vector3f(0.0f, 1.0f, 0.0f),
                    new Vector2f(1.0f, 0.0f));
        }

        if ((this.facesBits & Chunk.CHUNK_NEG_Y) == 32) {
            vertice0 = new Vertex(new Vector3f(0.0f, 0.0f, -1.0f),
                    new Vector3f(0.0f, -1.0f, 0.0f),
                    new Vector2f(0.0f, 0.0f));

            vertice1 = new Vertex(new Vector3f(0.0f, 0.0f, 0.0f),
                    new Vector3f(0.0f, -1.0f, 0.0f),
                    new Vector2f(0.0f, 1.0f));

            vertice2 = new Vertex(new Vector3f(1.0f, 0.0f, 0.0f),
                    new Vector3f(0.0f, -1.0f, 0.0f),
                    new Vector2f(1.0f, 1.0f));

            vertice3 = new Vertex(new Vector3f(1.0f, 0.0f, -1.0f),
                    new Vector3f(0.0f, -1.0f, 0.0f),
                    new Vector2f(1.0f, 0.0f));
        }
    }

    public Vertex getVertice0() {
        return vertice0;
    }

    public Vertex getVertice1() {
        return vertice1;
    }

    public Vertex getVertice2() {
        return vertice2;
    }

    public Vertex getVertice3() {
        return vertice3;
    }
}
