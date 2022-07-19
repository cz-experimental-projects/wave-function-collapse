package wfc.obj;

import wfc.utils.Lazy;

public enum Direction {
    UP("DOWN"),
    LEFT("RIGHT"),
    RIGHT("LEFT"),
    DOWN("UP");

    private final Lazy<Direction> opposite;

    Direction(String opposite) {
        this.opposite = new Lazy<>(() -> valueOf(opposite));
    }

    public Direction getOpposite() {
        return opposite.getValue();
    }
}
