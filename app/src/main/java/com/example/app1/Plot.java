package com.example.app1;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Plot {
    private int gid;
    private String survey_no;
    private List<LatLng> coordinates;

    public Plot(int gid, String survey_no, List<LatLng> coordinates) {
        this.gid = gid;
        this.survey_no = survey_no;
        this.coordinates = coordinates;
    }

    public int getGid() {
        return gid;
    }

    public String getSurvey_no() {
        return survey_no;
    }
    public List<LatLng> getCoordinates() {
        return coordinates;
    }
}
