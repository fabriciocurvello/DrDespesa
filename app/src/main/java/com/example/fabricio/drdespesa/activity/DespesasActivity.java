package com.example.fabricio.drdespesa.activity;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fabricio.drdespesa.R;
import com.example.fabricio.drdespesa.model.Movimentacao;
import com.example.fabricio.drdespesa.util.DateCustom;

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText etData, etCategoria, etDescricao;
    private EditText etValor;
    private Movimentacao movimentacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        etValor = findViewById(R.id.editValorDespesas);
        etData = findViewById(R.id.editDataDespesas);
        etCategoria = findViewById(R.id.editCategoriaDespesas);
        etDescricao = findViewById(R.id.editDescricaoDespesas);

        //Preenchendo etData com a data atual
        etData.setText( DateCustom.dataAtual() );

    }

    public void salvarDespesa(View view) {

        if (validarCampos()) {
            movimentacao = new Movimentacao();
            String data = etData.getText().toString();
            movimentacao.setValor( Double.parseDouble( etValor.getText().toString() ) );
            movimentacao.setCategoria( etCategoria.getText().toString() );
            movimentacao.setDescricao( etDescricao.getText().toString() );
            movimentacao.setData( etData.getText().toString() );
            movimentacao.setTipo( "d" ); // d para Despesa, r para Receita

            movimentacao.salvarMovimentacaoNoFirebaseDatabase( data );
        }

    }

    public boolean validarCampos () {

        String mensagem = "";

        if ( !etValor.getText().toString().isEmpty() ) {
            if ( !etData.getText().toString().isEmpty()) {
                if ( !etCategoria.getText().toString().isEmpty() ) {
                    if ( !etDescricao.getText().toString().isEmpty() ) {

                        Toast.makeText(DespesasActivity.this, "Dados válidos!",
                                Toast.LENGTH_SHORT).show();

                        return true;
                    } else {
                        mensagem = "Necessário inserir descrição";
                    }
                } else {
                    mensagem = "Necessário inserir categoria";
                }
            } else {
                mensagem = "Necessário inserir data";
            }
        } else {
            mensagem = "Necessário inserir valor";
        }

        Toast.makeText(DespesasActivity.this, mensagem,
                Toast.LENGTH_LONG).show();

        return false;
    }
}
