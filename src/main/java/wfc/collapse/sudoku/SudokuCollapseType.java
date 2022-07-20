package wfc.collapse.sudoku;

import wfc.collapse.ICollapseType;

public class SudokuCollapseType implements ICollapseType<SudokuCollapseOption> {
    @Override
    public SudokuCollapseOption[] options() {
        return SudokuCollapseOption.values();
    }
}
