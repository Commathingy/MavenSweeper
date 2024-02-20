package org.example;

public class BoardCoord {
    private final int i;
    private final int j;

    /**
     * Converts this coordinate into the index in a list which corresponds to
     * this coordinate for a board with the given width
     * @param width The width of the board
     * @return The index in a 1D list that corresponds to this coordinate
     */
    public int toIndex(int width){
        return i + j * width;
    }

    public BoardCoord addOffset(int i, int j){
        return new BoardCoord(this.i + i, this.j + j);
    }

    public boolean isValid(int width, int height){
        return i>=0 && i<width && j>=0 && j<height;
    }

    /**
     * @param i The horizontal position on the board
     * @param j The vertical position on the board
     */
    private BoardCoord(int i, int j){
        this.i = i;
        this.j = j;
    }

    /**
     * @param i The horizontal position on the board
     * @param j The vertical position on the board
     */
    public static BoardCoord fromCoord(int i, int j){
        return new BoardCoord(i,j);
    }

    /**
     * Converts from an index for a list to a 2D coordinate
     * @param index The index in the list for the tile
     * @param width The width of the board this coordinate corresponds to
     * @return The coordinate corresponding to the given index
     */
    public static BoardCoord fromIndex(int index, int width){
        int i = index % width;
        int j = index / width;
        return new BoardCoord(i,j);
    }
}
