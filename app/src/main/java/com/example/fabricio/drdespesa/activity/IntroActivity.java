package com.example.fabricio.drdespesa.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.fabricio.drdespesa.R;
import com.example.fabricio.drdespesa.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class IntroActivity extends AppCompatActivity {

    FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

    }

    //Será executado tanto ao iniciar a Activity quanto ao retornar de outra que estava sobre esta na pilha
    @Override
    protected void onStart() {
        super.onStart();
        verificarUsuarioLogado();
    }

    //Aciona CadastroActivity ao clicar em btCadastrar
    public void btCadastrar(View view) {
        startActivity(new Intent(IntroActivity.this, CadastroActivity.class));
    }

    //Aciona LoginActivity ao clicar em btEntrar
    public void btEntrar(View view) {
        startActivity(new Intent(IntroActivity.this, LoginActivity.class));
    }

    //Caso o usuário já esteja logado em sua conta FirebaseAuth,
    //encaminha automaticamente para MainActivity
    public void verificarUsuarioLogado() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //Para testes. Efetua logoff no usuário FirebaseAuth
        //autenticacao.signOut();

        if ( autenticacao.getCurrentUser() != null ) {
            abrirMainActivity();
        }
    }

    public void abrirMainActivity() {
        startActivity(new Intent(IntroActivity.this, MainActivity.class));

        //Encerrar esta Activity
        finish();
    }


}
