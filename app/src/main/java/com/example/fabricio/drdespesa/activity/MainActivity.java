package com.example.fabricio.drdespesa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.fabricio.drdespesa.R;
import com.example.fabricio.drdespesa.model.Usuario;
import com.example.fabricio.drdespesa.util.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private TextView txtSaudacao, txtSaldo;
    private double despesaTotal = 0.0;
    private double receitaTotal = 0.0;
    private double resumoUsuario = 0.0;

    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Dr Despesa");
        setSupportActionBar(toolbar);

        txtSaldo = findViewById(R.id.textSaldoContentMain);
        txtSaudacao = findViewById(R.id.textSaudacaoContentMain);

        calendarView = findViewById(R.id.calendarViewContentMain);
        configuraCalendarView();

        recuperarResumo();


        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

    public void adicionarDespesa(View view) {
        startActivity(new Intent(MainActivity.this, DespesasActivity.class));
    }

    public void adicionarReceita(View view) {
        startActivity(new Intent(MainActivity.this, ReceitasActivity.class));
    }

    public void efetuarLogoff(View view) {
        logOff();
    }

    public void logOff() {
        //Efetua logoff no usuário FirebaseAuth
        FirebaseAuth autenticacao;
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signOut();

        //Abre IntroActivity
        startActivity(new Intent(MainActivity.this, IntroActivity.class));

        //Encerrar esta Activity
        finish();
    }

    public void configuraCalendarView () {

        CharSequence meses[] = { "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        calendarView.setTitleMonths( meses );

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {

            }
        });
    }

    public void recuperarResumo() {
       DatabaseReference usuarioRef =  ConfiguracaoFirebase.getReferenciaFirebaseNoIdUsuario();
       usuarioRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               Usuario usuario = dataSnapshot.getValue( Usuario.class );

               despesaTotal = usuario.getDespesaTotal();
               receitaTotal = usuario.getReceitaTotal();
               resumoUsuario = receitaTotal - despesaTotal;

               //Formatando o número decimal
               DecimalFormat decimalFormat = new DecimalFormat("R$ 0.00");
               String resultadoFormatado = decimalFormat.format( resumoUsuario );

               txtSaudacao.setText( "Olá, " + usuario.getNome() );
               txtSaldo.setText( resultadoFormatado );
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.menuSair :
                logOff();
                break;
        }

        //noinspection SimplifiableIfStatement
        //if (id == R.id.menuSair) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

}
