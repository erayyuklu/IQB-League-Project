package com.iqb.league.model;

public class Color {
    private String colorName;

    // Constructor
    public Color(String colorName) {
        this.colorName = colorName;
    }

    public Color() {
    }

    // Getter
    public String getColorName() {
        return colorName;
    }

    // Setter
    public void setColorName(String colorName) {
        this.colorName = colorName;
    }
}
