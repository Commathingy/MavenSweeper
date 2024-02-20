package org.example;

import java.awt.*;
import java.util.Optional;

public class Tile {
    private boolean hit = false;
    private boolean is_held = false;
    private boolean is_flagged = false;
    private boolean is_hidden = true;
    private boolean is_mine;
    private int mine_neighbours = -1;


    public Tile(boolean is_mine){
        this.is_mine = is_mine;
    }

    public boolean flipFlag(){
        this.is_flagged ^= true;
        return this.is_flagged;
    }

    public int mineNeighbours(){
        return this.mine_neighbours;
    }

    public void setMineNeighbours(int mines){
        this.mine_neighbours = mines;
    }

    public boolean isFlag(){
        return this.is_flagged;
    }

    public boolean isHeld(){
        return this.is_held;
    }

    public void setHeld(boolean is_held){
        this.is_held = is_held;
    }

    public boolean isHidden(){
        return this.is_hidden;
    }

    public void Reveal(){
        this.is_hidden = false;
    }

    public void Hit(){
        this.hit = true;
    }

    public boolean isHit(){
        return this.hit;
    }


    public boolean isMine(){
        return this.is_mine;
    }
    public void setMine(boolean is_mine){
        this.is_mine = is_mine;
    }

    public void ResetTile(){
           this.hit = false;
           this.is_held = false;
           this.is_flagged = false;
           this.is_hidden = true;
           this.is_mine = false;
           this.mine_neighbours = -1;
    }
    public void draw(Graphics2D g2d, int i, int j, DrawInfo draw_info){
        int position_x = i * 30;
        int position_y = j * 30;

        if (is_hidden && is_flagged){
            DrawTools.draw_flagged(g2d, position_x, position_y, draw_info.flag_sprite, false);
        } else if (is_hidden && is_held) {
            DrawTools.draw_revealed(g2d, position_x, position_y);
        } else if (is_hidden) {
            DrawTools.draw_hidden(g2d, position_x, position_y);
        } else if (is_flagged && !is_mine){ //this case should only happen at the very end when we force reveal tiles
            DrawTools.draw_flagged(g2d, position_x, position_y, draw_info.flag_sprite, true);
        } else if (is_mine) {
            DrawTools.draw_mine(g2d, position_x, position_y, draw_info.mine_sprite, hit);
        } else if (mine_neighbours>0) {
            DrawTools.draw_number(g2d, position_x, position_y, mine_neighbours);
        } else {
            DrawTools.draw_revealed(g2d, position_x, position_y);
        }
    }
}
