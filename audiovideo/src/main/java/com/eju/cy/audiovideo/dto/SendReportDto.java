package com.eju.cy.audiovideo.dto;

public class SendReportDto {

    private  String report="";
    private  String community_id="";
    private  String client_id="";
    private  String broker_id="";

    private  String mf_id="";
    private  String community_name="";
    private  String lj_city_id="";

    public String getMf_id() {
        return mf_id;
    }

    public void setMf_id(String mf_id) {
        this.mf_id = mf_id;
    }

    public String getCommunity_name() {
        return community_name;
    }

    public void setCommunity_name(String community_name) {
        this.community_name = community_name;
    }

    public String getLj_city_id() {
        return lj_city_id;
    }

    public void setLj_city_id(String lj_city_id) {
        this.lj_city_id = lj_city_id;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getCommunity_id() {
        return community_id;
    }

    public void setCommunity_id(String community_id) {
        this.community_id = community_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getBroker_id() {
        return broker_id;
    }


    @Override
    public String toString() {
        return "SendReportDto{" +
                "report='" + report + '\'' +
                ", community_id='" + community_id + '\'' +
                ", client_id='" + client_id + '\'' +
                ", broker_id='" + broker_id + '\'' +
                ", mf_id='" + mf_id + '\'' +
                ", community_name='" + community_name + '\'' +
                ", lj_city_id='" + lj_city_id + '\'' +
                '}';
    }

    public void setBroker_id(String broker_id) {
        this.broker_id = broker_id;


    }
}
