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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView temperatura, umidade, vento;
    private Button atualizar, buscarLocal, qualidadeAr, notificacao;
    private DataSensors dados = new DataSensors();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        temperatura = findViewById(R.id.txtTemperatura);
        umidade = findViewById(R.id.txtUmidade);
        vento = findViewById(R.id.txtVento);
        //buttons
        atualizar = findViewById(R.id.btnAtualizar);
        buscarLocal = findViewById(R.id.btnBuscaLocal);
        qualidadeAr = findViewById(R.id.btnQualidadeAr);
        notificacao = findViewById(R.id.btnNotificacoes);

        //dados.setNo2(450);
        //dados.setTemperatura(38);

        RuleEventos re  = new RuleEventos();
        re.verificarEventos(dados, getApplicationContext());
        atualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProcessarDados pd = new ProcessarDados();
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


    class ProcessarDados extends AsyncTask<Void, Void, ResultSet>{
//fuseki não suporta agregação avg criar iot-stream e consultá-lo
        @Override
        protected ResultSet doInBackground(Void... voids) {
            String consulta = "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns/>\n" +
                    "PREFIX ssn: <http://www.w3.org/ns/ssn/>\n" +
                    "PREFIX ex: <http://example.com/>\n" +
                    "select avg(?resTemp) as ?avgTemperatura avg(?resUmid) as ?avgUmidade avg(?resVent) as ?avgVento where{\n" +
                    "{\n" +
                    "?temp sosa:madeBySensor ?sensTemp;\n" +
                    "sosa:hasSimpleResult ?resTemp;\n" +
                    "sosa:hasResultTime ?rtTemp;\n" +
                    "BIND (str(?sensTemp) AS ?strTemp)\n" +
                    "filter(regex(?strTemp, \"SensorTemperatura\"))\n" +
                    "filter (?rtTemp in(\"202108301200\"))\n" +
                    " }\n" +
                    " {\n" +
                    "?umid sosa:madeBySensor ?sensUmid;\n" +
                    "sosa:hasSimpleResult ?resUmid;\n" +
                    "sosa:hasResultTime ?rtUmid;\n" +
                    "BIND (str(?sensUmid) AS ?strUmid)\n" +
                    "filter(regex(?strUmid, \"SensorUmidade\"))\n" +
                    "filter (?rtUmid in(\"202108301200\"))\n" +
                    "  }\n" +
                    " {\n" +
                    "?vent sosa:madeBySensor ?sensVent;\n" +
                    "sosa:hasSimpleResult ?resVent;\n" +
                    "sosa:hasResultTime ?rtVent;\n" +
                    "BIND (str(?sensVent) AS ?strVent)\n" +
                    "filter(regex(?strVent, \"Vento\"))\n" +
                    "filter (?rtVent in(\"202108301200\"))\n" +
                    "  }\n" +
                    "}limit 1";
            Query query = QueryFactory.create(consulta);
            QueryExecution qexec = QueryExecutionFactory.createServiceRequest("http://172.18.0.1:3030/Observations/query", query);
            ResultSet results = qexec.execSelect();
            return results;
        }

        @Override
        protected void onPostExecute(ResultSet results) {
            super.onPostExecute(results);
            DataSensors t = new DataSensors();
            Log.info("onPosExecute", "onPos");
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                String avgTemp = solution.getLiteral("avgTemperatura").getString();
                String avgUmid = solution.getLiteral("avgUmidade").getString();
                String avgVento = solution.getLiteral("avgVento").getString();
                t.setTemperatura(avgTemp);
                t.setUmidade(avgUmid);
                t.setVento(avgVento);
            }
            //hora do sistema
            /*SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date data = new Date();
            String dataFormatada = formataData.format(data);*/
            temperatura.setText("Temperatura: "+t.getTemperatura()+"ºC");
            umidade.setText("Umidade: "+t.getUmidade()+"%");
            vento.setText("Vento: "+t.getVento()+"Km/h");

           // umidade.setText(dataFormatada);

        }
    }





    }


