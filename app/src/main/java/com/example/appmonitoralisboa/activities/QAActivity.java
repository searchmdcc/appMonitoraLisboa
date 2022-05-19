package com.example.appmonitoralisboa.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appmonitoralisboa.R;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.openjena.atlas.logging.Log;

import java.text.DecimalFormat;

public class QAActivity extends AppCompatActivity {
    private TextView pm10, pm25, no2, o3, so2;
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
    }
    public void consultarQualidadeAr(){
      processarDadosQualidadeDoAr pqa = new processarDadosQualidadeDoAr();
        pqa.execute();
    }
    class processarDadosQualidadeDoAr extends AsyncTask<Void, Void, ResultSet> {
        @Override
        protected ResultSet doInBackground(Void... voids) {
            String consulta = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                    "PREFIX iots: <http://purl.org/iot/ontology/iot-stream#>\n" +
                    "PREFIX ex: <http://example.com/>\n" +
                    "select ?pm10 ?pm25 ?no2 ?so2 ?o3 where { \n" +
                    "\t?obs1 rdf:type iots:StreamObservation;\n" +
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
                    "\n" +
                    "}  \n";
            Query query = QueryFactory.create(consulta);
            QueryExecution qexec = QueryExecutionFactory.createServiceRequest("http://172.18.0.1:3030/IoTStream/query", query);
            ResultSet results = qexec.execSelect();
            return results;
        }
        protected void onPostExecute(ResultSet results) {
            super.onPostExecute(results);
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                pm10.setText("PM10: " + new DecimalFormat("#,##0.00").format(solution.getLiteral("pm10").getDouble()));
                pm25.setText("PM25: " + new DecimalFormat("#,##0.00").format(solution.getLiteral("pm25").getDouble()));
                no2.setText("NO2: " + new DecimalFormat("#,##0.00").format(solution.getLiteral("no2").getDouble()));
                o3.setText("O3: "+ new DecimalFormat("#,##0.00").format(solution.getLiteral("o3").getDouble()));
                so2.setText("SO2: "+new DecimalFormat("#,##0.00").format(solution.getLiteral("so2").getDouble()));
            }
        }
    }
}