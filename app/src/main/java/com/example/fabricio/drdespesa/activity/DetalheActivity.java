package com.example.fabricio.drdespesa.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.fabricio.drdespesa.R;
import com.example.fabricio.drdespesa.model.Movimentacao;

public class DetalheActivity extends AppCompatActivity {

    private TextView tvTipo;
    private TextView tvValor;
    private TextView tvData;
    private TextView tvCategoria;
    private TextView tvDescricao;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        tvTipo = findViewById(R.id.textTipoDetalhe);
        tvValor = findViewById(R.id.textValorDetalhe);
        tvData = findViewById(R.id.textDataDetalhe);
        tvCategoria = findViewById(R.id.textCategoriaDetalhe);
        tvDescricao = findViewById(R.id.textDescricaoDetalhe);



        //Recuperar os dados enviados da MainActivity
        Movimentacao movimentacao = (Movimentacao) getIntent().getSerializableExtra("movimentacao");


        String tipo = movimentacao.getTipo();
        String valor = "" + movimentacao.getValor();
        String data = movimentacao.getData();
        String categoria = movimentacao.getCategoria();
        String descricao = movimentacao.getDescricao();

        if (tipo.equals("r")) {
            tipo = "Receita";
        } else {
            tipo = "Despesa";
        }

        tvTipo.setText(tipo);
        tvValor.setText("R$ " + valor);
        tvData.setText(data);
        tvCategoria.setText(categoria);
        tvDescricao.setText(descricao);

    }
}
