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
        data.setText("Data: " + dataFormatada);
        inicializarDados id = new inicializarDados();
        id.execute();

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
        @Override
        protected ResultSet doInBackground(Void... voids) {
            String consulta = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                    "PREFIX iots: <http://purl.org/iot/ontology/iot-stream#>\n" +
                    "PREFIX ex: <http://example.com/>\n" +
                    "select ?temp ?vent ?umid where { \n" +
                    "\t?t rdf:type iots:StreamObservation;\n" +
                    "\t    \tsosa:hasSimpleResult ?temp;\n" +
                    "     \tsosa:hasResultTime 202205101100;\n" +
                    "            \tiots:belongsTo ?iot.\n" +
                    "    ?iot iots:analyzedBy ?analytics.\n" +
                    "    ?analytics ex:sensorType \"Temperatura\".\n" +
                    "    \n" +
                    "    ?v rdf:type iots:StreamObservation;\n" +
                    "\t    \tsosa:hasSimpleResult ?vent;\n" +
                    "     \tsosa:hasResultTime 202205101100;\n" +
                    "            \tiots:belongsTo ?iotV.\n" +
                    "    ?iotV iots:analyzedBy ?analyticsV.\n" +
                    "    ?analyticsV ex:sensorType \"Intensidade_do_vento\".\n" +
                    "    \n" +
                    "        ?u rdf:type iots:StreamObservation;\n" +
                    "\t    \tsosa:hasSimpleResult ?umid;\n" +
                    "     \tsosa:hasResultTime 202205101100;\n" +
                    "            \tiots:belongsTo ?iotU.\n" +
                    "    ?iotU iots:analyzedBy ?analyticsU.\n" +
                    "    ?analyticsU ex:sensorType \"Umidade_relativa\".\n" +
                    "\n" +
                    "} ";
            Query query = QueryFactory.create(consulta);
            QueryExecution qexec = QueryExecutionFactory.createServiceRequest("http://172.18.0.1:3030/IoTStream/query", query);
            ResultSet results = qexec.execSelect();
            return results;
        }
        @Override
        protected void onPostExecute(ResultSet results) {
            super.onPostExecute(results);
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                dados.setTemperatura(new DecimalFormat("#,##0.00").format(solution.getLiteral("temp").getDouble()));
                dados.setUmidade(new DecimalFormat("#,##0.00").format(solution.getLiteral("umid").getDouble()));
                dados.setVento(new DecimalFormat("#,##0.00").format(solution.getLiteral("vent").getDouble()));
            }
            //hora do sistema
            /*SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date data = new Date();
            String dataFormatada = formataData.format(data);*/
            temperatura.setText("Temperatura: "+dados.getTemperatura()+"ºC");
            umidade.setText("Umidade: "+dados.getUmidade()+"%");
            vento.setText("Vento: "+dados.getVento()+"Km/h");

           // umidade.setText(dataFormatada);

        }
    }


    class inicializarDados extends AsyncTask<Void, Void, ResultSet>{
        @Override
        protected ResultSet doInBackground(Void... voids) {
            String consulta = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                    "PREFIX iots: <http://purl.org/iot/ontology/iot-stream#>\n" +
                    "PREFIX ex: <http://example.com/>\n" +
                    "select ?temp ?vent ?umid ?pm10 ?pm25 ?no2 ?so2 ?o3 where { \n" +
                    "\t?t rdf:type iots:StreamObservation;\n" +
                    "\t    \tsosa:hasSimpleResult ?temp;\n" +
                    "     \tsosa:hasResultTime 202205101100;\n" +
                    "            \tiots:belongsTo ?iot.\n" +
                    "    ?iot iots:analyzedBy ?analytics.\n" +
                    "    ?analytics ex:sensorType \"Temperatura\".\n" +
                    "    \n" +
                    "    ?v rdf:type iots:StreamObservation;\n" +
                    "\t    \tsosa:hasSimpleResult ?vent;\n" +
                    "     \tsosa:hasResultTime 202205101100;\n" +
                    "            \tiots:belongsTo ?iotV.\n" +
                    "    ?iotV iots:analyzedBy ?analyticsV.\n" +
                    "    ?analyticsV ex:sensorType \"Intensidade_do_vento\".\n" +
                    "    \n" +
                    "        ?u rdf:type iots:StreamObservation;\n" +
                    "\t    \tsosa:hasSimpleResult ?umid;\n" +
                    "     \tsosa:hasResultTime 202205101100;\n" +
                    "            \tiots:belongsTo ?iotU.\n" +
                    "    ?iotU iots:analyzedBy ?analyticsU.\n" +
                    "    ?analyticsU ex:sensorType \"Umidade_relativa\".\n" +
                    "?obs1 rdf:type iots:StreamObservation;\n" +
                    "\t    \tsosa:hasSimpleResult ?pm10;\n" +
                    "     \tsosa:hasResultTime 202205101100;\n" +
                    "            \tiots:belongsTo ?iotPm10.\n" +
                    "    ?iotPm10 iots:analyzedBy ?analyticsPm10.\n" +
                    "    ?analyticsPm10 ex:sensorType \"PM10\".\n" +
                    "    \n" +
                    "    ?obs2 rdf:type iots:StreamObservation;\n" +
                    "\t    \tsosa:hasSimpleResult ?pm25;\n" +
                    "     \tsosa:hasResultTime 202205101100;\n" +
                    "            \tiots:belongsTo ?iotPm25.\n" +
                    "    ?iotPm25 iots:analyzedBy ?analyticsPm25.\n" +
                    "    ?analyticsPm25 ex:sensorType \"PM25\".\n" +
                    "    \n" +
                    "    ?obs3 rdf:type iots:StreamObservation;\n" +
                    "\t    \tsosa:hasSimpleResult ?o3;\n" +
                    "     \tsosa:hasResultTime 202205101100;\n" +
                    "            \tiots:belongsTo ?iotO3.\n" +
                    "    ?iotO3 iots:analyzedBy ?analyticsO3.\n" +
                    "    ?analyticsO3 ex:sensorType \"O3\".\n" +
                    "    \n" +
                    " ?obs4 rdf:type iots:StreamObservation;\n" +
                    "\t    \tsosa:hasSimpleResult ?no2;\n" +
                    "     \tsosa:hasResultTime 202205101100;\n" +
                    "            \tiots:belongsTo ?iotNo2.\n" +
                    "    ?iotNo2 iots:analyzedBy ?analyticsNo2.\n" +
                    "    ?analyticsNo2 ex:sensorType \"NO2\".\n" +
                    "  \n" +
                    "?obs5 rdf:type iots:StreamObservation;\n" +
                    "\t    \tsosa:hasSimpleResult ?so2;\n" +
                    "     \tsosa:hasResultTime 202205101100;\n" +
                    "            \tiots:belongsTo ?iotSo2.\n" +
                    "    ?iotSo2 iots:analyzedBy ?analyticsSo2.\n" +
                    "    ?analyticsSo2 ex:sensorType \"SO2\".\n" +
                    "}  \n";
            Query query = QueryFactory.create(consulta);
            QueryExecution qexec = QueryExecutionFactory.createServiceRequest("http://172.18.0.1:3030/IoTStream/query", query);
            ResultSet results = qexec.execSelect();
            return results;
        }

        @Override
        protected void onPostExecute(ResultSet results) {
            super.onPostExecute(results);
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                dados.setTemperatura(new DecimalFormat("#,##0.00").format(solution.getLiteral("temp").getDouble()));
                dados.setUmidade(new DecimalFormat("#,##0.00").format(solution.getLiteral("umid").getDouble()));
                dados.setVento(new DecimalFormat("#,##0.00").format(solution.getLiteral("vent").getDouble()));
                dados.setPm10(new DecimalFormat("#,##0.00").format(solution.getLiteral("pm10").getDouble()));
                dados.setPm25(new DecimalFormat("#,##0.00").format(solution.getLiteral("pm25").getDouble()));
                dados.setNo2(new DecimalFormat("#,##0.00").format(solution.getLiteral("no2").getDouble()));
                dados.setO3(new DecimalFormat("#,##0.00").format(solution.getLiteral("o3").getDouble()));
                dados.setSo2(new DecimalFormat("#,##0.00").format(solution.getLiteral("so2").getDouble()));
            }

            temperatura.setText("Temperatura: "+dados.getTemperatura()+"ºC");
            umidade.setText("Umidade: "+dados.getUmidade()+"%");
            vento.setText("Vento: "+dados.getVento()+"Km/h");
           // dados.setTemperatura("40");
            RuleEventos re  = new RuleEventos();
            re.verificarEventos(dados, getApplicationContext());

            }
    }




}


