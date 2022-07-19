package wfc.obj;

import wfc.collapse.ICollapseOption;
import wfc.collapse.ICollapseType;

import java.util.ArrayList;
import java.util.List;

public class Tile<T extends ICollapseOption> {
    
    private final List<T> options;
    private final int x;
    private final int y;

    private boolean collapsed;
    private T collapsedTo;

    // for debugging and back tracking
    private final List<Pos> influencedBy;
        
    public Tile(ICollapseType<T> type, int x, int y) {
        collapsed = false;
        options = new ArrayList<>(List.of(type.options()));
        this.x = x;
        this.y = y;
        
        influencedBy = new ArrayList<>();
    }
    
    public boolean isCollapsed() {
        return collapsed;
    }

    public List<T> getOptions() {
        return options;
    }

    public void collapse(T option) {
        collapsed = true;
        collapsedTo = option;
        if (!options.contains(option)) {
            options.add(option);
        }
    }

    public T getCollapsedTo() {
        return collapsedTo;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<Pos> getInfluencedBy() {
        return influencedBy;
    }
}
