package com.example.app1;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

public class Plot {
    @SerializedName("gid")
    private int gid;
    @SerializedName("survey_no")
    private String survey_no;

    private Geometry geometry;
    @SerializedName("geom")
    private String geomstr;
    // Constructor
    public Plot(int gid, String survey_no, String coordinatesJson) {
        this.gid = gid;
        this.survey_no = survey_no;
        this.geomstr = coordinatesJson;
        this.geometry = parseCoordinates(coordinatesJson);
    }

    // Method to parse coordinates JSON string into Geometry object
    private Geometry parseCoordinates(String wkt) {
        WKTReader reader = new WKTReader();
        try {
            return reader.read(wkt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Getters
    public int getGid() {
        return gid;
    }

    public String getSurvey_no() {
        return survey_no;
    }

    public Geometry getGeometry() {
        return geometry;
    }
    public String getGeomstr() {
        return geomstr;
    }

    public void setGeometry() {
        this.geometry = parseCoordinates(this.geomstr);
    }
}
