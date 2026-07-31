package agenta.domain;

import java.util.Objects;

public class Counter {
    private int currentValue;
    private int maxValue;

    public Counter(int initValue, int maxValue) {
        this.currentValue = initValue;
        this.maxValue = maxValue;
    }

    public Counter(int maxValue) {
        this.currentValue = maxValue;
        this.maxValue = maxValue;
    }

    public boolean isReady() {
        return currentValue == 0;
    }

    public void tick() {
        if (currentValue > 0) {
            currentValue--;
        }
    }

    public void reset() {
        currentValue = maxValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Counter)) return false;
        Counter other = (Counter) obj;
        return currentValue == other.currentValue && maxValue == other.maxValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentValue, maxValue);
    }
}
