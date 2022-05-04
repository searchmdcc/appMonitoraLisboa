package com.example.appmonitoralisboa.activities;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.appmonitoralisboa.R;
import com.example.appmonitoralisboa.activities.*;
import com.example.appmonitoralisboa.databinding.ActivityLocalMapsBinding;
import com.example.appmonitoralisboa.model.Tempo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLocalMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ProcessarLocais pl = new ProcessarLocais();
        pl.execute();
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
            for (int i = 0; i < ListaEstacoes.size(); i++) {
                Log.info("onPosExecuteMapa", "colocando marcações");
                LatLng coordenadas = new LatLng(ListaEstacoes.get(i).getLat(), ListaEstacoes.get(i).getLgt());
                mMap.addMarker(new MarkerOptions().position(coordenadas).title("Estação " + ListaEstacoes.get(i).getEst())).setSnippet("Rua: " + ListaEstacoes.get(i).getEndereco());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(coordenadas));
                Log.info("onPosExecuteMapa", "ok");
            }
        }
    }


        class ProcessarLocais extends AsyncTask<Void, Void, ResultSet> {
            //fuseki não suporta agregação avg criar iot-stream e consultá-lo
            @Override
            protected ResultSet doInBackground(Void... voids) {
                String consulta = "PREFIX sosa: <http://www.w3.org/ns/sosa/>\n" +
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns/>\n" +
                        "PREFIX ssn: <http://www.w3.org/ns/ssn/>\n" +
                        "PREFIX ex: <http://example.com/>\n" +
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema/>\n" +
                        "PREFIX owl: <http://www.w3.org/2002/07/owl/>\n" +
                        "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos/>\n" +
                        "\n" +
                        "select distinct ?lat ?long ?end ?est where{\n" +
                        "  ?sensor rdf:type ssn:Sensor;\n" +
                        "          ex:belongsStation ?estacao.\n" +
                        "  ?estacao rdfs:label ?est;\n" +
                        "  \t\t\towl:sameAs ?dataEst.\n" +
                        "  ?dataEst ex:hasAddress ?endereco.\n" +
                        "  ?endereco rdfs:label ?end;\n" +
                        "            geo:location ?loc.\n" +
                        "  ?loc geo:lat ?lat;\n" +
                        "       geo:long ?long.\n" +
                        "\n" +
                        "           \n" +
                        "     \n" +
                        "}";
                Query query = QueryFactory.create(consulta);
                QueryExecution qexec = QueryExecutionFactory.createServiceRequest("http://172.18.0.1:3030/MetadataSensors/query", query);
                ResultSet results = qexec.execSelect();
                if (results != null) {
                    Log.info("onPosExecuteMapa", "A consulta está retornando");
                }
                return results;
            }

            @Override
            protected void onPostExecute(ResultSet results) {
                super.onPostExecute(results);
                Estacoes estacoes = new Estacoes();


                while (results.hasNext()) {
                    QuerySolution solution = results.nextSolution();
                    String lat = solution.get("lat").toString().replace("http://example.com/", "");
                    estacoes.setLat(Double.valueOf(lat));
                    String lgt = solution.get("long").toString().replace("http://example.com/", "");
                    estacoes.setLgt(Double.valueOf(lgt));
                    String end = solution.get("end").toString().replace("http://example.com/", "");
                    estacoes.setEndereco(end);
                    String est = solution.get("est").toString().replace("http://example.com/", "");
                    estacoes.setEst(est);
                    ListaEstacoes.add(estacoes);
                    Log.info("onPosExecuteMapa", "adcionando");
                }
                marcarMapa();
                Log.info("onPosExecuteMapa", "lista criada");

            }
        }

}