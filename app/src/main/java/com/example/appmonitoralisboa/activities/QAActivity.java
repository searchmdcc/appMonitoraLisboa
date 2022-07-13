package com.example.appmonitoralisboa.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appmonitoralisboa.R;
import com.example.appmonitoralisboa.RulesNofication.RuleEventos;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.example.appmonitoralisboa.model.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.openjena.atlas.logging.Log;

import java.text.DecimalFormat;

public class QAActivity extends AppCompatActivity {
    private TextView pm10, pm25, no2, o3, so2;
    DataSensors dados = new DataSensors();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa);
        pm10 = findViewById(R.id.txtPM10);
        pm25 = findViewById(R.id.txtPm25);
        no2 = findViewById(R.id.txtNo2);
        o3= findViewById(R.id.txtO3);
        so2=findViewById(R.id.txtSo2);
        this.consultarQualidadeAr();
        dados.inicializar();
    }
    public void consultarQualidadeAr(){
      processarDadosQualidadeDoAr pqa = new processarDadosQualidadeDoAr();
        pqa.execute();
    }
    class processarDadosQualidadeDoAr extends AsyncTask<Void, Void, ResultSet> {
        @Override
        protected ResultSet doInBackground(Void... voids) {
            String consulta ="PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "PREFIX iotl: <http://purl.oclc.org/NET/UNIS/fiware/iot-lite/>\n" +
                    "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                    "PREFIX iots: <http://purl.org/iot/ontology/iot-stream#>\n" +
                    "PREFIX ex: <http://example.com/>\n" +
                    "select ?valuePm10 ?iotStreamPm10 ?valuePm25 ?iotStreamPm25 ?valueO3 ?iotStreamO3 ?valueSo2 ?iotStreamSo2 ?valueNo2 ?iotStreamNo2  where { \n" +
                    "?obs rdf:type iots:StreamObservation;\n" +
                    "   sosa:hasSimpleResult ?valuePm10;\n" +
                    "  iots:windowStart 202205101100;\n" +
                    "  iots:belongsTo ?iotStreamPm10.\n" +
                    "?iotStreamPm10 iots:analyzedBy ?analyticsPm10.\n" +
                    "?analyticsPm10 iots:parameters \"PM10\".\n" +
                    "\n" +
                    "?obs2 rdf:type iots:StreamObservation;\n" +
                    "   sosa:hasSimpleResult ?valuePm25;\n" +
                    "  iots:windowStart 202205101100;\n" +
                    "  iots:belongsTo ?iotStreamPm25.\n" +
                    "?iotStreamPm25 iots:analyzedBy ?analyticsPm25.\n" +
                    "?analyticsPm25 iots:parameters \"PM25\".\n" +
                    "  \n" +
                    "?obs3 rdf:type iots:StreamObservation;\n" +
                    "   sosa:hasSimpleResult ?valueO3;\n" +
                    "  iots:windowStart 202205101100;\n" +
                    "  iots:belongsTo ?iotStreamO3.\n" +
                    "?iotStreamO3 iots:analyzedBy ?analyticsO3.\n" +
                    "?analyticsO3 iots:parameters \"O3\".\n" +
                    "  \n" +
                    "?obs4 rdf:type iots:StreamObservation;\n" +
                    "   sosa:hasSimpleResult ?valueNo2;\n" +
                    "  iots:windowStart 202205101100;\n" +
                    "  iots:belongsTo ?iotStreamNo2.\n" +
                    "?iotStreamNo2 iots:analyzedBy ?analyticsNo2.\n" +
                    "?analyticsNo2 iots:parameters \"NO2\".\n" +
                    "\n" +
                    "?obs5 rdf:type iots:StreamObservation;\n" +
                    "   sosa:hasSimpleResult ?valueSo2;\n" +
                    "  iots:windowStart 202205101100;\n" +
                    "  iots:belongsTo ?iotStreamSo2.\n" +
                    "?iotStreamSo2 iots:analyzedBy ?analyticsSo2.\n" +
                    "?analyticsSo2 iots:parameters \"SO2\".\n" +
                    "  \n" +
                    "     \n" +
                    "} ";
            Query query = QueryFactory.create(consulta);
            QueryExecution qexec = QueryExecutionFactory.createServiceRequest("http://172.18.0.1:3030/IoTStream/query", query);
            ResultSet results = qexec.execSelect();
            return results;
        }
        protected void onPostExecute(ResultSet results) {
            super.onPostExecute(results);
            Pm10 pm10d = new Pm10();  Pm25 pm25d = new Pm25(); O3 o3d = new O3();
            So2 so2d = new So2(); No2 no2d = new No2();

            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                pm10d.setPm10(String.valueOf(new DecimalFormat("#,##0.00").format(solution.getLiteral("valuePm10").getDouble())));
                pm10d.setIotStream(solution.getResource("iotStreamPm10").toString());
                dados.setPm10(pm10d);
                pm25d.setPm25(String.valueOf(new DecimalFormat("#,##0.00").format(solution.getLiteral("valuePm25").getDouble())));
                pm25d.setIotStream(solution.getResource("iotStreamPm25").toString());
                dados.setPm25(pm25d);
                o3d.setO3(String.valueOf(new DecimalFormat("#,##0.00").format(solution.getLiteral("valueO3").getDouble())));
                o3d.setIotStream(solution.getResource("iotStreamO3").toString());
                dados.setO3(o3d);
                no2d.setNo2(new DecimalFormat("#,##0.00").format(solution.getLiteral("valueNo2").getDouble()));
                no2d.setIotStream(solution.getResource("iotStreamNo2").toString());
                dados.setNo2(no2d);
                so2d.setSo2(new DecimalFormat("#,##0.00").format(solution.getLiteral("valueSo2").getDouble()));
                so2d.setIotStream(solution.getResource("iotStreamSo2").toString());
                dados.setSo2(so2d);
            }
            pm10.setText("PM10: "+dados.getPm10().getPm10());
            pm25.setText("PM25: " + dados.getPm25().getPm25());
            no2.setText("NO2: " +dados.getNo2().getNo2() );
            o3.setText("O3: "+ dados.getO3().getO3() );
            so2.setText("SO2: "+ dados.getSo2().getSo2());
            RuleEventos re  = new RuleEventos();
            re.verificarEventosQA(dados, getApplicationContext());

        }
    }
}