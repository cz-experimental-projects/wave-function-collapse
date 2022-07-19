import processing.core.PApplet;
import wfc.WaveFunctionCollapse;
import wfc.collapse.standard.StandardCollapseOptions;
import wfc.collapse.standard.StandardCollapseType;

public class Main {
    public static void main(String[] args) {
        PApplet.runSketch(new String[]{"wfc.WaveFunctionCollapse"}, new WaveFunctionCollapse<>(new StandardCollapseType()));
    }
}