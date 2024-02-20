import org.example.BoardCoord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CoordTest {
    @Test
    public void testIsValid(){
        BoardCoord valid = BoardCoord.fromCoord(2,5);
        BoardCoord invalid2 = BoardCoord.fromCoord(-1,5);
        Assertions.assertTrue(valid.isValid(6, 6), "position should be valid");
        Assertions.assertFalse(valid.isValid(6, 5), "position should be invalid");
        Assertions.assertFalse(invalid2.isValid(6, 6), "position should be invalid");
    }

    @Test
    public void testToFromIndex(){
        BoardCoord coord = BoardCoord.fromIndex(15, 4);
        Assertions.assertEquals(15, coord.toIndex(4), "index should not change");
    }

    @Test
    public void testFromCoord(){
        BoardCoord coord = BoardCoord.fromCoord(5, 2);
        Assertions.assertEquals(17, coord.toIndex(6), "coordinate to index conversion should give expected value");
    }

    @Test
    public void testAddOffset(){
        BoardCoord coord = BoardCoord.fromCoord(5, 2);
        Assertions.assertEquals(10, coord.addOffset(-1,-1).toIndex(6), "coordinate to index conversion should give expected value");
    }
}
