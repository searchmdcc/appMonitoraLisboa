package com.example.appmonitoralisboa.model;

public class Event {
    private String method;
    private String startProcess, endProcess;
    private String startStream, endStream;
    private String typeSensor, event;
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public String getStartProcess() {
        return startProcess;
    }
    public void setStartProcess(String startProcess) {
        this.startProcess = startProcess;
    }
    public String getEndProcess() {
        return endProcess;
    }
    public void setEndProcess(String endProcess) {
        this.endProcess = endProcess;
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
    public String getTypeSensor() {
        return typeSensor;
    }
    public void setTypeSensor(String typeSensor) {
        this.typeSensor = typeSensor;
    }
    public String getEvent() {
        return event;
    }
    public void setEvent(String event) {
        this.event = event;
    }
}
