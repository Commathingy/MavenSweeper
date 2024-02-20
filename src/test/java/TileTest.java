import org.example.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class TileTest {

    @Test
    public void testIsMine(){
        Tile tile = new Tile(false);
        Assertions.assertFalse(tile.isMine(), "tile should not be a mine");
    }

}
