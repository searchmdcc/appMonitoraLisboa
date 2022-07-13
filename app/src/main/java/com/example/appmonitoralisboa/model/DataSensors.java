package com.example.appmonitoralisboa.model;

public class DataSensors {
    private Temperatura temperatura = new Temperatura();
    private Umidade umidade = new Umidade();
    private Vento vento = new Vento();
    private Pm10 pm10 = new Pm10();
    private Pm25 pm25 = new Pm25();
    private No2 no2 = new No2();
    private O3 o3 = new O3();
    private So2 so2 = new So2();

    public void inicializar(){
        temperatura.setTemperatura("-");
        umidade.setUmidade("-");
        vento.setVento("-");
        pm10.setPm10("-");
        pm25.setPm25("-");
        o3.setO3("-");
        no2.setNo2("-");
        so2.setSo2("-");
    }

    public Temperatura getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Temperatura temperatura) {
        this.temperatura = temperatura;
    }

    public Umidade getUmidade() {
        return umidade;
    }

    public void setUmidade(Umidade umidade) {
        this.umidade = umidade;
    }

    public Vento getVento() {
        return vento;
    }

    public void setVento(Vento vento) {
        this.vento = vento;
    }

    public Pm10 getPm10() {
        return pm10;
    }

    public void setPm10(Pm10 pm10) {
        this.pm10 = pm10;
    }

    public Pm25 getPm25() {
        return pm25;
    }

    public void setPm25(Pm25 pm25) {
        this.pm25 = pm25;
    }

    public No2 getNo2() {
        return no2;
    }

    public void setNo2(No2 no2) {
        this.no2 = no2;
    }

    public O3 getO3() {
        return o3;
    }

    public void setO3(O3 o3) {
        this.o3 = o3;
    }

    public So2 getSo2() {
        return so2;
    }

    public void setSo2(So2 so2) {
        this.so2 = so2;
    }
}
