package com.iqb.league.dto;

import lombok.Data;

@Data
public class TeamDTO {
    private int id;
    private String name;
    private short foundationYear;
    private String[] colors; // Renkler dizisi olarak tanımlandı

    // Parametreli yapıcı metod
    public TeamDTO(String name, short foundationYear, String[] colors) {
        this.name = name;
        this.foundationYear = foundationYear;
        this.colors = colors;
    }

    public TeamDTO() {

    }
}