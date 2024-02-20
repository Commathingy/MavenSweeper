package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Optional;

public class MineBoard extends JComponent {
    private final ArrayList<Tile> tiles;
    public final int width;
    private final int window_width;
    public final int height;
    private final int num_mines;

    private int num_uncovered;

    /**
     * The number of un-flagged mines, equal to (num_mines - no. flagged tiles)
     */
    private int unflagged;

    /**
     * Flag indicating if the current move is the first
     */
    private boolean first_flip = true;

    /**
     * stores the buffered images of the mine and flag
     */
    private final DrawInfo draw_info;
    
    /**
     * indicates whether the rest button is currently being held
     */
    private boolean reset_held = false;

    /**
     * The coordinate of the last pressed tile, if there is one
     */
    private Optional<BoardCoord> last_held = Optional.empty();

    //0 is playing, 1 is lost, 2 is won
    private int game_state = 0;


    /**
     * Create a board with a custom size and number of mines
     */
    public MineBoard(int width, int height, int num_mines) {
        this.draw_info = new DrawInfo();
        this.num_mines = num_mines;
        this.width = width;
        this.window_width = 30*width;
        this.height = height;
        this.unflagged = num_mines;
        this.num_uncovered = 0;
        tiles = new ArrayList<>(width*height);
        for (int i = 0; i<width*height; i++){
            tiles.add(new Tile(false));
        }
        this.ResetBoard();

        //add the mouse listener
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {PressMouse(e);}
            public void mouseReleased(MouseEvent e) {ReleaseMouse(e);}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });
    }

    private void ReplaceMines(){
        //spawn mines using reservoir method
        int left_to_spawn = num_mines;
        int i=width*height;

        //place mines
        for (Tile tile : tiles){
            //chance to spawn mine tile is left_to_spawn/i
            //since i = number of tiles left to create
            //only have is_mine true if left_to_spawn is actually above 0 still
            boolean is_mine = Math.random() < ((float) left_to_spawn / (float) i) && (left_to_spawn > 0);
            left_to_spawn -= is_mine ? 1 : 0;
            tile.setMine(is_mine);
            i--;
        }

        //place numbers on all tiles
        for (int j = 0; j < width*height; j++){
            Tile tile = tiles.get(j);
            tile.setMineNeighbours(mineNeighbours(BoardCoord.fromIndex(j, width)));
        }

        if (left_to_spawn > 0) {throw new RuntimeException("Error: Failed to spawn all mines");}
    }

    /**
     * Resets the entire board, replacing the mines and resetting all relevant variables.
     */
    private void ResetBoard(){
        unflagged = num_mines;
        first_flip = true;
        game_state = 0;
        num_uncovered = 0;
        for (Tile tile : tiles){
            tile.ResetTile();
        }
        ReplaceMines();
        repaint();
    }

    private void CheckGameEnd(){
        if (game_state == 0) {
            return;
        }

        String outcome = new String[]{"You Lost", "You Won!"}[game_state-1];
        this.paintComponent(getGraphics());
        int reply = JOptionPane.showConfirmDialog(null, new Object[]{"Try again?"}, outcome, JOptionPane.YES_NO_OPTION);
        if (reply == 0) {
            //yes was pressed, so reset

            ResetBoard();
        } else {
            SwingUtilities.getWindowAncestor(this).dispose();
        }
    }

    /**
     * Converts the coordinates of mouse event to a board coordinate.
     * <p>
     * If the events position was outside the board, this will result in an invalid <code>BoardCoord</code>
     * </p>
     * @param e The event containing position info
     * @return A <code>BoardCoord</code> that corresponds to this position
     */
    private BoardCoord ScreenToTile(MouseEvent e){
        int i = (e.getX() - 25) / 30;
        int j = (e.getY() - 105) / 30;
        return BoardCoord.fromCoord(i,j);
    }

    public ArrayList<Integer> getNeighbours(BoardCoord pos){
        ArrayList<Integer> output = new ArrayList<>();
        for (int i=-1; i<2; i++){
            for (int j=-1; j<2; j++){
                if (i==0 && j==0){continue;}
                BoardCoord neighbour_coord = pos.addOffset(i, j);
                if (!neighbour_coord.isValid(width, height)) {continue;}
                output.add(neighbour_coord.toIndex(width));
            }
        }
        return output;
    }

    private void tileReleased(BoardCoord pos){
        if (game_state !=0) {
            return;
        }

        tiles.get(last_held.map(value -> value.toIndex(width)).orElse(0)).setHeld(false);
        int last_held_temp = last_held.map(value -> value.toIndex(width)).orElse(-1);
        last_held = Optional.empty();

        if (!pos.isValid(width, height)) {
            return;
        }

        //check if release is in same place as press
        if (pos.toIndex(width) == last_held_temp){
            //if so do the reveal logic
            //first check if this is first click and we should rearrange mines
            if (first_flip){
                first_flip = false;
                Tile tile = tiles.get(pos.toIndex(width));
                while (tile.isMine()){
                    ReplaceMines();
                }
            }
            //then do the cascade
            StartCascade(pos);
        }

        //this should be the only point when we could have already won
        CheckGameEnd();
    }

    private void tilePressed(BoardCoord pos, int button){
        if (game_state !=0) {
            return;
        }

        //check for left or right click
        if (button == MouseEvent.BUTTON1){
            Optional<Tile> tile = TryAccess(pos);
            if (tile.isPresent()){
                last_held = Optional.of(pos);
                tile.get().setHeld(true);
            }
        } else if (button == MouseEvent.BUTTON3 && last_held.isEmpty()){
            //we check for empty last held since m1 has precedence over m2
            Optional<Tile> op_tile = TryAccess(pos);
            //check if the tile is still hidden
            if (op_tile.isPresent() && op_tile.get().isHidden()){
                //flip the flagged status
                unflagged -= op_tile.get().flipFlag() ? 1 : -1;
            }
        }
    }

    private boolean isReset(MouseEvent e){
        return e.getY()>35 && e.getY()<65 && e.getX()<window_width/2 + 40 && e.getX()>window_width/2 + 10;
    }

    private boolean isSettings(MouseEvent e){
        return e.getY()>35 && e.getY()<65 && e.getX()<window_width + 5 && e.getX()>window_width - 25;
    }

    private void ReleaseMouse(MouseEvent e){

        //check if button 1 was released
        if (!(e.getButton() == MouseEvent.BUTTON1)){
            return;
        }

        //check for pressing reset
        if (isReset(e) && reset_held) {
            ResetBoard();
        }
        reset_held = false;

        tileReleased(ScreenToTile(e));

        repaint();
    }
    private void PressMouse(MouseEvent e){

        //in this case we pressed the reset button
        if (e.getButton() == MouseEvent.BUTTON1 && isReset(e)) {
            reset_held = true;
            return;
        }

        //check for pressing settings
        if (isSettings(e)) {
            Component container = SwingUtilities.getWindowAncestor(this);
            container.setVisible(false);
            Main.run_once((JFrame) container);
            return;
        }

        tilePressed(ScreenToTile(e), e.getButton());

        repaint();
    }

    /**
     * Get the tile from the board with the given coordinate, if it is valid
     * @param pos The coordinate of the tile desired
     * @return An optional value that contains the tile if the coordinate was valid, otherwise empty
     */
    public Optional<Tile> TryAccess(BoardCoord pos){
        if (pos.isValid(width, height)) {
            return Optional.empty();
        } else {
            return Optional.of(tiles.get(pos.toIndex(width)));
        }
    }

    public int mineNeighbours(BoardCoord pos){
        //place numbers on all tiles
        int num_mines = 0;
        for (int tile_index : getNeighbours(pos)){
            num_mines += tiles.get(tile_index).isMine() ? 1 : 0;
        }
        return num_mines;
    }


    /**
     * Start a cascade from the current tile.
     * <p>
     * If a tile is hidden, start a cascade at the given position with <code>CascadeFrom</code>
     * If the tile is already revealed and the number on it is less than or equal to the number of adjacent flags, it
     * will call <code>CascadeFrom</code> on all the adjacent tiles
     * </p>
     *
     * @param pos The position at which the cascade is beginning
     */
    private void StartCascade(BoardCoord pos){
        Optional<Tile> tile = TryAccess(pos);
        //we start a cascade if the tile is hidden
        //or if it is revealed, and the number of flags around it is equal to the number of mines around it
        if (tile.isPresent() && !tile.get().isFlag()){
            if (tile.get().isHidden()){
                CascadeFrom(pos.toIndex(width));
                return;
            }
            //calculate number of flags around
            int flag_neighbours = 0;
            for (int neighbour_index : getNeighbours(pos)){
                Tile neighbour = tiles.get(neighbour_index);
                flag_neighbours += neighbour.isFlag() ? 1 : 0;
            }
            //if there are at least as many flags as the number, cascade
            if (!(flag_neighbours >= tile.get().mineNeighbours())) {
                return;
            }
            for (int neighbour_index : getNeighbours(pos)){
                CascadeFrom(neighbour_index);
            }
        }
    }

    /**
     * Cascading reveal of tiles on the board.
     * <p>
     * If the tile is hidden and not flagged, it will be revealed,
     * and if it is a 0, it will reveal all the tiles next to it via this method.
     * </p>
     *
     * @param index The index of the tile the cascade is currently happening at
     */
    private void CascadeFrom(int index){
        Tile tile = tiles.get(index);
        if (!tile.isHidden() || tile.isFlag()){return;}

        tile.Reveal();

        //if it is a mine
        if (tile.isMine()) {
            tile.Hit();
            game_state = 1;
            //reveal all other mines or incorrectly flagged
            for (Tile other_tile : tiles){
                if (other_tile.isMine() || (other_tile.isFlag() && !other_tile.isMine())) {other_tile.Reveal();}
            }
            return;
        }

        //check for a game win
        num_uncovered += 1;
        if (num_uncovered == width*height - num_mines){
            game_state = 2;
        }

        //otherwise we are revealing a non mine tile, so we check for a 0
        if (tile.mineNeighbours() == 0) {
            //now cascade is there were no mine neighbours
            for (int neighbour_index : getNeighbours(BoardCoord.fromIndex(index, width))) {
                CascadeFrom(neighbour_index);
            }
        }
    }


    protected void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;

        //draw the shading
        DrawTools.DrawShadedRegion(0, 0, window_width + 50, height*30 + 130, 5, false, g2d);
        DrawTools.DrawShadedRegion(20, 20, window_width + 10, 65, 5, true, g2d);
        DrawTools.DrawShadedRegion(20, 100, window_width + 10, height*30 + 10, 5, true, g2d);

        g2d.translate(0, 20);
        //draw reset button
        Ellipse2D.Float reset_button = new Ellipse2D.Float((float)window_width/2 + 10, 15, 30, 30);
        g2d.setColor(Color.YELLOW);
        g2d.fill(reset_button);

        //draw settings button
        Ellipse2D.Float settings_button = new Ellipse2D.Float(window_width - 25, 15, 30, 30);
        g2d.setColor(Color.DARK_GRAY);
        g2d.fill(settings_button);

        //draw the mine counter
        g2d.setFont(new Font("SansSerif", Font.BOLD, 30));
        g2d.setColor(Color.BLACK);
        g2d.drawString(Integer.toString(unflagged), 75, 41);
        g2d.drawImage(draw_info.mine_sprite, 40, 20, null);

        //translate downwards to start drawing tiles
        g2d.translate(25, 85);
        //draw the tiles
        for (int j = 0; j<height; j++){
            for (int i = 0; i<width; i++){
                //should be present since i and j are in valid range
                tiles.get(BoardCoord.fromCoord(i, j).toIndex(width)).draw(g2d, i, j, draw_info);
            }
        }
    }
}

