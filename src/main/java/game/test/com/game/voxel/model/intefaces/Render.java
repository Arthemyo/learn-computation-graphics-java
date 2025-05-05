package game.test.com.game.voxel.model.intefaces;

import game.test.com.game.voxel.engine.Shader;

public interface Render {
    void update(Shader shader);
    void draw(Shader shader);
}
