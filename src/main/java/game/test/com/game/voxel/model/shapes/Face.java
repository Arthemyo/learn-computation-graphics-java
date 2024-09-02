package game.test.com.game.voxel.model.shapes;

import game.test.com.game.voxel.model.mesh.Vertex;
import game.test.com.game.voxel.model.enums.FaceType;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Face {
    private final FaceType faceType;

    private Vertex vertice0;
    private Vertex vertice1;
    private Vertex vertice2;
    private Vertex vertice3;

    public Face(FaceType faceType){
        this.faceType = faceType;
        this.defineFace();
    }

    public void defineFace() {
        if (this.faceType == FaceType.FRONT) {
            vertice0 = new Vertex(new Vector3f(-0.5f, 0.5f, 0.5f),
                    new Vector3f(0.0f, 0.0f, 1.0f),
                    new Vector2f(0.0f, 0.0f));

            vertice1 = new Vertex(new Vector3f(-0.5f, -0.5f, 0.5f),
                    new Vector3f(0.0f, 0.0f, 1.0f),
                    new Vector2f(0.0f, 1.0f));

            vertice2 = new Vertex(new Vector3f(0.5f, -0.5f, 0.5f),
                    new Vector3f(0.0f, 0.0f, 1.0f),
                    new Vector2f(1.0f, 1.0f));

            vertice3 = new Vertex(new Vector3f(0.5f, 0.5f, 0.5f),
                    new Vector3f(0.0f, 0.0f, 1.0f),
                    new Vector2f(1.0f, 0.0f));
        }

        if (this.faceType == FaceType.BACK) {
            vertice0 = new Vertex(new Vector3f(-0.5f, 0.5f, -0.5f),
                    new Vector3f(0.0f, 0.0f, -1.0f),
                    new Vector2f(0.0f, 0.0f));

            vertice1 = new Vertex(new Vector3f(-0.5f, -0.5f, -0.5f),
                    new Vector3f(0.0f, 0.0f, -1.0f),
                    new Vector2f(0.0f, 1.0f));

            vertice2 = new Vertex(new Vector3f(0.5f, -0.5f, -0.5f),
                    new Vector3f(0.0f, 0.0f, -1.0f),
                    new Vector2f(1.0f, 1.0f));

            vertice3 = new Vertex(new Vector3f(0.5f, 0.5f, -0.5f),
                    new Vector3f(0.0f, 0.0f, -1.0f),
                    new Vector2f(1.0f, 0.0f));
        }

        if (this.faceType == FaceType.RIGHT) {
            vertice0 = new Vertex(new Vector3f(0.5f, 0.5f, 0.5f),
                    new Vector3f(1.0f, 0.0f, 0.0f),
                    new Vector2f(0.0f, 0.0f));

            vertice1 = new Vertex(new Vector3f(0.5f, -0.5f, 0.5f),
                    new Vector3f(1.0f, 0.0f, 0.0f),
                    new Vector2f(0.0f, 1.0f));

            vertice2 = new Vertex(new Vector3f(0.5f, -0.5f, -0.5f),
                    new Vector3f(1.0f, 0.0f, 0.0f),
                    new Vector2f(1.0f, 1.0f));

            vertice3 = new Vertex(new Vector3f(0.5f, 0.5f, -0.5f),
                    new Vector3f(1.0f, 0.0f, 0.0f),
                    new Vector2f(1.0f, 0.0f));
        }

        if (this.faceType == FaceType.LEFT) {
            vertice0 = new Vertex(new Vector3f(-0.5f, 0.5f, 0.5f),
                    new Vector3f(-1.0f, 0.0f, 0.0f),
                    new Vector2f(0.0f, 0.0f));

            vertice1 = new Vertex(new Vector3f(-0.5f, -0.5f, 0.5f),
                    new Vector3f(-1.0f, 0.0f, 0.0f),
                    new Vector2f(0.0f, 1.0f));

            vertice2 = new Vertex(new Vector3f(-0.5f, -0.5f, -0.5f),
                    new Vector3f(-1.0f, 0.0f, 0.0f),
                    new Vector2f(1.0f, 1.0f));

            vertice3 = new Vertex(new Vector3f(-0.5f, 0.5f, -0.5f),
                    new Vector3f(-1.0f, 0.0f, 0.0f),
                    new Vector2f(1.0f, 0.0f));
        }

        if (this.faceType == FaceType.TOP) {
            vertice0 = new Vertex(new Vector3f(-0.5f, 0.5f, -0.5f),
                    new Vector3f(0.0f, 1.0f, 0.0f),
                    new Vector2f(0.0f, 0.0f));

            vertice1 = new Vertex(new Vector3f(-0.5f, 0.5f, 0.5f),
                    new Vector3f(0.0f, 1.0f, 0.0f),
                    new Vector2f(0.0f, 1.0f));

            vertice2 = new Vertex(new Vector3f(0.5f, 0.5f, 0.5f),
                    new Vector3f(0.0f, 1.0f, 0.0f),
                    new Vector2f(1.0f, 1.0f));

            vertice3 = new Vertex(new Vector3f(0.5f, 0.5f, -0.5f),
                    new Vector3f(0.0f, 1.0f, 0.0f),
                    new Vector2f(1.0f, 0.0f));
        }

        if (this.faceType == FaceType.BOTTOM) {
            vertice0 = new Vertex(new Vector3f(-0.5f, -0.5f, -0.5f),
                    new Vector3f(0.0f, -1.0f, 0.0f),
                    new Vector2f(0.0f, 0.0f));

            vertice1 = new Vertex(new Vector3f(-0.5f, -0.5f, 0.5f),
                    new Vector3f(0.0f, -1.0f, 0.0f),
                    new Vector2f(0.0f, 1.0f));

            vertice2 = new Vertex(new Vector3f(0.5f, -0.5f, 0.5f),
                    new Vector3f(0.0f, -1.0f, 0.0f),
                    new Vector2f(1.0f, 1.0f));

            vertice3 = new Vertex(new Vector3f(0.5f, -0.5f, -0.5f),
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
