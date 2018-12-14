package com.example.fabricio.drdespesa.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.fabricio.drdespesa.R;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
    }

    //Aciona CadastroActivity ao clicar em btCadastrar
    public void btCadastrar(View view) {
        startActivity(new Intent(IntroActivity.this, CadastroActivity.class));
    }

    //Aciona LoginActivity ao clicar em btEntrar
    public void btEntrar(View view) {
        startActivity(new Intent(IntroActivity.this, LoginActivity.class));
    }
}
