import org.example.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class TileTest {

    @Test
    public void ConstructorTest(){
        Tile tile = new Tile(false);
        Assertions.assertTrue(tile.is_hidden, "tile should start hidden");
        Assertions.assertFalse(tile.hit, "tile should start not hit");
        Assertions.assertFalse(tile.is_held, "tile should start unheld");
        Assertions.assertFalse(tile.is_mine, "tile should not be a mine");
        Assertions.assertTrue(tile.mine_neighbours.isEmpty(), "tile should start with no neighbours");
    }

    @Test
    public void ResetTest(){
        Tile reset_tile = new Tile(true);
        reset_tile.is_hidden = false;
        reset_tile.is_held = true;
        reset_tile.hit = true;
        reset_tile.is_flagged = true;
        reset_tile.mine_neighbours = Optional.of(1);


        reset_tile.ResetTile();
        Assertions.assertTrue(reset_tile.is_hidden, "tile should reset to hidden");
        Assertions.assertFalse(reset_tile.hit, "tile should reset to not hit");
        Assertions.assertFalse(reset_tile.is_held, "tile should reset to be unheld");
        Assertions.assertFalse(reset_tile.is_mine, "tile should reset as not a mine");
        Assertions.assertTrue(reset_tile.mine_neighbours.isEmpty(), "tile should reset with no neighbours");
    }
}
