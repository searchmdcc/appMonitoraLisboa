package com.example.appmonitoralisboa.RulesNofication;
import android.app.Notification;
import android.content.Context;

import com.example.appmonitoralisboa.model.DataSensors;

public class RuleEventos {
    String msg;
     Notificacao notificacao = new Notificacao();
   public void verificarEventos(DataSensors dados, Context contexto){
       if(dados.getTemperatura()>35){
           msg = "Temperatura Elevada";
           notificacao.pushNotification(contexto, "Alerta", msg);

       }if(dados.getVento()>70){
           msg = "Vento Forte";
           notificacao.pushNotification(contexto, "Alerta", msg);
       }if(dados.getUmidade()<30 && dados.getUmidade()>0){
           msg="Umidade Baixa";
           notificacao.pushNotification(contexto, "Alerta", msg);
       }if(dados.getPm10()>99){
           msg="Qualidade do ar prejudicada por nível elevado de partículas PM10";
           notificacao.pushNotification(contexto, "Alerta", msg);
       }
       if(dados.getPm25()>50){
           msg="Qualidade do ar prejudicada por nível elevado de partículas PM25";
           notificacao.pushNotification(contexto, "Alerta", msg);
       }if(dados.getNo2()>400){
           msg="Qualidade do ar prejudicada por nível elevado de dióxido de azoto(NO2)";
           notificacao.pushNotification(contexto, "Alerta", msg);
       }
   }
}
