package game.test.com.game.voxel.model.mesh;

public class Texture {
    private final Integer id;
    private final String type;
    private final String path;

    public Texture(Integer id, String type, String path){
        this.id = id;
        this.type = type;
        this.path = path;
    }

    public Integer getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }
}