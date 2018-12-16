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
import com.example.fabricio.drdespesa.util.ConfiguracaoFirebase;
import com.example.fabricio.drdespesa.util.DateCustom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ReceitasActivity extends AppCompatActivity {

    private TextInputEditText etData, etCategoria, etDescricao;
    private EditText etValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private double receitaTotal; //Quanto está no DB até então
    private double receitaPreenchida; //Quanto o usuário está preenchendo como nova receita
    //private double receitaAtualizada; //Soma da receitaTotal com receitaPreenchida

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        etValor = findViewById(R.id.editValorReceitas);
        etData = findViewById(R.id.editDataReceitas);
        etCategoria = findViewById(R.id.editCategoriaReceitas);
        etDescricao = findViewById(R.id.editDescricaoReceitas);

        //Preenchendo etData com a data atual
        etData.setText( DateCustom.dataAtual() );

        recuperarReceitaTotal();
    }

    public void salvarReceita(View view) {

        if (validarCampos()) {
            movimentacao = new Movimentacao();
            String data = etData.getText().toString();
            movimentacao.setValor( Double.parseDouble( etValor.getText().toString() ) );
            movimentacao.setCategoria( etCategoria.getText().toString() );
            movimentacao.setDescricao( etDescricao.getText().toString() );
            movimentacao.setData( etData.getText().toString() );
            movimentacao.setTipo( "d" ); // d para Despesa, r para Receita

            //receitaPreenchida é o valor inserido pelo usuário
            //receitaTotal é o valor que já está salvo no FirebaseDatabase
            //receitaAtualizada é o valor da soma da receitaPreenchida com a receitaTotal
            receitaPreenchida = Double.parseDouble( etValor.getText().toString() );
            double receitaAtualizada = receitaTotal + receitaPreenchida;
            atualizarReceita( receitaAtualizada );

            movimentacao.salvarMovimentacaoNoFirebaseDatabase( data );
        }
    }

    public boolean validarCampos () {

        String mensagem = "";

        if ( !etValor.getText().toString().isEmpty() ) {
            if ( !etData.getText().toString().isEmpty()) {
                if ( !etCategoria.getText().toString().isEmpty() ) {
                    if ( !etDescricao.getText().toString().isEmpty() ) {

                        Toast.makeText(ReceitasActivity.this, "Dados válidos!",
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

        Toast.makeText(ReceitasActivity.this, mensagem,
                Toast.LENGTH_LONG).show();

        return false;
    }

    public void recuperarReceitaTotal() {

        DatabaseReference usuarioRef = ConfiguracaoFirebase.getReferenciaFirebaseNoIdUsuario();

        //Criando um ouvinte no nó que contém a despesa total
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue( Usuario.class );
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void atualizarReceita(double receita) {
        DatabaseReference usuarioRef = ConfiguracaoFirebase.getReferenciaFirebaseNoIdUsuario();
        usuarioRef.child( "receitaTotal" ).setValue(receita);
    }
}
