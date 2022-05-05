package com.example.appmonitoralisboa.activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.example.appmonitoralisboa.R;
import com.example.appmonitoralisboa.activities.*;
import com.example.appmonitoralisboa.databinding.ActivityLocalMapsBinding;
import com.example.appmonitoralisboa.model.Tempo;
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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.info("testando carregamento mapa", "Mapa sendo carregado..marcadores");
        mMap = googleMap;

    }

    public void marcarMapa() {

        if (ListaEstacoes != null) {
            for(int i=0; i<ListaEstacoes.size(); i++){
                Log.info("onPosExecuteMapa", "colocando marcações");
                LatLng coordenadas = new LatLng(ListaEstacoes.get(i).getLat(), ListaEstacoes.get(i).getLgt());
                mMap.addMarker(new MarkerOptions().position(coordenadas).title("Estação " + ListaEstacoes.get(i).getEst()))
                        .setSnippet("Rua: " + ListaEstacoes.get(i).getEndereco());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(coordenadas));

            }
         /*   mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    Log.info("Evento clique", "chamadoo");
                    for (int i = 0; i < ListaEstacoes.size(); i++) {
                        Log.info("lista -lat", String.valueOf(ListaEstacoes.get(i).getLat()));
                        Log.info("lista -long", String.valueOf(ListaEstacoes.get(i).getLgt()));
                        Log.info("marcador-lat", String.valueOf(latLng.latitude));
                        Log.info("lista -long", String.valueOf(latLng.longitude));
                        if(latLng.latitude==ListaEstacoes.get(i).getLat() && latLng.longitude==ListaEstacoes.get(i).getLgt()){
                            Log.info("Evento clique", "coordenada igual");
                            String texto = "Temperatura: "+ListaEstacoes.get(i).getTemperatura()+
                                    "\nUmidade: "+ListaEstacoes.get(i).getUmidade()+
                                    "\nVento: "+ListaEstacoes.get(i).getVento()+
                                    "\nPM10: "+ListaEstacoes.get(i).getPm10()+
                                    "\nPM25: "+ListaEstacoes.get(i).getPm25()+
                                    "\nNO2: "+ListaEstacoes.get(i).getNo2()+
                                    "\nQuantidade de veículos:" +ListaEstacoes.get(i).getContVeiculos();
                            Toast.makeText(getApplicationContext(),texto,
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                }
            });*/

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
                    Log.info("tipo 1",sensor);
                    if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString()+"ºC";
                        estacao1.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString()+"%";
                        estacao1.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString()+"Km/h";
                        estacao1.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString();
                        estacao1.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString();
                        estacao1.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString();
                        estacao1.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString();
                        estacao1.setContVeiculos(vei);
                    }
                }else if(sensor.contains("_2")){
                    Log.info("tipo 2",sensor);
                    if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString()+"ºC";
                        estacao2.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString()+"%";
                        estacao2.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString()+"Km/h";
                        estacao2.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString();
                        estacao2.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString();
                        estacao2.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString();
                        estacao2.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString();
                        estacao2.setContVeiculos(vei);
                    }
                }else if(sensor.contains("_3")){
                    Log.info("tipo 3",sensor);
                    if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString()+"ºC";
                        estacao3.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString()+"%";
                        estacao3.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString()+"Km/h";
                        estacao3.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString();
                        estacao3.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString();
                        estacao3.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString();
                        estacao3.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString();
                        estacao3.setContVeiculos(vei);
                    }
                }else if(sensor.contains("_4")){
                    if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString()+"ºC";
                        estacao4.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString()+"%";
                        estacao4.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString()+"Km/h";
                        estacao4.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString();
                        estacao4.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString();
                        estacao4.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString();
                        estacao4.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString();
                        estacao4.setContVeiculos(vei);
                    }
                }else if(sensor.contains("_5")){
                    if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString()+"ºC";
                        estacao5.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString()+"%";
                        estacao5.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString()+"Km/h";
                        estacao5.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString();
                        estacao5.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString();
                        estacao5.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString();
                        estacao5.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString();
                        estacao5.setContVeiculos(vei);
                    }
                }else if(sensor.contains("_6")){
                    if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString()+"ºC";
                        estacao6.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString()+"%";
                        estacao6.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString()+"Km/h";
                        estacao6.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString();
                        estacao6.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString();
                        estacao6.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString();
                        estacao6.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString();
                        estacao6.setContVeiculos(vei);
                    }
                }else if(sensor.contains("_7")){
                   if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString()+"ºC";
                        estacao7.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString()+"%";
                        estacao7.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString()+"Km/h";
                        estacao7.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString();
                        estacao7.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString();
                        estacao7.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString();
                        estacao7.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString();
                        estacao7.setContVeiculos(vei);
                    }
                }else if(sensor.contains("_8")){
                   if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString()+"ºC";
                        estacao8.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString()+"%";
                        estacao8.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString()+"Km/h";
                        estacao8.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString();
                        estacao8.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString();
                        estacao8.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString();
                        estacao8.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString();
                        estacao8.setContVeiculos(vei);
                    }
                }else if(sensor.contains("_9")){
                  if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString()+"ºC";
                        estacao9.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString()+"%";
                        estacao9.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString()+"Km/h";
                        estacao9.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString();
                        estacao9.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString();
                        estacao9.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString();
                        estacao9.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString();
                        estacao9.setContVeiculos(vei);
                    }
                }else if(sensor.contains("_10")&& !sensor.contains("_1027")&& !sensor.contains("_1079")){
                if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString()+"ºC";
                        estacao10.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString()+"%";
                        estacao10.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString()+"Km/h";
                        estacao10.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString();
                        estacao10.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString();
                        estacao10.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString();
                        estacao10.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString();
                        estacao10.setContVeiculos(vei);
                    }
                }else if(sensor.contains("_50")){
                 if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString()+"ºC";
                        estacao11.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString()+"%";
                        estacao11.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString()+"Km/h";
                        estacao11.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString();
                        estacao11.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString();
                        estacao11.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString();
                        estacao11.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString();
                        estacao11.setContVeiculos(vei);
                    }
                }else if(sensor.contains("_1027")){
                    if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString()+"ºC";
                        estacao12.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString()+"%";
                        estacao12.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString()+"Km/h";
                        estacao12.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString();
                        estacao12.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString();
                        estacao12.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString();
                        estacao12.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString();
                        estacao12.setContVeiculos(vei);
                    }
                }else if(sensor.contains("_1079")){
                    if(sensor.contains("Temperatura")){
                        String temp = solution.get("result").toString()+"ºC";
                        estacao13.setTemperatura(temp);
                    }else if(sensor.contains("Umidade")){
                        String umid = solution.get("result").toString()+"%";
                        estacao13.setUmidade(umid);
                    }else if(sensor.contains("Vento")){
                        String vent = solution.get("result").toString()+"Km/h";
                        estacao13.setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString();
                        estacao13.setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString();
                        estacao13.setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString();
                        estacao13.setNo2(no);
                    }else if(sensor.contains("Veiculos")){
                        String vei = solution.get("result").toString();
                        estacao13.setContVeiculos(vei);
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