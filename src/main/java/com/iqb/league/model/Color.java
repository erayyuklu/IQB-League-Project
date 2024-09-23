package com.iqb.league.model;
import lombok.Data;

@Data
public class Color {
    private String colorName;

    // Constructor
    public Color(String colorName) {
        this.colorName = colorName;
    }

    public Color() {
    }

    @Override
    public String toString() {
        return colorName; // Yalnızca renk ismini döndür
    }


}
