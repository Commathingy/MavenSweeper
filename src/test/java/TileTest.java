import org.example.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class TileTest {

    @Test
    public void testIsMine(){
        Tile tile = new Tile(false);
        Assertions.assertFalse(tile.isMine(), "tile should not be a mine");
        tile.setMine(true);
        Assertions.assertTrue(tile.isMine(), "tile should be a mine");
    }
    @Test
    public void testReveal(){
        Tile tile = new Tile(false);
        Assertions.assertTrue(tile.isHidden(), "tile should not be revealed yet");
        tile.Reveal();
        Assertions.assertFalse(tile.isHidden(), "tile should have been revealed");
        tile.Reveal();
        Assertions.assertFalse(tile.isHidden(), "tile should still be revealed");
    }
    @Test
    public void testFlipFlag(){
        Tile tile = new Tile(false);
        Assertions.assertTrue(tile.flipFlag(), "tile should have become flagged");
        Assertions.assertFalse(tile.flipFlag(), "tile should not have been unflagged");
        Assertions.assertFalse(tile.isFlag(), "tile should still be unflagged");
    }
    @Test
    public void testSetHeld(){
        Tile tile = new Tile(false);
        tile.setHeld(true);
        Assertions.assertTrue(tile.isHeld(), "tile should be held");
        tile.setHeld(false);
        Assertions.assertFalse(tile.isHeld(), "tile should not be held");

    }
    @Test
    public void testHit(){
        Tile tile = new Tile(false);
        Assertions.assertFalse(tile.isHit(), "tile has not been hit yet");
        tile.Hit();
        Assertions.assertTrue(tile.isHit(), "tile should have been hit");
        tile.Hit();
        Assertions.assertTrue(tile.isHit(), "tile should still be hit");
    }

    @Test
    public void testMineNeighbours(){
        Tile tile = new Tile(false);
        Assertions.assertEquals(-1, tile.mineNeighbours(), "tile should have -1 mine neighbours initially");
        tile.setMineNeighbours(5);
        Assertions.assertEquals(5, tile.mineNeighbours(), "tile should now have 5 mine neighbours");
    }


}
