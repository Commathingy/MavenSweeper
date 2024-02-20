import org.example.BoardCoord;
import org.example.MineBoard;
import org.example.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class BoardTest {
    @Test
    public void testTryAccess(){
        MineBoard board = new MineBoard(10, 15, 20);
        BoardCoord valid = BoardCoord.fromCoord(2,5);
        BoardCoord invalid = BoardCoord.fromCoord(-1,5);
        Assertions.assertTrue(board.TryAccess(valid).isPresent(), "position should be valid");
        Assertions.assertFalse(board.TryAccess(invalid).isPresent(), "position should be invalid");
    }
    @Test
    public void testGetNeighbours(){
        MineBoard board = new MineBoard(10, 15, 20);
        BoardCoord pos = BoardCoord.fromIndex(1, 10);
        Assertions.assertArrayEquals(new int[]{0,10,11,2,12}, board.getNeighbours(pos).stream().mapToInt(i->i).toArray(), "provided neighbours should be correct");
        pos = BoardCoord.fromIndex(-3, 7);
        Assertions.assertArrayEquals(new int[0], board.getNeighbours(pos).stream().mapToInt(i->i).toArray(), "there should be no neighbours");
    }

    @Test
    public void testMineNeighbours(){
        MineBoard board = new MineBoard(10, 15, 20);
        Optional<Tile> op_tile = board.TryAccess(BoardCoord.fromCoord(3,4));
        Assertions.assertTrue(op_tile.isPresent(), "Coordinate should be valid");
        Tile tile = op_tile.get();
        Assertions.assertTrue(tile.mineNeighbours() != -1, "tile should have a valid number of mine neighbours");
    }
}
