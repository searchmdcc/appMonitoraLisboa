package com.example.appmonitoralisboa.model;

public class Event {
    private String method;
    private String startObs, endObs;
    private String startStream, endStream;
    private String parameters, event, iotStream;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStartObs() {
        return startObs;
    }

    public void setStartObs(String startObs) {
        this.startObs = startObs;
    }

    public String getEndObs() {
        return endObs;
    }

    public void setEndObs(String endObs) {
        this.endObs = endObs;
    }

    public String getStartStream() {
        return startStream;
    }

    public void setStartStream(String startStream) {
        this.startStream = startStream;
    }

    public String getEndStream() {
        return endStream;
    }

    public void setEndStream(String endStream) {
        this.endStream = endStream;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getIotStream() {
        return iotStream;
    }

    public void setIotStream(String iotStream) {
        this.iotStream = iotStream;
    }
}
