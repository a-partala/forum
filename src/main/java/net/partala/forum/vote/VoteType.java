package net.partala.forum.vote;

public enum VoteType {
    UP((short) 1),
    DOWN((short) -1);

    public final short value;

    VoteType(short value) {
        this.value = value;
    }

    public static VoteType of(short value) {

        return switch (value) {
            case 1 -> VoteType.UP;
            case -1 -> VoteType.DOWN;
            default -> throw new IllegalArgumentException("No VoteType for \"" + value + "\"");
        };
    }
}
