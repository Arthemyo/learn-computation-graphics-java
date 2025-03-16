package game.test.com.game.voxel.model.intefaces;

import game.test.com.game.voxel.engine.Shader;

public interface Entity {
    void update();
    void draw(Shader shader);
}
