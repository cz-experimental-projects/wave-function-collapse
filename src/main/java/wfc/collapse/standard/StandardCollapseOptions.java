package wfc.collapse.standard;

import processing.core.PApplet;
import processing.core.PImage;
import wfc.WaveFunctionCollapse;
import wfc.collapse.ICollapseOption;
import wfc.obj.Direction;
import wfc.obj.Pos;
import wfc.obj.Tile;

import java.util.*;
import java.util.stream.Collectors;

public enum StandardCollapseOptions implements ICollapseOption {
    BLANK("standard/blank.png", new HashMap<>() {
        {
            put(Direction.UP, ConnectionState.DISCONNECTED);
            put(Direction.LEFT, ConnectionState.DISCONNECTED);
            put(Direction.RIGHT, ConnectionState.DISCONNECTED);
            put(Direction.DOWN, ConnectionState.DISCONNECTED);
        }
    }),
    UP("standard/up.png", new HashMap<>() {
        {
            put(Direction.UP, ConnectionState.CONNECTED);
            put(Direction.LEFT, ConnectionState.CONNECTED);
            put(Direction.RIGHT, ConnectionState.CONNECTED);
            put(Direction.DOWN, ConnectionState.DISCONNECTED);
        }
    }),
    LEFT("standard/left.png", new HashMap<>() {
        {
            put(Direction.UP, ConnectionState.CONNECTED);
            put(Direction.LEFT, ConnectionState.CONNECTED);
            put(Direction.RIGHT, ConnectionState.DISCONNECTED);
            put(Direction.DOWN, ConnectionState.CONNECTED);
        }
    }),
    DOWN("standard/down.png", new HashMap<>() {
        {
            put(Direction.UP, ConnectionState.DISCONNECTED);
            put(Direction.LEFT, ConnectionState.CONNECTED);
            put(Direction.RIGHT, ConnectionState.CONNECTED);
            put(Direction.DOWN, ConnectionState.CONNECTED);
        }
    }),
    RIGHT("standard/right.png", new HashMap<>() {
        {
            put(Direction.UP, ConnectionState.CONNECTED);
            put(Direction.LEFT, ConnectionState.DISCONNECTED);
            put(Direction.RIGHT, ConnectionState.CONNECTED);
            put(Direction.DOWN, ConnectionState.CONNECTED);
        }
    });

    private final String textureFile;
    private final Map<Direction, ConnectionState> connectionState;
    private PImage image;

    StandardCollapseOptions(String textureFile, Map<Direction, ConnectionState> validNeighbors) {
        this.textureFile = textureFile;
        this.connectionState = validNeighbors;
    }

    public PImage getImage() {
        if (image == null) throw new IllegalStateException("Image was not loaded correctly!");
        return image;
    }

    public void setImage(PImage image) {
        if (this.image != null) throw new IllegalStateException("Image should not be loaded twice");
        this.image = image;
    }

    public String getTextureFile() {
        return textureFile;
    }

    @Override
    public void render(PApplet applet, float x, float y, int dimension) {
        applet.fill(255);
        applet.stroke(0);
        applet.image(getImage(), x, y, dimension, dimension);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pos> reduceEntropyOfAffectedTiles(int collapsedTileX, int collapsedTileY, Tile<?>[] grid) {
        List<Pos> pos = new ArrayList<>();

        Pos upPos = new Pos(collapsedTileX, collapsedTileY - 1);
        Pos leftPos = new Pos(collapsedTileX - 1, collapsedTileY);
        Pos rightPos = new Pos(collapsedTileX + 1, collapsedTileY);
        Pos downPos = new Pos(collapsedTileX, collapsedTileY + 1);

        int up = WaveFunctionCollapse.posToIndex(upPos);
        int left = WaveFunctionCollapse.posToIndex(leftPos);
        int right = WaveFunctionCollapse.posToIndex(rightPos);
        int down = WaveFunctionCollapse.posToIndex(downPos);

        if (reduceEntropy(up, Direction.UP, (Tile<StandardCollapseOptions>[]) grid)) {
            pos.add(upPos);
        }
        
        if (reduceEntropy(left, Direction.LEFT, (Tile<StandardCollapseOptions>[]) grid)) {
            pos.add(leftPos);
        }
        
        if (reduceEntropy(right, Direction.RIGHT, (Tile<StandardCollapseOptions>[]) grid)) {
            pos.add(rightPos);
        }

        if (reduceEntropy(down, Direction.DOWN, (Tile<StandardCollapseOptions>[]) grid)) {
            pos.add(downPos);
        }

        return pos;
    }

    private boolean reduceEntropy(int index, Direction direction, Tile<StandardCollapseOptions>[] grid) {
        if (index < 0 || index >= grid.length) {
            return false;
        }

        Tile<StandardCollapseOptions> tile = grid[index];

        if (tile.isCollapsed()) {
            return false;
        }
        
        List<StandardCollapseOptions> options = tile.getOptions();
        List<StandardCollapseOptions> newOptions = getOptions(direction);
        
        // if it already had conditions, evaluate all of them and only keep the ones that fits all conditions (overlapping elements)
        if (!options.isEmpty()) {
            List<StandardCollapseOptions> fitAllConditions = options.stream().filter(newOptions::contains).toList();
            options.clear();
            options.addAll(fitAllConditions);
            return true;
        }
        options.addAll(newOptions);
        
        return true;
    }

    private List<StandardCollapseOptions> getOptions(Direction direction) {
        ConnectionState state = connectionState.get(direction);
        return Arrays.stream(values()).filter(o -> o.connectionState.get(direction.getOpposite()) == state).collect(Collectors.toList());
    }
}
