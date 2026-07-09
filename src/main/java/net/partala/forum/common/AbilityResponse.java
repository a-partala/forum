package net.partala.forum.common;

public final class AbilityResponse {
    private static final AbilityResponse CAN = new AbilityResponse(true, "");
    public final boolean result;
    public final String reason;

    private AbilityResponse(boolean result, String reason) {
        this.result = result;
        this.reason = reason;
    }

    public void throwIfCannot() {
        if(!result) {
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
