package com.example.appmonitoralisboa.model;

public class DataSensors {
    private String temperatura="-";
    private String umidade="-";
    private String vento="-";
    private String pm10="-";
    private String pm25="-";
    private String no2="-";
    private String contVeiculos="-";

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getUmidade() {
        return umidade;
    }

    public void setUmidade(String umidade) {
        this.umidade = umidade;
    }

    public String getVento() {
        return vento;
    }

    public void setVento(String vento) {
        this.vento = vento;
    }

    public String getPm10() {
        return pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getNo2() {
        return no2;
    }

    public void setNo2(String no2) {
        this.no2 = no2;
    }

    public String getContVeiculos() {
        return contVeiculos;
    }

    public void setContVeiculos(String contVeiculos) {
        this.contVeiculos = contVeiculos;
    }
}
