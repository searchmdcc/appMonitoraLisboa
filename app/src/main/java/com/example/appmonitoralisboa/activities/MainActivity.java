package com.example.appmonitoralisboa.activities;

import static com.hp.hpl.jena.query.QueryExecution.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.appmonitoralisboa.R;
import com.example.appmonitoralisboa.model.*;
import com.example.appmonitoralisboa.RulesNofication.RuleEventos;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import org.openjena.atlas.logging.Log;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView temperatura, umidade, vento, data;
    private Button atualizar, buscarLocal, qualidadeAr;
    private DataSensors dados = new DataSensors();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dados.inicializar();
        temperatura = findViewById(R.id.txtTemperatura);
        umidade = findViewById(R.id.txtUmidade);
        vento = findViewById(R.id.txtVento);
        data=findViewById(R.id.txtData);
        //buttons
        atualizar = findViewById(R.id.btnAtualizar);
        buscarLocal = findViewById(R.id.btnBuscaLocal);
        qualidadeAr = findViewById(R.id.btnQualidadeAr);

        SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy HH:MM");
        Date d = new Date();
        String dataFormatada = formataData.format(d);
       // data.setText("Data: " + dataFormatada);
        data.setText("Data: 10-05-2022 16:31");
        ProcessarDadosTempo pd = new ProcessarDadosTempo();
        pd.execute();

        atualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProcessarDadosTempo pd = new ProcessarDadosTempo();
                pd.execute();
            }
        });
        buscarLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplicationContext(), LocalMapsActivity.class);
                startActivity(intent);
            }
        });
        qualidadeAr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplicationContext(), QAActivity.class);
                startActivity(intent);
            }
        });
    }


    class ProcessarDadosTempo extends AsyncTask<Void, Void, ResultSet>{
        @Override
        protected ResultSet doInBackground(Void... voids) {
            String consulta = "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "PREFIX iotl: <http://purl.oclc.org/NET/UNIS/fiware/iot-lite/>\n" +
                    "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                    "PREFIX iots: <http://purl.org/iot/ontology/iot-stream#>\n" +
                    "PREFIX ex: <http://example.com/>\n" +
                    "select ?valueTemp ?iotStreamTemp ?valueUmid ?iotStreamUmid ?valueVent ?iotStreamVent  where { \n" +
                    "?obs rdf:type iots:StreamObservation;\n" +
                    "   sosa:hasSimpleResult ?valueTemp;\n" +
                    "  iots:windowStart 202205101100;\n" +
                    "  iots:belongsTo ?iotStreamTemp.\n" +
                    "?iotStreamTemp iots:analyzedBy ?analyticsTemp.\n" +
                    "?analyticsTemp iots:parameters \"Temperatura\".\n" +
                    "\n" +
                    "?obs2 rdf:type iots:StreamObservation;\n" +
                    "   sosa:hasSimpleResult ?valueUmid;\n" +
                    "  iots:windowStart 202205101100;\n" +
                    "  iots:belongsTo ?iotStreamUmid.\n" +
                    "?iotStreamUmid iots:analyzedBy ?analyticsUmid.\n" +
                    "?analyticsUmid iots:parameters \"Umidade_relativa\".\n" +
                    "  \n" +
                    "?obs3 rdf:type iots:StreamObservation;\n" +
                    "   sosa:hasSimpleResult ?valueVent;\n" +
                    "  iots:windowStart 202205101100;\n" +
                    "  iots:belongsTo ?iotStreamVent.\n" +
                    "?iotStreamVent iots:analyzedBy ?analyticsVent.\n" +
                    "?analyticsVent iots:parameters \"Intensidade_do_vento\".\n" +
                    "     \n" +
                    "} ";
            Query query = QueryFactory.create(consulta);
            QueryExecution qexec = QueryExecutionFactory.createServiceRequest("http://172.18.0.1:3030/IoTStream/query", query);
            ResultSet results = qexec.execSelect();
            return results;
        }
        @Override
        protected void onPostExecute(ResultSet results) {
            super.onPostExecute(results);
            Temperatura t = new Temperatura();
            Umidade u = new Umidade();
            Vento v = new Vento();
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                t.setTemperatura(String.valueOf(new DecimalFormat("#,##0.00").format(solution.getLiteral("valueTemp").getDouble())));
                t.setIotStream(solution.getResource("iotStreamTemp").toString());
                u.setUmidade(String.valueOf(new DecimalFormat("#,##0.00").format(solution.getLiteral("valueUmid").getDouble())));
                u.setIotStream(solution.getResource("iotStreamUmid").toString());
                v.setVento(String.valueOf(new DecimalFormat("#,##0.00").format(solution.getLiteral("valueVent").getDouble())));
                v.setIotStream(solution.getResource("iotStreamVent").toString());
                dados.setTemperatura(t);
                dados.setUmidade(u);
                dados.setVento(v);
            }
            //hora do sistema
            /*SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date data = new Date();
            String dataFormatada = formataData.format(data);*/
            temperatura.setText("Temperatura: "+dados.getTemperatura().getTemperatura()+"ºC");
            umidade.setText("Umidade: "+dados.getUmidade().getUmidade()+"%");
            vento.setText("Vento: "+dados.getVento().getVento()+"Km/h");
            RuleEventos re  = new RuleEventos();
            re.verificarEventosTempo(dados, getApplicationContext());
           // umidade.setText(dataFormatada);

        }
    }




}


