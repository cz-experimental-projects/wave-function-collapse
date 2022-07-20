import processing.core.PApplet;
import wfc.process.WaveFunctionCollapse;
import wfc.collapse.standard.StandardCollapseType;

public class Main {
    public static void main(String[] args) {
        PApplet.runSketch(new String[]{"wfc.process.WaveFunctionCollapse"}, new WaveFunctionCollapse<>(new StandardCollapseType()));
    }
}