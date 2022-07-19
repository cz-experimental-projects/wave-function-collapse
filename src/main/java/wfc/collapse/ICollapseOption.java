package wfc.collapse;

import processing.core.PApplet;
import wfc.obj.Pos;
import wfc.obj.Tile;

import java.util.List;

public interface ICollapseOption {
    void render(PApplet applet, float x, float y, int dimension);

    List<Pos> reduceEntropyOfAffectedTiles(int collapsedTileX, int collapsedTileY, Tile<?>[] grid);
}
