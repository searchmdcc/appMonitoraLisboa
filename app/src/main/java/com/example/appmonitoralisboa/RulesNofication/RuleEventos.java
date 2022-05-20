package com.example.appmonitoralisboa.RulesNofication;
import com.example.appmonitoralisboa.model.Event;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.appmonitoralisboa.model.DataSensors;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;


import java.text.SimpleDateFormat;
import java.util.Date;

public class RuleEventos {
    Notificacao notificacao = new Notificacao();
    String msg;
    SimpleDateFormat formataData = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public void verificarEventos(DataSensors dados, Context contexto) {

        if (Double.parseDouble(dados.getTemperatura()) > 35) {
            msg= "Temperatura Elevada";
            notificacao.pushNotification(contexto, "Alerta", msg);
            Date data = new Date();
            String dataFormatada = formataData.format(data);
            Event ev = new Event();
            ev.setEvent(msg);
            ev.setMethod("AVG");
            ev.setStartProcess(dataFormatada);
            ev.setEndProcess(dataFormatada);
            ev.setStartStream(dataFormatada);
            ev.setEndStream(dataFormatada);
            ev.setTypeSensor("Temperatura");
            insert(ev);
            Log.i("testeNotificação", "volta para o if");
        }
        if (Double.parseDouble(dados.getVento()) > 70) {
            String msg = "Vento Forte";
            notificacao.pushNotification(contexto, "Alerta", msg);
            Date data = new Date();
            String dataFormatada = formataData.format(data);
            Event ev = new Event();
            ev.setEvent(msg);
            ev.setMethod("AVG");
            ev.setStartProcess(dataFormatada);
            ev.setEndProcess(dataFormatada);
            ev.setStartStream(dataFormatada);
            ev.setEndStream(dataFormatada);
            ev.setTypeSensor("Intensidade do Vento");
            insert(ev);
        }
        if ((Double.parseDouble(dados.getUmidade()) < 30) && (Double.parseDouble(dados.getUmidade()) > 0)) {
            String msg = "Umidade Baixa";
            notificacao.pushNotification(contexto, "Alerta", msg);
            Date data = new Date();
            String dataFormatada = formataData.format(data);
            Event ev = new Event();
            ev.setEvent(msg);
            ev.setMethod("AVG");
            ev.setStartProcess(dataFormatada);
            ev.setEndProcess(dataFormatada);
            ev.setStartStream(dataFormatada);
            ev.setEndStream(dataFormatada);
            ev.setTypeSensor("Umidade");
            insert(ev);
        }
        if (Double.parseDouble(dados.getPm10()) > 99) {
            String msg = "Qualidade do ar prejudicada por nível elevado de partículas PM10";
            notificacao.pushNotification(contexto, "Alerta", msg);
            Date data = new Date();
            String dataFormatada = formataData.format(data);
            Event ev = new Event();
            ev.setEvent(msg);
            ev.setMethod("AVG");
            ev.setStartProcess(dataFormatada);
            ev.setEndProcess(dataFormatada);
            ev.setStartStream(dataFormatada);
            ev.setEndStream(dataFormatada);
            ev.setTypeSensor("PM10");
            insert(ev);
        }
        if (Double.parseDouble(dados.getPm25()) > 50) {
            String msg = "Qualidade do ar prejudicada por nível elevado de partículas PM25";
            notificacao.pushNotification(contexto, "Alerta", msg);
            Date data = new Date();
            String dataFormatada = formataData.format(data);
            Event ev = new Event();
            ev.setEvent(msg);
            ev.setMethod("AVG");
            ev.setStartProcess(dataFormatada);
            ev.setEndProcess(dataFormatada);
            ev.setStartStream(dataFormatada);
            ev.setEndStream(dataFormatada);
            ev.setTypeSensor("PM25");
            insert(ev);
        }
        if (Double.parseDouble(dados.getNo2()) > 400) {
            String msg = "Qualidade do ar prejudicada por nível elevado de dióxido de azoto(NO2)";
            notificacao.pushNotification(contexto, "Alerta", msg);
            Date data = new Date();
            String dataFormatada = formataData.format(data);
            Event ev = new Event();
            ev.setEvent(msg);
            ev.setMethod("AVG");
            ev.setStartProcess(dataFormatada);
            ev.setEndProcess(dataFormatada);
            ev.setStartStream(dataFormatada);
            ev.setEndStream(dataFormatada);
            ev.setTypeSensor("NO2");
            insert(ev);
        }
        if (Double.parseDouble(dados.getO3()) > 240) {
            String msg = "Qualidade do ar prejudicada por nível elevado de ozonio (O3)";
            notificacao.pushNotification(contexto, "Alerta", msg);
            Date data = new Date();
            String dataFormatada = formataData.format(data);
            Event ev = new Event();
            ev.setEvent(msg);
            ev.setMethod("AVG");
            ev.setStartProcess(dataFormatada);
            ev.setEndProcess(dataFormatada);
            ev.setStartStream(dataFormatada);
            ev.setEndStream(dataFormatada);
            ev.setTypeSensor("O3");
            insert(ev);
        }
        if (Double.parseDouble(dados.getSo2()) > 500) {
            String msg = "Qualidade do ar prejudicada por nível elevado de dióxido de enxofre (SO2)";
            notificacao.pushNotification(contexto, "Alerta", msg);
            Date data = new Date();
            String dataFormatada = formataData.format(data);
            Event ev = new Event();
            ev.setEvent(msg);
            ev.setMethod("AVG");
            ev.setStartProcess(dataFormatada);
            ev.setEndProcess(dataFormatada);
            ev.setStartStream(dataFormatada);
            ev.setEndStream(dataFormatada);
            ev.setTypeSensor("SO2");
            insert(ev);
        }
    }

    public void insert(Event ev) {
       /* DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
        DatabaseReference eventos= referencia.child("Eventos");
        Event evento = ev;
        eventos.push().setValue(evento);*/
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Eventos")
                .add(ev)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("teste", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("teste", "Error adding document", e);
                    }
                });


    }
}
