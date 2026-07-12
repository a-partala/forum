package net.partala.forum.common;

public final class AbilityResponse {
    private static final AbilityResponse CAN = new AbilityResponse(true, "");
    public final boolean isAble;
    public final String reason;

    private AbilityResponse(boolean isAble, String reason) {
        this.isAble = isAble;
        this.reason = reason;
    }

    public void throwIfCannot() {
        if(!isAble) {
            throw new IllegalStateException(reason);
        }
    }

    public static AbilityResponse can() {
        return CAN;
    }

    public static AbilityResponse cannot(String reason) {
        return new AbilityResponse(false, reason);
    }
}
