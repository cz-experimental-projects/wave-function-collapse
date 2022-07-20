package wfc.obj;

import wfc.collapse.ICollapseOption;
import wfc.collapse.ICollapseType;

import java.util.ArrayList;
import java.util.List;

public class Tile<T extends ICollapseOption> {

    private final List<Pos> influencedBy;
    private final List<T> options;
    private final int x;
    private final int y;

    private boolean collapsed;
    private boolean backTracked;
    private T collapsedTo;
    
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
        backTracked = false;
        if (!options.contains(option)) {
            options.add(option);
        }
    }
    
    public void revoke() {
        collapsed = false;
        collapsedTo = null;
    }

    public boolean isBackTracked() {
        return backTracked;
    }

    public void setBackTracked(boolean backTracked) {
        this.backTracked = backTracked;
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
