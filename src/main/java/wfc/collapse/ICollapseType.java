package wfc.collapse;

import wfc.WaveFunctionCollapse;

public interface ICollapseType<T extends ICollapseOption> {
    default int optionsCount() {
        return options().length;
    }

    default void loadContent(WaveFunctionCollapse<?> applet) {
    }
    
    T[] options();
}
