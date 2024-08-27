package fr.maxlego08.zsupport.api;

public class Vacation {

    private final long startAt;
    private final long endAt;

    public Vacation(long startAt, long endAt) {
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public long getStartAt() {
        return startAt;
    }

    public long getEndAt() {
        return endAt;
    }

    @Override
    public String toString() {
        return "Vacation{" +
                "startAt=" + startAt +
                ", endAt=" + endAt +
                '}';
    }
}
