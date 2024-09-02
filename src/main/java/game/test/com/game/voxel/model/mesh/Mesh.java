package game.test.com.game.voxel.model.mesh;

import game.test.com.game.voxel.engine.Shader;
import game.test.com.game.voxel.model.intefaces.Model;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Mesh implements Model {
    private final List<Vertex> vertices;
    private final int[] indices;
    private final List<Texture> textures;

    private final int VAO, VBO, EBO;

    public Mesh(List<Vertex> vertices, List<Texture> textures, int[] indices) {
        this.vertices = vertices;
        this.indices = indices;
        this.textures = textures;

        this.VAO = glGenVertexArrays();
        this.VBO = glGenBuffers();
        this.EBO = glGenBuffers();

        this.setupMesh();
    }

    public void setupMesh() {
        try {
            FloatBuffer vec3BufferVertice = MemoryUtil.memAllocFloat(this.vertices.size() * 8);
            //Generate a buffer to save the vertex object

            for (Vertex  vertex : vertices) {
                //Get a float array of a Vector3f(Vector of three values type float);
                //Array referring to the vertex position
                FloatBuffer vec3BufferPosition = MemoryUtil.memAllocFloat(3);
                vertex.getPosition().get(vec3BufferPosition);

                //Get a float array of a Vector3f(Vector of three values type float);
                //Array referring to the vertex normal
                FloatBuffer vec3BufferNormal = MemoryUtil.memAllocFloat(3);
                vertex.getNormal().get(vec3BufferNormal);

                //Get a float array of a Vector2f(Vector of two values type float);
                //Array referring to the vertex texture coordinates
                FloatBuffer vec3BufferTextureCoords = MemoryUtil.memAllocFloat(2);
                vertex.getTextureCoords().get(vec3BufferTextureCoords);

                //Building a float array with all attributes of a vertex
                vec3BufferVertice.put(vec3BufferPosition);
                vec3BufferVertice.put(vec3BufferNormal);
                vec3BufferVertice.put(vec3BufferTextureCoords);

                vec3BufferPosition.clear();
                vec3BufferNormal.clear();
                vec3BufferTextureCoords.clear();
            }

            vec3BufferVertice.flip();

            glBindVertexArray(this.VAO);
            glBindBuffer(GL_ARRAY_BUFFER, this.VBO);

            //Binding array buffer(VBO) with our array data of vertex
            glBufferData(GL_ARRAY_BUFFER, vec3BufferVertice, GL_STATIC_DRAW);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.EBO);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

            glVertexAttribPointer(0, 3, GL_FLOAT, false,
                    8 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);

            glVertexAttribPointer(1, 3, GL_FLOAT, false,
                    8 * Float.BYTES, 3 * Float.BYTES);
            glEnableVertexAttribArray(1);

            glVertexAttribPointer(2, 2, GL_FLOAT, false,
                    8 * Float.BYTES, 6 * Float.BYTES);
            glEnableVertexAttribArray(2);

            glBindVertexArray(0);
            vec3BufferVertice.clear();

        } catch (Exception e) {
            throw new RuntimeException("Draw Cube Failure: " + e.getMessage());
        }
    }

    @Override
    public void draw(Shader shader) {

        int diffuseNr = 1;
        int specularNr = 1;

        for (int i = 0; i < textures.size(); i++) {

            String name = textures.get(i).getType();
            String number = null;

            if (shader != null) {

                GL30.glActiveTexture(GL30.GL_TEXTURE0 + i);
                glBindTexture(GL_TEXTURE_2D, textures.get(i).getId());

                if (Objects.equals(name, "texture_diffuse")) {
                    number = String.valueOf(diffuseNr++);
                }else if (Objects.equals(name, "texture_specular")){
                    number = String.valueOf(specularNr++);
                }

                shader.setInt(("material." + name + number), i);

            }
        }

        glBindVertexArray(VAO);
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void clear() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDeleteBuffers(this.VAO);
        glDeleteBuffers(this.VBO);
        glDeleteBuffers(this.EBO);
        GL30.glBindVertexArray(0);
    }

}
