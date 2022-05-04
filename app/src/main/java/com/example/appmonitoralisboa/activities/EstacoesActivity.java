package com.example.appmonitoralisboa.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.appmonitoralisboa.R;
import com.example.appmonitoralisboa.model.Estacoes;

import java.util.ArrayList;

public class EstacoesActivity extends AppCompatActivity {
    private ArrayList<Estacoes> listaEstacoes = new ArrayList<Estacoes>();
    private RadioGroup rd_grupo;
    private RadioButton rd_todos;
    private RadioButton rd_especifico;
    private EditText escolha_est;
    private Button buscarDados, acessarMapa;
    private TextView textoEstacoes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estacoes);
        rd_grupo = findViewById(R.id.rd_grupo);
        rd_todos=findViewById(R.id.rd_todos);
        rd_especifico=findViewById(R.id.rd_especifica);
        escolha_est = findViewById(R.id.editNum_est);
        buscarDados = findViewById(R.id.btnBuscar_est);
        acessarMapa = findViewById(R.id.btnMapa);
        textoEstacoes = findViewById(R.id.txtEstacoes);

        rd_especifico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                escolha_est.setVisibility(View.VISIBLE);
                textoEstacoes.setVisibility(View.VISIBLE);

            }
        });
        rd_todos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                escolha_est.setVisibility(View.INVISIBLE);
                textoEstacoes.setText("");
                textoEstacoes.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void buscarDadosEstacoes(){

    }

}