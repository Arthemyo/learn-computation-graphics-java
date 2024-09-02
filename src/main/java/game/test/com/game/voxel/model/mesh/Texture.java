package game.test.com.game.voxel.model.mesh;

public class Texture {
    private final Integer id;
    private final String type;

    public Texture(Integer id, String type){
        this.id = id;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
