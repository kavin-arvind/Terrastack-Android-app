package com.example.app1;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

public class Plot {
    @SerializedName("gid")
    private int gid;
    @SerializedName("survey_tag")
    private String survey_tag;
    @SerializedName("survey_tag_gid")
    private Integer survey_tag_gid;
    @SerializedName("description")
    private String description;
    @SerializedName("varp")
    private float varp;
    @SerializedName("geom")
    private String geomstr;
    private Geometry geometry;

    // Constructor
    public Plot(int gid, String survey_tag, int survey_tag_gid, String description, float varp, String coordinatesJson) {
        this.gid = gid;
        this.survey_tag = survey_tag;
        this.survey_tag_gid = survey_tag_gid;
        this.description = description;
        this.varp = varp;
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
    public String getSurvey_tag() {
        return survey_tag;
    }
    public int getSurvey_tag_gid() {
        return survey_tag_gid;
    }
    public String getDescription() {
        return description;
    }
    public float getVarp() {
        return varp;
    }
    public Geometry getGeometry() {
        return geometry;
    }
    public String getGeomstr() {
        return geomstr;
    }
    // Setters
    public void setGeometry() {
        this.geometry = parseCoordinates(this.geomstr);
    }
    public void setSurvey_tag(String survey_tag) {
        this.survey_tag = survey_tag;
    }
    public void setSurvey_tag_gid(Integer survey_tag_gid) {
        this.survey_tag_gid = survey_tag_gid;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setVarp(float varp) {
        this.varp = varp;
    }
}
