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

public class QAActivity extends AppCompatActivity {
    private TextView pm10, pm25, no2;
    //private List<QualidadeDoAr> listaQualidadeAr = new ArrayList<QualidadeDoAr>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa);
        pm10 = findViewById(R.id.txtPM10);
        pm25 = findViewById(R.id.txtPm25);
        no2 = findViewById(R.id.txtNo2);
        this.consultarQualidadeAr();
    }
    public void consultarQualidadeAr(){
      processarDadosQualidadeDoAr pqa = new processarDadosQualidadeDoAr();
        pqa.execute();

    }
    class processarDadosQualidadeDoAr extends AsyncTask<Void, Void, ResultSet> {

        @Override
        protected ResultSet doInBackground(Void... voids) {
            String consulta = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns/>\n" +
                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema/>\n" +
                    "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                    "PREFIX ssn: <http://www.w3.org/ns/ssn/>\n" +
                    "PREFIX ex: <http://example.com/>\n" +
                    "SELECT ?resultpm10 ?resultpm25 ?resultno2  WHERE {\n" +
                    "     ?obs1 rdf:type sosa:Observation;\n" +
                    "        sosa:hasSimpleResult ?resultpm25;\n" +
                    "       sosa:madeBySensor ex:SensorPM25_1.\n" +
                    "     ?obs2 rdf:type sosa:Observation;\n" +
                    "        sosa:hasSimpleResult ?resultpm10;\n" +
                    "       sosa:madeBySensor ex:SensorPM10_1.\n" +
                    "   ?obs3 rdf:type sosa:Observation;\n" +
                    "        sosa:hasSimpleResult ?resultno2;\n" +
                    "       sosa:madeBySensor ex:SensorDioxidoDeAzoto-NO2_4.\n" +
                    " \n" +
                    "}limit 1";
            Query query = QueryFactory.create(consulta);
            QueryExecution qexec = QueryExecutionFactory.createServiceRequest("http://172.18.0.1:3030/Observations/query", query);
            ResultSet results = qexec.execSelect();
            return results;
        }
        protected void onPostExecute(ResultSet results) {
            super.onPostExecute(results);
            //QualidadeDoAr qa = new QualidadeDoAr();
            Log.info("onPosQA", "onPosQualidadeAr");
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                pm10.setText("PM10:" + solution.getLiteral("resultpm10").getString());
                pm25.setText("PM25:" + solution.getLiteral("resultpm25").getString());
                no2.setText("NO2:" + solution.getLiteral("resultno2").getString());
            }

            Log.info("onPosQA", "lista criada");

        }
    }
}