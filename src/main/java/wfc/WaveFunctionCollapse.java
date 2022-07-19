package wfc;

import processing.core.PApplet;
import processing.core.PImage;
import processing.event.KeyEvent;
import wfc.collapse.ICollapseOption;
import wfc.collapse.ICollapseType;
import wfc.obj.Pos;
import wfc.obj.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WaveFunctionCollapse<T extends ICollapseOption> extends PApplet {

    private static final int tileSize = 32;
    private static final int screenWidth = 1280;
    private static final int screenHeight = 720;
    private static final int dimensionX = 9;
    private static final int dimensionY = 9;
    
    private final ICollapseType<T> collapseType;
    private final Random random;
    
    private int updateDelay = 1;
    private int elapsed;
    private boolean next;
    private boolean paused = true;
    private boolean showDebugger = true;
    
    private Tile<T>[] grid;

    public WaveFunctionCollapse(ICollapseType<T> collapseType) {
        this.collapseType = collapseType;
        this.random = new Random();
    }
    
    @Override
    public void setup() {
        surface.setSize(screenWidth, screenHeight);
        collapseType.loadContent(this);
        grid = createGrid((x, y) -> new Tile<>(collapseType, x, y));
    }

    private void backTrack(int x, int y) {
        System.out.println("Require back tracking at " + new Pos(x, y));
        paused = true;
    }

    @Override
    public void draw() {
        background(255);
        
        if (next || (elapsed % updateDelay == 0 && !paused)) {
            List<Tile<T>> toPickTiles = new ArrayList<>(List.of(grid));
            toPickTiles = toPickTiles.stream().filter(a -> !a.isCollapsed()).collect(Collectors.toList());

            if (!toPickTiles.isEmpty()) {
                toPickTiles.sort(Comparator.comparingInt(a -> a.getOptions().size()));
                int lowestEntropy = toPickTiles.get(0).getOptions().size();
                toPickTiles = toPickTiles.stream().filter(a -> a.getOptions().size() == lowestEntropy).collect(Collectors.toList());
                Tile<T> pickedTile = getRandomElement(toPickTiles);
                List<T> options = pickedTile.getOptions();
                
                int x = pickedTile.getX();
                int y = pickedTile.getY();
                
                if (options.isEmpty()) {
                    backTrack(x, y);
                    return;
                }
                
                T pickedOption = getRandomElement(options);
                pickedTile.collapse(pickedOption);

                for (Pos p : pickedOption.reduceEntropyOfAffectedTiles(x, y, grid)) {
                    grid[posToIndex(p)].getInfluencedBy().add(new Pos(x, y));
                }
            }
            
            next = false;
        }
        elapsed++;

        render();
        renderDebugger();
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKey() == ' ') {
            paused = !paused;
        }

        if (event.getKey() == '\t') {
            next = true;
        }
        
        if (event.getKey() == 'h') {
            showDebugger = !showDebugger;
        }

        if (event.getKey() == 'r') {
            grid = createGrid((x, y) -> new Tile<>(collapseType, x, y));
        }
    }

    private void render() {
        forEach(tile -> {
            int x = tile.getX() * tileSize;
            int y = tile.getY() * tileSize;
            if (tile.isCollapsed()) {
                tile.getCollapsedTo().render(this, x, y, tileSize);
            } else {
                fill(255);
                stroke(0);
                rect(x, y, tileSize - 1, tileSize - 1);
            }
        });
    }
    
    private void renderDebugger() {
        if (!showDebugger) return;
        
        int x = mouseX / tileSize;
        int y = mouseY / tileSize;
        
        if (!valid(x, y)) {
            return;
        }
        
        int index = posToIndex(x, y);
        if (index < 0 || index >= dimensionX * dimensionY) return;
        
        Tile<T> tile = grid[index];
        
        float rx = mouseX + 10;
        float ry = mouseY + 10;
        
        fill(40, 40, 40, 200);
        noStroke();
        rect(rx, ry, 300, 150, 10);
        
        fill(255, 255, 255, 255);
        text("Pos: " + tile.getX() + ", " + tile.getY(), rx + 10, ry + 15);
        text("Collapsed: " + tile.isCollapsed(), rx + 10, ry + 30);
        text("Entropy: " + tile.getOptions().size(), rx + 10, ry + 45);
        text("Options: " + tile.getOptions(), rx + 10, ry + 60, 290, 40);
        text("Influenced by: " + tile.getInfluencedBy(), rx + 10, ry + 90, 290, 130);
    }
    
    
    
    
    
    
    
    
    
    
    @SuppressWarnings("unchecked")
    private Tile<T>[] createGrid(BiFunction<Integer, Integer, Tile<T>> factory) {
        Tile<T>[] g = (Tile<T>[]) new Tile<?>[dimensionX * dimensionY];
        for (int i = 0; i < g.length; i++) {
            Pos pos = indexToPos(i);
            g[i] = factory.apply(pos.x(), pos.y());
        }
        return g;
    }
    
    private void forEach(Consumer<Tile<T>> action) {
        for (int i = 0; i < dimensionX * dimensionY; i++) {
            action.accept(grid[i]);
        }
    }
    
    public static Pos indexToPos(int index) {
        return new Pos(index % dimensionX, index / dimensionX);
    }
    
    public static int posToIndex(int x, int y) {
        return x + y * dimensionX;
    }
    
    public static int posToIndex(Pos pos) {
        return posToIndex(pos.x(), pos.y());
    }
    
    private static boolean valid(int x, int y) {
        return x >= 0 && x < dimensionX && y >= 0 && y < dimensionY;
    }
    
    private <T1> T1 getRandomElement(T1[] arr) {
        return arr[random.nextInt(arr.length)];
    }

    private <T1> T1 getRandomElement(List<T1> arr) {
        return arr.get(random.nextInt(0, arr.size()));
    }
    
    public PImage loadPNGImage(InputStream stream) {
        byte[] bytes = loadBytes(stream);
        if (bytes == null) {
            return null;
        }

        Image awtImage = (new ImageIcon(bytes)).getImage();
        if (awtImage instanceof BufferedImage buffImage) {
            int space = buffImage.getColorModel().getColorSpace().getType();
            if (space == 9) {
                throw new IllegalStateException("Error loading image");
            }
        }

        PImage image = new PImage(awtImage);
        if (image.width == -1) {
            throw new IllegalStateException("Error loading image");
        }
        
        try {
            Method method = PImage.class.getDeclaredMethod("checkAlpha");
            method.setAccessible(true);
            method.invoke(image);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        image.parent = this;
        return image;
    }
}
