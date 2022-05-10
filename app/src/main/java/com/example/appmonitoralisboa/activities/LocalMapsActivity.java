package com.example.appmonitoralisboa.activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appmonitoralisboa.R;
import com.example.appmonitoralisboa.activities.*;
import com.example.appmonitoralisboa.databinding.ActivityLocalMapsBinding;
import com.example.appmonitoralisboa.model.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.example.appmonitoralisboa.model.Estacoes;
import org.openjena.atlas.logging.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class LocalMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityLocalMapsBinding binding;
    private ArrayList<Estacoes> ListaEstacoes = new ArrayList<Estacoes>();
    private Estacoes estacao1, estacao2, estacao3, estacao4, estacao5, estacao6,
            estacao7, estacao8, estacao9, estacao10, estacao11, estacao12, estacao13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLocalMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        estacao1=new Estacoes();estacao2=new Estacoes();estacao3=new Estacoes();estacao4=new Estacoes();
        estacao5=new Estacoes();estacao6=new Estacoes();estacao7=new Estacoes();estacao8=new Estacoes();
        estacao9=new Estacoes();estacao10=new Estacoes();estacao11=new Estacoes();estacao12=new Estacoes();
        estacao13=new Estacoes();
        ProcessarMetadados pm = new ProcessarMetadados();
        pm.execute();
        ProcessarObservacoes po = new ProcessarObservacoes();
        po.execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.info("testando carregamento mapa", "Mapa sendo carregado..marcadores");
        mMap = googleMap;

    }

    public void marcarMapa() {

        if (ListaEstacoes != null) {
            LatLng coordenadas = null;
            for (int i = 0; i < ListaEstacoes.size(); i++) {
                Log.info("onPosExecuteMapa", "colocando marcações");
                coordenadas = new LatLng(ListaEstacoes.get(i).getLat(), ListaEstacoes.get(i).getLgt());
                mMap.addMarker(new MarkerOptions().position(coordenadas).title("Estação " + ListaEstacoes.get(i).getEst()))
                        .setSnippet("Rua: " + ListaEstacoes.get(i).getEndereco()+
                                "\nTemperatura: "+ListaEstacoes.get(i).getDados().getTemperatura()+
                "\nUmidade: "+ListaEstacoes.get(i).getDados().getUmidade()+
                        "\nVento: "+ListaEstacoes.get(i).getDados().getVento()+
                        "\nPM10: "+ListaEstacoes.get(i).getDados().getPm10()+
                        "\nPM25: "+ListaEstacoes.get(i).getDados().getPm25()+
                        "\nNO2: "+ListaEstacoes.get(i).getDados().getNo2()+
                        "\nQuantidade de veículos:" +ListaEstacoes.get(i).getDados().getContVeiculos());


            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(coordenadas));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                    LinearLayout info = new LinearLayout(context);
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(context);
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(context);
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });
        }
    }

    class ProcessarMetadados extends AsyncTask<Void, Void, ResultSet> {
            //fuseki não suporta agregação avg criar iot-stream e consultá-lo
            @Override
            protected ResultSet doInBackground(Void... voids) {
                String consulta = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns/>\n" +
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema/>\n" +
                        "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                        "PREFIX ssn: <http://www.w3.org/ns/ssn/>\n" +
                        "PREFIX ex: <http://example.com/>\n" +
                        "PREFIX owl: <http://www.w3.org/2002/07/owl/>\n" +
                        "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos/>\n" +
                        "select distinct ?est ?end ?lat ?long where{\n" +
                        "    ?sensor rdf:type ssn:Sensor;\n" +
                        "    \t\tex:belongsStation ?estacao.\n" +
                        "    ?estacao rdfs:label ?est;\n" +
                        "    \t owl:sameAs ?est2.\n" +
                        "    ?est2 ex:hasAddress ?ender.\n" +
                        "    ?ender rdfs:label ?end;\n" +
                        "           geo:location ?ponto.\n" +
                        "     ?ponto geo:lat ?lat;\n" +
                        "            geo:long ?long.\n" +
                        "    \n" +
                        "  }";
                Query query = QueryFactory.create(consulta);
                QueryExecution qexec = QueryExecutionFactory.createServiceRequest("http://172.18.0.1:3030/MetadataSensors/query", query);
                ResultSet results = qexec.execSelect();
                return results;
            }

            @Override
            protected void onPostExecute(ResultSet results) {
                super.onPostExecute(results);
                 Log.info("Conferindo dados", "resultSet");
                while (results.hasNext()) {
                    QuerySolution solution = results.nextSolution();
                    String numEst = solution.get("est").toString().replace("http://example.com/", "");
                    Log.info("valor estacao", numEst);
                    if(numEst.equals("1")){
                        Log.info("Metadado 1", numEst);
                        String lat = solution.get("lat").toString().replace("http://example.com/", "");
                        estacao1.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("http://example.com/", "");
                        estacao1.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao1.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao1.setEst(est);
                    }else if(numEst.equals("2")){
                        Log.info("Metadado 2", numEst);
                        String lat = solution.get("lat").toString().replace("http://example.com/", "");
                        estacao2.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("http://example.com/", "");
                        estacao2.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao2.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao2.setEst(est);

                    }else if(numEst.equals("3")){
                        String lat = solution.get("lat").toString().replace("http://example.com/", "");
                        estacao3.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("http://example.com/", "");
                        estacao3.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao3.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao3.setEst(est);
                    }else if(numEst.equals("4")){
                        String lat = solution.get("lat").toString().replace("http://example.com/", "");
                        estacao4.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("http://example.com/", "");
                        estacao4.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao4.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao4.setEst(est);
                    }else if(numEst.equals("5")){
                        String lat = solution.get("lat").toString().replace("http://example.com/", "");
                        estacao5.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("http://example.com/", "");
                        estacao5.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao5.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao5.setEst(est);
                    }else if(numEst.equals("6")){
                        String lat = solution.get("lat").toString().replace("http://example.com/", "");
                        estacao6.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("http://example.com/", "");
                        estacao6.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao6.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao6.setEst(est);
                    }else if(numEst.equals("7")){
                        String lat = solution.get("lat").toString().replace("http://example.com/", "");
                        estacao7.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("http://example.com/", "");
                        estacao7.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao7.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao7.setEst(est);
                    }else if(numEst.equals("8")){
                        String lat = solution.get("lat").toString().replace("http://example.com/", "");
                        estacao8.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("http://example.com/", "");
                        estacao8.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao8.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao8.setEst(est);
                    }else if(numEst.equals("9")){
                        String lat = solution.get("lat").toString().replace("http://example.com/", "");
                        estacao9.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("http://example.com/", "");
                        estacao9.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao9.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao9.setEst(est);
                    }else if(numEst.equals("10")){
                        String lat = solution.get("lat").toString().replace("http://example.com/", "");
                        estacao10.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("http://example.com/", "");
                        estacao10.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao10.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao10.setEst(est);
                    }else if(numEst.equals("50")){
                        String lat = solution.get("lat").toString().replace("http://example.com/", "");
                        estacao11.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("http://example.com/", "");
                        estacao11.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao11.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao11.setEst(est);
                    }else if(numEst.equals("1027")){
                        String lat = solution.get("lat").toString().replace("http://example.com/", "");
                        estacao12.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("http://example.com/", "");
                        estacao12.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao12.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao12.setEst(est);
                    }else if(numEst.equals("1079")){
                        String lat = solution.get("lat").toString().replace("http://example.com/", "");
                        estacao13.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("http://example.com/", "");
                        estacao13.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao13.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao13.setEst(est);

                    }

                    Log.info("onPosExecuteMapa", "adcionandoMetadados");
                }
                Log.info("onPosExecuteMapa", "lista metadados criada");
            }
        }

    class ProcessarObservacoes extends AsyncTask<Void, Void, ResultSet> {
        //fuseki não suporta agregação avg criar iot-stream e consultá-lo
        @Override
        protected ResultSet doInBackground(Void... voids) {
            String consulta = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns/>\n" +
                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema/>\n" +
                    "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                    "PREFIX ssn: <http://www.w3.org/ns/ssn/>\n" +
                    "PREFIX ex: <http://example.com/>\n" +
                    "PREFIX owl: <http://www.w3.org/2002/07/owl/>\n" +
                    "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos/>\n" +
                    "\n" +
                    "select ?result ?sensor where{\n" +
                    "\t?observ rdf:type sosa:Observation;\n" +
                    "    \t    sosa:hasSimpleResult ?result;\n" +
                    "            sosa:hasResultTime \"202108201000\";\n" +
                    "            sosa:madeBySensor ?sensor.\n" +
                    "      \n" +
                    "  \n" +
                    "    \n" +
                    "  }";
            Query query = QueryFactory.create(consulta);
            QueryExecution qexec = QueryExecutionFactory.createServiceRequest("http://172.18.0.1:3030/Observations/query", query);
            ResultSet results = qexec.execSelect();
            return results;
        }

        @Override
        protected void onPostExecute(ResultSet results) {
            super.onPostExecute(results);
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                String sensor = solution.get("sensor").toString().replace("http://example.com/", "");
                if(sensor.contains("_1") && !sensor.contains("_10") ){
                    DataSensors dados = new DataSensors();
                    Log.info("tipo 1",sensor);
                    if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                        dados.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                        dados.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                        dados.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setContVeiculos(vei);
                    }
                    estacao1.setDados(dados);
                }else if(sensor.contains("_2")){
                    DataSensors dados = new DataSensors();
                    Log.info("tipo 2",sensor);
                    if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                        dados.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                        dados.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                        dados.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setContVeiculos(vei);
                    }
                    estacao2.setDados(dados);
                }else if(sensor.contains("_3")){
                    DataSensors dados = new DataSensors();
                    Log.info("tipo 3",sensor);
                    if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                        dados.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                        dados.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                        dados.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setContVeiculos(vei);
                    }
                    estacao3.setDados(dados);
                }else if(sensor.contains("_4")){
                    DataSensors dados = new DataSensors();
                    if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                        dados.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                        dados.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                        dados.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setContVeiculos(vei);
                    }
                    estacao4.setDados(dados);
                }else if(sensor.contains("_5")){
                    DataSensors dados = new DataSensors();
                    if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                        dados.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                        dados.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                        dados.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setContVeiculos(vei);
                    }
                    estacao5.setDados(dados);
                }else if(sensor.contains("_6")){
                    DataSensors dados = new DataSensors();
                    if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                        dados.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                        dados.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                        dados.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setContVeiculos(vei);
                    }
                    estacao6.setDados(dados);
                }else if(sensor.contains("_7")){
                    DataSensors dados = new DataSensors();
                   if(sensor.contains("Temperatura")){
                       String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "")+"ºC";
                       dados.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                       String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                      dados.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                       String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                      dados.setVento(vent);
                    }else if(sensor.contains("PM10")){
                       String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                      dados.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                       String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       dados.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                       String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       dados.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                       String vei = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       dados.setContVeiculos(vei);
                    }
                   estacao7.setDados(dados);
                }else if(sensor.contains("_8")){
                    DataSensors dados = new DataSensors();
                   if(sensor.contains("Temperatura")){
                       String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                       dados.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                       String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                        dados.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                       String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                        dados.setVento(vent);
                    }else if(sensor.contains("PM10")){
                       String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       dados.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                       String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                      dados.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                       String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       dados.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                       String vei = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       dados.setContVeiculos(vei);
                    }
                   estacao8.setDados(dados);
                }else if(sensor.contains("_9")){
                    DataSensors dados = new DataSensors();
                  if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                        dados.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                      String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                      dados.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                      String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                      dados.setVento(vent);
                    }else if(sensor.contains("PM10")){
                      String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                     dados.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                      String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                     dados.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                      String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                      dados.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                      String vei = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                      dados.setContVeiculos(vei);
                    }
                  estacao9.setDados(dados);
                }else if(sensor.contains("_10")&& !sensor.contains("_1027")&& !sensor.contains("_1079")){
                    DataSensors dados = new DataSensors();
                if(sensor.contains("Temperatura")){
                    String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                     dados.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                        dados.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                        dados.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setContVeiculos(vei);
                    }
                estacao10.setDados(dados);
                }else if(sensor.contains("_50")){
                    DataSensors dados = new DataSensors();
                 if(sensor.contains("Temperatura")){
                     String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                     dados.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                        dados.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                        dados.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString().replace("h^^ttp://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setContVeiculos(vei);
                    }
                 estacao11.setDados(dados);
                }else if(sensor.contains("_1027")){
                    DataSensors dados = new DataSensors();
                    if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                       dados.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                        dados.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                        dados.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       dados.setContVeiculos(vei);
                    }
                    estacao12.setDados(dados);
                }else if(sensor.contains("_1079")){
                    DataSensors dados = new DataSensors();
                    if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                        dados.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                       dados.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                        dados.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                       String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       dados.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        dados.setContVeiculos(vei);
                    }
                }

                Log.info("onPosExecuteMapa", "adcionando observações");
            }
            ListaEstacoes.add(estacao1);ListaEstacoes.add(estacao2);ListaEstacoes.add(estacao3);ListaEstacoes.add(estacao4);ListaEstacoes.add(estacao5);
            ListaEstacoes.add(estacao6);ListaEstacoes.add(estacao7);ListaEstacoes.add(estacao8);ListaEstacoes.add(estacao9);ListaEstacoes.add(estacao10);
            ListaEstacoes.add(estacao11);ListaEstacoes.add(estacao12);ListaEstacoes.add(estacao13);
            marcarMapa();

        }
    }

}