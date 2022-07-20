package wfc.process;

import wfc.obj.Pos;
import wfc.obj.Tile;

import java.util.ArrayList;
import java.util.List;

public class BackTrackThread extends Thread {

    private final int x;
    private final int y;
    private final Tile<?>[] grid;

    public BackTrackThread(int x, int y, Tile<?>[] grid) {
        this.x = x;
        this.y = y;
        this.grid = grid;
    }

    @Override
    public void run() {
        backTrack(x, y, new ArrayList<>());
    }

    private void backTrack(int x, int y, List<Pos> history) {
        Pos currPos = new Pos(x, y);
        System.out.println("Back tracking at " + currPos);
        history.add(currPos);

        // find the last tile that influenced this one
        Tile<?> tile = grid[WaveFunctionCollapse.posToIndex(x, y)];

        List<Pos> influences = tile.getInfluencedBy();
        Pos lastInfluencePos = influences.get(influences.size() - 1);

        try {
            int back = 2;
            
            while (history.contains(lastInfluencePos)) {
                lastInfluencePos = influences.get(influences.size() - back);
                back++;
            }
            
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("No valid back track is found for position: " + currPos + ". Originated from " + new Pos(this.x, this.y));
        }

        Tile<?> influencer = grid[WaveFunctionCollapse.posToIndex(lastInfluencePos)];

        // remove last the choice of the influencer made and make it choose a different one see if it makes a difference.
        influencer.getOptions().remove(influencer.getCollapsedTo());
        influencer.revoke();

        if (influencer.getOptions().isEmpty()) {
            influencer.setBackTracked(true);
            backTrack(influencer.getX(), influencer.getY(), history);

            List<Pos> influencersInfluences = influencer.getInfluencedBy();
            for (Pos p : influencersInfluences) {
                Tile<?> t = grid[WaveFunctionCollapse.posToIndex(p)];
                if (t.isCollapsed()) {
                    t.getCollapsedTo().reduceEntropyOfAffectedTiles(t.getX(), t.getY(), grid);
                }
            }
        }
    }
}
