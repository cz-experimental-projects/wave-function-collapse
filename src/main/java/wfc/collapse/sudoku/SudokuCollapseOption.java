package wfc.collapse.sudoku;

import processing.core.PApplet;
import wfc.process.WaveFunctionCollapse;
import wfc.collapse.ICollapseOption;
import wfc.obj.Pos;
import wfc.obj.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum SudokuCollapseOption implements ICollapseOption {
    I(1),
    II(2),
    III(3),
    IV(4),
    V(5),
    VI(6),
    VII(7),
    VIII(8),
    IX(9);

    private final int numericValue;

    SudokuCollapseOption(int numericValue) {
        this.numericValue = numericValue;
    }

    @Override
    public void render(PApplet applet, float x, float y, int dimension) {
        float off = dimension * 0.4f;
        applet.text(numericValue, x + off, y + off * 1.5f);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pos> reduceEntropyOfAffectedTiles(int collapsedTileX, int collapsedTileY, Tile<?>[] grid) {
        List<Pos> pos = new ArrayList<>();
        
        // this contains all tiles in the district (the 3x3 areas) and in the same column and rows
        Tile<SudokuCollapseOption>[] relatedTiles = getRelatedTiles((Tile<SudokuCollapseOption>[]) grid, collapsedTileX, collapsedTileY);
        
        for (Tile<SudokuCollapseOption> tile : relatedTiles) {
            if (tile.isCollapsed()) {
                continue;
            }
            
            if (tile.getX() == collapsedTileX && tile.getY() == collapsedTileX) {
                continue;
            }

            int currTileX = tile.getX();
            int currTileY = tile.getY();
            
            pos.add(new Pos(currTileX, currTileY));

            List<SudokuCollapseOption> options = tile.getOptions();
            // get the related tiles to the currently examining tile, and remove all the options that have already been collapsed to.
            List<SudokuCollapseOption> noLongerValidOptions = Arrays.stream(getRelatedTiles((Tile<SudokuCollapseOption>[]) grid, currTileX, currTileY)).filter(Tile::isCollapsed).map(Tile::getCollapsedTo).toList();
            options.removeAll(noLongerValidOptions);
        }
        
        return pos;
    }
    
    

    @SuppressWarnings("unchecked")
    private Tile<SudokuCollapseOption>[] getRelatedTiles(Tile<SudokuCollapseOption>[] grid, int x, int y) {
        
        Pos district = getDistrict(x, y);
        int centerPieceX = district.x() * 3 + 1;
        int centerPieceY = district.y() * 3 + 1;

        List<Tile<SudokuCollapseOption>> tiles = new ArrayList<>();
        
        for (int i = -1; i < 1; i++) {
            for (int j = -1; j < 1; j++) {
                tiles.add(grid[WaveFunctionCollapse.posToIndex(centerPieceX + i, centerPieceY + j)]);
            }
        }
        
        for (int i = 0; i < 9; i++) {
            Tile<SudokuCollapseOption> row = grid[WaveFunctionCollapse.posToIndex(i, y)];
            Tile<SudokuCollapseOption> col = grid[WaveFunctionCollapse.posToIndex(x, i)];
            
            if (!tiles.contains(row)) {
                tiles.add(row);
            }
            
            if (!tiles.contains(col)) {
                tiles.add(col);
            }
        }
        
        return tiles.toArray(new Tile[0]);
    }
    
    private Pos getDistrict(int x, int y) {
        return new Pos(x / 3, y / 3);
    }
}