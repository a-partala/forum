package net.partala.forum.common;

public final class AbilityResponse {
    private static final AbilityResponse positive = new AbilityResponse(true, "");
    public final boolean result;
    public final String reason;

    private AbilityResponse(boolean result, String reason) {
        this.result = result;
        this.reason = reason;
    }

    public static AbilityResponse can() {
        return positive;
    }

    public static AbilityResponse cannot(String reason) {
        return new AbilityResponse(false, reason);
    }
}
