package wfc.collapse.standard;

import wfc.Resources;
import wfc.WaveFunctionCollapse;
import wfc.collapse.ICollapseType;

public class StandardCollapseType implements ICollapseType<StandardCollapseOptions> {
    @Override
    public void loadContent(WaveFunctionCollapse<?> applet) {
        for (StandardCollapseOptions option : options()) {
            option.setImage(applet.loadPNGImage(Resources.getStream(option.getTextureFile())));
        }
    }

    @Override
    public StandardCollapseOptions[] options() {
        return StandardCollapseOptions.values();
    }
}
