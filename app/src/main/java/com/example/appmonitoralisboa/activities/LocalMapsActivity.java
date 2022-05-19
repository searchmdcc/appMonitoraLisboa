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
            estacao7, estacao8, estacao9, estacao10, estacao11, estacao12;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocalMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        estacao1=new Estacoes();estacao2=new Estacoes();estacao3=new Estacoes();estacao4=new Estacoes();
        estacao5=new Estacoes();estacao6=new Estacoes();estacao7=new Estacoes();estacao8=new Estacoes();
        estacao9=new Estacoes();estacao10=new Estacoes();estacao11=new Estacoes();estacao12=new Estacoes();

        ProcessarMetadados pm = new ProcessarMetadados();
        pm.execute();
        ProcessarObservacoes po = new ProcessarObservacoes();
        po.execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void marcarMapa() {
        if (ListaEstacoes != null) {
            LatLng coordenadas = null;
            for (int i = 0; i < ListaEstacoes.size(); i++) {
                coordenadas = new LatLng(ListaEstacoes.get(i).getLat(), ListaEstacoes.get(i).getLgt());
                Log.info("Temperatura", ListaEstacoes.get(i).getDados().getTemperatura());
                mMap.addMarker(new MarkerOptions().position(coordenadas).title("Estação " + ListaEstacoes.get(i).getEst()))
                        .setSnippet("Rua: " + ListaEstacoes.get(i).getEndereco()+
                                "\nTemperatura: "+ListaEstacoes.get(i).getDados().getTemperatura()+
                "\nUmidade: "+ListaEstacoes.get(i).getDados().getUmidade()+
                        "\nVento: "+ListaEstacoes.get(i).getDados().getVento()+
                        "\nPM10: "+ListaEstacoes.get(i).getDados().getPm10()+
                        "\nPM25: "+ListaEstacoes.get(i).getDados().getPm25()+
                        "\nNO2: "+ListaEstacoes.get(i).getDados().getNo2()+
                        "\nSO2: "+ ListaEstacoes.get(i).getDados().getSo2()+
                        "\nO3: "+ListaEstacoes.get(i).getDados().getO3());
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
                    Context context = getApplicationContext();
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
            @Override
            protected ResultSet doInBackground(Void... voids) {
                String consulta = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                        "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                        "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                        "prefix ex: <http://example.com/>\n" +
                        "prefix geo:<http://www.w3.org/2003/01/geo/wgs84_pos/>\n" +
                        "select distinct ?est ?end ?lat ?long where{\n" +
                        " ?sensor rdf:type sosa:Sensor;\n" +
                        "\t\tex:belongsStation ?estacao.\n" +
                        " ?estacao rdfs:label ?est;\n" +
                        "\t\towl:sameAs ?est2.\n" +
                        "?est2 ex:hasAddress ?ender.\n" +
                        "?ender rdfs:label ?end;\n" +
                        "\t  geo:location ?ponto.\n" +
                        "?ponto geo:lat ?lat;\n" +
                        "\t   geo:long ?long.\n" +
                        "\n" +
                        "}limit 13";
                Query query = QueryFactory.create(consulta);
                QueryExecution qexec = QueryExecutionFactory.createServiceRequest("http://172.18.0.1:3030/Metadata_sensor/query", query);
                ResultSet results = qexec.execSelect();
                return results;
            }
            @Override
            protected void onPostExecute(ResultSet results) {
                super.onPostExecute(results);
                  while (results.hasNext()) {
                    QuerySolution solution = results.nextSolution();
                    String numEst = solution.get("est").toString().replace("http://example.com/", "");
                    if(numEst.equals("1")){
                           String lat = solution.get("lat").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao1.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao1.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao1.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao1.setEst(est);
                    }else if(numEst.equals("2")){
                        String lat = solution.get("lat").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao2.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao2.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao2.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao2.setEst(est);
                    }else if(numEst.equals("3")){
                        String lat = solution.get("lat").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao3.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao3.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao3.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao3.setEst(est);
                    }else if(numEst.equals("4")){
                        String lat = solution.get("lat").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao4.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao4.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao4.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao4.setEst(est);
                    }else if(numEst.equals("5")){
                        String lat = solution.get("lat").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao5.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao5.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao5.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao5.setEst(est);
                    }else if(numEst.equals("6")){
                        Log.info("Metadado 6", numEst);
                        String lat = solution.get("lat").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao6.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao6.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao6.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao6.setEst(est);
                    }else if(numEst.equals("7")){
                        String lat = solution.get("lat").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao7.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao7.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao7.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao7.setEst(est);
                    }else if(numEst.equals("8")){
                        String lat = solution.get("lat").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao8.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao8.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao8.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao8.setEst(est);
                    }else if(numEst.equals("9")){
                        String lat = solution.get("lat").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao9.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao9.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao9.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao9.setEst(est);
                    }else if(numEst.equals("10")){
                        String lat = solution.get("lat").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao10.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao10.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao10.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao10.setEst(est);
                     }else if(numEst.equals("11")){
                        String lat = solution.get("lat").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao11.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao11.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao11.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao11.setEst(est);
                    }else if(numEst.equals("12")){
                        String lat = solution.get("lat").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao12.setLat(Double.valueOf(lat));
                        String lgt = solution.get("long").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "");
                        estacao12.setLgt(Double.valueOf(lgt));
                        String end = solution.get("end").toString().replace("http://example.com/", "");
                        estacao12.setEndereco(end);
                        String est = solution.get("est").toString().replace("http://example.com/", "");
                        estacao12.setEst(est);
                    }
                }
            }
        }

    class ProcessarObservacoes extends AsyncTask<Void, Void, ResultSet> {
        @Override
        protected ResultSet doInBackground(Void... voids) {
            String consulta ="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                    "select ?result ?sensor where{ \n" +
                    "?obs rdf:type sosa:Observation;\n" +
                    "      sosa:hasSimpleResult ?result;\n" +
                    "      sosa:hasResultTime 202205101100;\n" +
                    "      sosa:madeBySensor ?sensor.\n" +
                    "} ";
            Query query = QueryFactory.create(consulta);
            QueryExecution qexec = QueryExecutionFactory.createServiceRequest("http://172.18.0.1:3030/Observation_sensor/query", query);
            ResultSet results = qexec.execSelect();
            return results;
        }
        @Override
        protected void onPostExecute(ResultSet results) {
            super.onPostExecute(results);
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                String sensor = solution.get("sensor").toString().replace("http://example.com/", "");
                if(sensor.contains("TEMP0001") || sensor.contains("HR0001") || sensor.contains("VI0001") || sensor.contains("PM100001") || sensor.contains("PM250001") ||
                        sensor.contains("SO20001") || sensor.contains("NO20001") || sensor.contains("O30001")){
                    if(sensor.contains("TEMP")){
                        String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                        temp=temp.replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao1.getDados().setTemperatura(temp);
                    }else if(sensor.contains("HR")){
                        String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                        estacao1.getDados().setUmidade(umid);
                    }else if(sensor.contains("VI")){
                        String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                        estacao1.getDados().setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao1.getDados().setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao1.getDados().setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao1.getDados().setNo2(no);
                    }else if(sensor.contains("SO2")){
                        String no2 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao1.getDados().setSo2(no2);
                    }else if(sensor.contains("O3")){
                        String o3 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao1.getDados().setO3(o3);
                    }
                }else if(sensor.contains("TEMP0002") || sensor.contains("HR0002") || sensor.contains("VI0002") || sensor.contains("PM100002") ||
                        sensor.contains("PM250002") || sensor.contains("SO20002") || sensor.contains("NO20002") || sensor.contains("O30002")){
                    if(sensor.contains("TEMP")){
                        String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                        temp=temp.replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao2.getDados().setTemperatura(temp);
                    }else if(sensor.contains("HR")){
                        String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                        estacao2.getDados().setUmidade(umid);
                    }else if(sensor.contains("VI")){
                        String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                        estacao2.getDados().setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao2.getDados().setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao2.getDados().setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao2.getDados().setNo2(no);
                    }else if(sensor.contains("SO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao2.getDados().setSo2(no);
                    }else if(sensor.contains("O3")) {
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao2.getDados().setO3(no);
                    }
                }else if(sensor.contains("TEMP0003") || sensor.contains("HR0003") || sensor.contains("VI0003") || sensor.contains("PM100003") ||
                        sensor.contains("PM250003") || sensor.contains("SO20003") || sensor.contains("NO20003") || sensor.contains("O30003")){
                    if(sensor.contains("TEMP")){
                        String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                        temp=temp.replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao3.getDados().setTemperatura(temp);
                    }else if(sensor.contains("HR")){
                        String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                        estacao3.getDados().setUmidade(umid);
                    }else if(sensor.contains("VI")){
                        String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                        estacao3.getDados().setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao3.getDados().setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao3.getDados().setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao3.getDados().setNo2(no);
                    }else if(sensor.contains("SO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao3.getDados().setSo2(no);
                    }else if(sensor.contains("O3")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao3.getDados().setO3(no);
                    }
                }else if(sensor.contains("TEMP0004") || sensor.contains("HR0004") || sensor.contains("VI0004") || sensor.contains("PM100004") ||
                        sensor.contains("PM250004") || sensor.contains("SO20004") || sensor.contains("NO20004") || sensor.contains("O30004")){
                    if(sensor.contains("TEMP")){
                        String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                        temp=temp.replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao4.getDados().setTemperatura(temp);
                    }else if(sensor.contains("HR")){
                        String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                        estacao4.getDados().setUmidade(umid);
                    }else if(sensor.contains("VI")){
                        String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                        estacao4.getDados().setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao4.getDados().setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao4.getDados().setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao4.getDados().setNo2(no);
                    }else if(sensor.contains("SO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao4.getDados().setSo2(no);
                    }else if(sensor.contains("O3")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao4.getDados().setO3(no);
                    }
                }else if(sensor.contains("TEMP0005") || sensor.contains("HR0005") || sensor.contains("VI0005") || sensor.contains("PM100005") ||
                        sensor.contains("PM250005") || sensor.contains("SO20005") || sensor.contains("NO20005") || sensor.contains("O30005")){
                    if(sensor.contains("TEMP")){
                        String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                        temp=temp.replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao5.getDados().setTemperatura(temp);
                    }else if(sensor.contains("HR")){
                        String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                        estacao5.getDados().setUmidade(umid);
                    }else if(sensor.contains("VI")){
                        String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                        estacao5.getDados().setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao5.getDados().setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao5.getDados().setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao5.getDados().setNo2(no);
                    }else if(sensor.contains("SO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao5.getDados().setSo2(no);
                    }else if(sensor.contains("O3")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao5.getDados().setO3(no);
                    }
                }else if(sensor.contains("TEMP0006") || sensor.contains("HR0006") || sensor.contains("VI0002") || sensor.contains("PM100006") ||
                        sensor.contains("PM250006") || sensor.contains("SO20006") || sensor.contains("NO20002") || sensor.contains("O30006")){
                    if(sensor.contains("TEMP")){
                        String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                        temp=temp.replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao6.getDados().setTemperatura(temp);
                    }else if(sensor.contains("HR")){
                        String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                        estacao6.getDados().setUmidade(umid);
                    }else if(sensor.contains("VI")){
                        String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                        estacao6.getDados().setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao6.getDados().setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao6.getDados().setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao6.getDados().setNo2(no);
                    }else if(sensor.contains("SO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao6.getDados().setSo2(no);
                    }else if(sensor.contains("O3")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao6.getDados().setO3(no);
                    }
                }else if(sensor.contains("TEMP0007") || sensor.contains("HR0007") || sensor.contains("VI0007") || sensor.contains("PM100007") ||
                        sensor.contains("PM250007") || sensor.contains("SO20007") || sensor.contains("NO20007") || sensor.contains("O30007")){
                   if(sensor.contains("TEMP")){
                       String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                       temp=temp.replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       estacao7.getDados().setTemperatura(temp);
                    }else if(sensor.contains("HR")){
                       String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                       estacao7.getDados().setUmidade(umid);
                    }else if(sensor.contains("VI")){
                       String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                       estacao7.getDados().setVento(vent);
                    }else if(sensor.contains("PM10")){
                       String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       estacao7.getDados().setPm10(p10);
                    }else if(sensor.contains("PM25")){
                       String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       estacao7.getDados().setPm25(p25);
                    }else if(sensor.contains("NO2")){
                       String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       estacao7.getDados().setNo2(no);
                    }else if(sensor.contains("SO2")){
                       String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       estacao7.getDados().setSo2(no);
                   }else if(sensor.contains("O3")){
                       String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       estacao7.getDados().setO3(no);
                   }
                }else if(sensor.contains("TEMP0008") || sensor.contains("HR0008") || sensor.contains("VI0008") || sensor.contains("PM100008") ||
                        sensor.contains("PM250008") || sensor.contains("SO20008") || sensor.contains("NO20008") || sensor.contains("O30008")){
                   if(sensor.contains("TEMP")){
                       String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                       temp=temp.replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       estacao8.getDados().setTemperatura(temp);
                    }else if(sensor.contains("HR")){
                       String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                       estacao8.getDados().setUmidade(umid);
                    }else if(sensor.contains("VI")){
                       String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                       estacao8.getDados().setVento(vent);
                    }else if(sensor.contains("PM10")){
                       String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       estacao8.getDados().setPm10(p10);
                    }else if(sensor.contains("PM25")){
                       String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       estacao8.getDados().setPm25(p25);
                    }else if(sensor.contains("NO2")){
                       String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       estacao8.getDados().setNo2(no);
                    }else if(sensor.contains("SO2")){
                       String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       estacao8.getDados().setSo2(no);
                   }else if(sensor.contains("O3")){
                       String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                       estacao8.getDados().setO3(no);
                   }
                }else if(sensor.contains("TEMP0009") || sensor.contains("HR0009") || sensor.contains("VI0009") || sensor.contains("PM100009") ||
                        sensor.contains("PM250009") || sensor.contains("SO20009") || sensor.contains("NO20009") || sensor.contains("O30009")){
                  if(sensor.contains("TEMP")){
                        String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                      temp=temp.replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                      estacao9.getDados().setTemperatura(temp);
                    }else if(sensor.contains("HR")){
                      String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                      estacao9.getDados().setUmidade(umid);
                    }else if(sensor.contains("VI")){
                      String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                      estacao9.getDados().setVento(vent);
                    }else if(sensor.contains("PM10")){
                      String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                      estacao9.getDados().setPm10(p10);
                    }else if(sensor.contains("PM25")){
                      String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                      estacao9.getDados().setPm25(p25);
                    }else if(sensor.contains("NO2")){
                      String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                      estacao9.getDados().setNo2(no);
                    }else if(sensor.contains("SO2")){
                      String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                      estacao9.getDados().setSo2(no);
                  }else if(sensor.contains("O3")){
                      String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                      estacao9.getDados().setO3(no);
                  }
                }else if(sensor.contains("TEMP0010") || sensor.contains("HR0010") || sensor.contains("VI0010") || sensor.contains("PM100010") ||
                        sensor.contains("PM250010") || sensor.contains("SO20010") || sensor.contains("NO20010") || sensor.contains("O30010")){
                if(sensor.contains("TEMP")){
                    String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                    temp=temp.replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                    estacao10.getDados().setTemperatura(temp);
                    }else if(sensor.contains("HR")){
                        String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                    estacao10.getDados().setUmidade(umid);
                    }else if(sensor.contains("VI")){
                        String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                    estacao10.getDados().setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                    estacao10.getDados().setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                    estacao10.getDados().setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                    estacao10.getDados().setNo2(no);
                    }else if(sensor.contains("SO2")){
                    String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                    estacao10.getDados().setSo2(no);
                }else if(sensor.contains("O3")){
                    String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                    estacao10.getDados().setO3(no);
                }
                }else if(sensor.contains("TEMP0011") || sensor.contains("HR0011") || sensor.contains("VI0011") || sensor.contains("PM100011") ||
                        sensor.contains("PM250011") || sensor.contains("SO20011") || sensor.contains("NO20011") || sensor.contains("O30011")){
                 if(sensor.contains("TEMP")){
                     String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                     temp=temp.replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                     estacao11.getDados().setTemperatura(temp);
                    }else if(sensor.contains("HR")){
                        String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                     estacao11.getDados().setUmidade(umid);
                    }else if(sensor.contains("VI")){
                        String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                     estacao11.getDados().setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                     estacao11.getDados().setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                     estacao11.getDados().setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                     estacao11.getDados().setNo2(no);
                    }else if(sensor.contains("SO2")){
                     String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                     estacao11.getDados().setSo2(no);
                 }else if(sensor.contains("O3")){
                     String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                     estacao11.getDados().setO3(no);
                 }
                }else if(sensor.contains("TEMP0012") || sensor.contains("HR0012") || sensor.contains("VI0012") || sensor.contains("PM100012") ||
                        sensor.contains("PM250012") || sensor.contains("SO20012") || sensor.contains("NO20012") || sensor.contains("O30012")){
                    if(sensor.contains("TEMP")){
                        String temp = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"ºC";
                        temp=temp.replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao12.getDados().setTemperatura(temp);
                    }else if(sensor.contains("HR")){
                        String umid = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"%";
                        estacao12.getDados().setUmidade(umid);
                    }else if(sensor.contains("VI")){
                        String vent = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#decimal", "")+"Km/h";
                        estacao12.getDados().setVento(vent);
                    }else if(sensor.contains("PM10")){
                        String p10 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao12.getDados().setPm10(p10);
                    }else if(sensor.contains("PM25")){
                        String p25 = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao12.getDados().setPm25(p25);
                    }else if(sensor.contains("NO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao12.getDados().setNo2(no);
                    }else if(sensor.contains("SO2")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao12.getDados().setSo2(no);
                    }else if(sensor.contains("O3")){
                        String no = solution.get("result").toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
                        estacao12.getDados().setO3(no);
                    }
                }
            }
            ListaEstacoes.add(estacao1);ListaEstacoes.add(estacao2);ListaEstacoes.add(estacao3);ListaEstacoes.add(estacao4);ListaEstacoes.add(estacao5);
            ListaEstacoes.add(estacao6);ListaEstacoes.add(estacao7);ListaEstacoes.add(estacao8);ListaEstacoes.add(estacao9);ListaEstacoes.add(estacao10);
            ListaEstacoes.add(estacao11);ListaEstacoes.add(estacao12);
            marcarMapa();
        }
    }
}