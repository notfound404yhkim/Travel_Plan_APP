package com.example.travelapp.model;

import java.io.Serializable;

public class Map {

    public String name;
    public String vicinity;
    public Geometry geometry;

    public class Geometry implements Serializable {
        public Location location;

        public class Location implements Serializable{
            public double lat;
            public double lng;
        }

    }
}
