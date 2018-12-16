package com.example.fabricio.drdespesa.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fabricio.drdespesa.R;
import com.example.fabricio.drdespesa.model.Movimentacao;
import com.example.fabricio.drdespesa.model.Usuario;
import com.example.fabricio.drdespesa.util.Base64Custom;
import com.example.fabricio.drdespesa.util.ConfiguracaoFirebase;
import com.example.fabricio.drdespesa.util.DateCustom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText etData, etCategoria, etDescricao;
    private EditText etValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private double despesaTotal; //Quanto está no DB até então
    private double despesaPreenchida; //Quanto o usuário está preenchendo como nova despesa
    //private double despesaAtualizada; //Soma da despesaTotal com despesaPreenchida

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

        recuperarDespesaTotal();

    }

    public void salvarDespesa(View view) {

        if (validarCampos()) {
            movimentacao = new Movimentacao();
            String data = etData.getText().toString();
            movimentacao.setValor( Double.parseDouble( etValor.getText().toString() ) );
            movimentacao.setCategoria( etCategoria.getText().toString() );
            movimentacao.setDescricao( etDescricao.getText().toString() );
            movimentacao.setData( etData.getText().toString() );
            movimentacao.setTipo( "r" ); // d para Despesa, r para Receita

            //despesaPreenchida é o valor inserido pelo usuário
            //despesaTotal é o valor que já está salvo no FirebaseDatabase
            //despesaAtualizada é o valor da soma da despesaPreenchida com a despesaTotal
            despesaPreenchida = Double.parseDouble( etValor.getText().toString() );
            double despesaAtualizada = despesaTotal + despesaPreenchida;
            atualizarDespesa( despesaAtualizada );

            movimentacao.salvarMovimentacaoNoFirebaseDatabase( data );

            Toast.makeText(DespesasActivity.this, "Despesa adicionada com sucesso!",
                    Toast.LENGTH_SHORT).show();

            //encerrar esta activity
            finish();
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

    public void recuperarDespesaTotal() {

        DatabaseReference usuarioRef = ConfiguracaoFirebase.getReferenciaFirebaseNoIdUsuario();

        //Criando um ouvinte no nó que contém a despesa total
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue( Usuario.class );
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void atualizarDespesa(double despesa) {
        DatabaseReference usuarioRef = ConfiguracaoFirebase.getReferenciaFirebaseNoIdUsuario();
        usuarioRef.child( "despesaTotal" ).setValue(despesa);

    }
}
