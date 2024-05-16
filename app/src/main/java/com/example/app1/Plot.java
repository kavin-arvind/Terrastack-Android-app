package com.example.app1;

public class Plot {
    int gid;
    String survey_no;

    public Plot(int gid, String survey_no) {
        this.gid = gid;
        this.survey_no = survey_no;
    }

    public int getGid() {
        return gid;
    }

    public String getSurvey_no() {
        return survey_no;
    }
}
