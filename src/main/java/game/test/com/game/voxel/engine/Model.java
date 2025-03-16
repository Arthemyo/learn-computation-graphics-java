package game.test.com.game.voxel.engine;

import game.test.com.game.voxel.engine.Shader;
import game.test.com.game.voxel.model.mesh.Mesh;
import game.test.com.game.voxel.model.mesh.Texture;
import game.test.com.game.voxel.model.mesh.Vertex;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.assimp.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;


public class Model {
    private final List<Mesh> meshes = new ArrayList<>();
    private String directory;

    public Model(String path) {
        loadModel(path);
    }

    public void loadModel(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("Model path does not exist [" + path + "]");
        }

        directory = file.getParent();

        try (AIScene aiScene = aiImportFile(path, aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
                aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace |
                aiProcess_LimitBoneWeights | aiProcess_PreTransformVertices)) {
            if (aiScene == null) {
                throw new RuntimeException("Error loading model [modelPath: " + path + "]");
            }

            processNode(Objects.requireNonNull(aiScene.mRootNode()), aiScene);
        }
    }

    public void processNode(AINode node, AIScene scene) {
        // Processa todas as malhas do nó (se houver)
        for (int i = 0; i < node.mNumMeshes(); i++) {
            int meshIndex = Objects.requireNonNull(node.mMeshes()).get(i); // Obtenha o índice da malha
            AIMesh mesh = AIMesh.create(Objects.requireNonNull(scene.mMeshes()).get(meshIndex)); // Obtenha a malha da cena
            meshes.add(processMesh(mesh, scene));
        }

        // Faz o mesmo para os filhos deste nó
        for (int i = 0; i < node.mNumChildren(); i++) {
            AINode childNode = AINode.create(Objects.requireNonNull(node.mChildren()).get(i));
            processNode(childNode, scene);
        }
    }

    public Mesh processMesh(AIMesh mesh, AIScene scene) {
        List<Vertex> vertices = new ArrayList<>();
        List<Texture> textures = new ArrayList<>();
        int[] indices;
        List<Integer> indicesList = new ArrayList<>();
        int numFaces = mesh.mNumFaces();
        AIFace.Buffer aiFaces = mesh.mFaces();

        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indicesList.add(buffer.get());
            }
        }

        indices = indicesList.stream().mapToInt(Integer::intValue).toArray();

        for (int i = 0; i < mesh.mNumVertices(); i++) {
            // Obtendo posição do vértice
            AIVector3D vertex = mesh.mVertices().get(i);
            float xPosition = vertex.x();
            float yPosition = vertex.y();
            float zPosition = vertex.z();

            // Obtendo normais do vértice
            AIVector3D normal = mesh.mNormals().get(i);
            float xNormal = normal.x();
            float yNormal = normal.y();
            float zNormal = normal.z();

            // Obtendo coordenadas de textura (se existirem)
            Vector2f texCoords;
            if (mesh.mTextureCoords(0) != null) {
                AIVector3D texCoord = mesh.mTextureCoords(0).get(i);
                texCoords = new Vector2f(texCoord.x(), texCoord.y());
            } else {
                texCoords = new Vector2f(0.0f, 0.0f);
            }

            // Criando e adicionando o vértice à lista
            vertices.add(new Vertex(
                    new Vector3f(xPosition, yPosition, zPosition),  // Posição
                    new Vector3f(xNormal, yNormal, zNormal),        // Normal
                    texCoords                                       // Coordenadas de textura
            ));
        }

        if (mesh.mMaterialIndex() >= 0) { // Verifica se há um material associado
            AIMaterial material = AIMaterial.create(scene.mMaterials().get(mesh.mMaterialIndex()));

            // Carregar texturas difusas
            List<Texture> diffuseMaps = loadMaterialTextures(material, aiTextureType_DIFFUSE,
                    "texture_diffuse", directory);
            textures.addAll(diffuseMaps);

            // Carregar texturas especulares
            List<Texture> specularMaps = loadMaterialTextures(material, aiTextureType_SPECULAR,
                    "texture_specular", directory);
            textures.addAll(specularMaps);
        }

        return new Mesh(vertices, textures, indices);
    }

    public List<Texture> loadMaterialTextures(AIMaterial mat, int type, String typeName, String directory) {
        List<Texture> textures = new ArrayList<>();
        List<Texture> textures_loaded = new ArrayList<>();

        int textureCount = Assimp.aiGetMaterialTextureCount(mat, type);
        for (int i = 0; i < textureCount; i++) {
            AIString path = AIString.calloc();
            Assimp.aiGetMaterialTexture(mat, type, i, path, (int[]) null, null, null, null,
                    null, null);

            boolean skip = false;

            // Verifica se a textura já foi carregada anteriormente
            for (Texture loadedTexture : textures_loaded) {
                if (loadedTexture.getPath().equals(path.dataString())) {
                    textures.add(loadedTexture);
                    skip = true;
                    break;
                }
            }

            // Se a textura ainda não foi carregada, carregue-a
            if (!skip) {
                Texture texture = new Texture(textureFromFile(path.dataString(), directory), typeName,
                        path.dataString());
                textures.add(texture);
                textures_loaded.add(texture); // Adiciona à lista de texturas já carregadas
            }

            path.free(); // Libera memória alocada pelo AIString
        }

        return textures;
    }

    public int textureFromFile(String path, String directory) {
        String filename = directory + "/" + path;

        int textureID = GL11.glGenTextures(); // Gera o identificador da textura
        ByteBuffer image;
        int width, height;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            // Carregar a imagem usando STB
            image = STBImage.stbi_load(filename, w, h, comp, 0);
            if (image == null) {
                System.out.println("Texture failed to load at path: " + filename);
                return 0;
            }

            width = w.get(0);
            height = h.get(0);
            int nrComponents = comp.get(0);

            int format = 0;
            if (nrComponents == 1) {
                format = GL11.GL_RED;
            } else if (nrComponents == 3) {
                format = GL11.GL_RGB;
            } else if (nrComponents == 4) {
                format = GL11.GL_RGBA;
            }

            // Fazendo o bind da textura e configurando
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, format, width, height, 0, format, GL11.GL_UNSIGNED_BYTE, image);
            glGenerateMipmap(GL11.GL_TEXTURE_2D);

            // Configura parâmetros de textura
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

            // Libera a memória da imagem
            STBImage.stbi_image_free(image);
        }

        return textureID;
    }

    public void draw(Shader shader) {
        for (Mesh mesh : meshes) {
            mesh.draw(shader);
        }
    }

    public List<Mesh> getMeshes() {
        return meshes;
    }
}
