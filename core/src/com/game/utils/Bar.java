package com.game.utils;

public class Bar {
    private float max;
    private float current;

    public Bar(float max) {
        this.max = max;
        this.current = max;
    }

    public Bar(float max, float current) {
        this.max = max;
        this.current = current;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float m) {
        max = m;
    }

    public void setMax(float m, boolean fillCurrent) {
        max = m;
        if (fillCurrent) current = m;
    }

    public void updateMax(float m, boolean fillCurrent) {
        setMax(max + m, fillCurrent);
    }

    public float getCurrent() {
        return current;
    }

    public void setCurrent(float c) {
        current = c;
    }

    public void updateCurrent(float c) {
        updateCurrent(c, false);
    }

    public void updateCurrent(float c, boolean limitToMax) {
        if (limitToMax) {
            if (current + c < max) current += c;
            if (current + c >= max) current = max;
        } else current += c;
    }

    public boolean isFull() {
        return current >= max;
    }

    public boolean isEmpty() {
        return current <= 0;
    }
}
