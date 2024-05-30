package game.test.com.game.voxel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class MainTest {

    @Test
    public void mainContextLoadsAndUnloads() throws IOException {
        try (Main main = new Main()) {
            main.init();
        }
    }

    @Test
    public void intentionallyFailingTest() {
        fail("TDD Dictates you must have a failing test before you write any new code");
    }
}