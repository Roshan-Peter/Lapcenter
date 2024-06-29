package com.example.lapcenter;

import android.content.Context;

public class LapData {
    private int id;
    private String lapName;
    private String lapdescription;
    private String price;
    private String image;
    private String fulldescription;

    public LapData(int id, String lapName, String lapdescription, String price, String image, String fulldescription) {
        this.id = id;
        this.lapName = lapName;
        this.lapdescription = lapdescription;
        this.price = price;
        this.image = image;
        this.fulldescription = fulldescription;
    }

    public int id() {
        return id;
    }

    public String lapName() {
        return lapName;
    }

    public String lapdescription() {
        return lapdescription;
    }

    public String price() {
        return price;
    }

    public String image() {
        return image;
    }

    public String fulldescription() {
        return fulldescription;
    }
}
