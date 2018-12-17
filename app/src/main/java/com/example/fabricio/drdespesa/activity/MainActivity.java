package com.example.fabricio.drdespesa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.fabricio.drdespesa.R;
import com.example.fabricio.drdespesa.adapter.AdapterMovimentacao;
import com.example.fabricio.drdespesa.model.Movimentacao;
import com.example.fabricio.drdespesa.model.Usuario;
import com.example.fabricio.drdespesa.util.Base64Custom;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private TextView txtSaudacao, txtSaldo;
    private double despesaTotal = 0.0;
    private double receitaTotal = 0.0;
    private double resumoUsuario = 0.0;

    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListenerUsuario;
    private ValueEventListener valueEventListenerMovimentacoes;

    private RecyclerView recyclerView;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private DatabaseReference movimentacaoRef; // = ConfiguracaoFirebase.getFirebaseDatabase();
    private String anoMesSelecionado;

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
        recyclerView = findViewById(R.id.recyclerMovimentosContentMain);

        configuraCalendarView();

        //Configurar Adapter
        adapterMovimentacao = new AdapterMovimentacao( movimentacoes , this );

        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( this );
        recyclerView.setLayoutManager( layoutManager );
        recyclerView.setHasFixedSize( true );
        recyclerView.setAdapter( adapterMovimentacao );

    }

    //Acionado quando a Activity é iniciada ou reiniciada
    @Override
    protected void onStart() {
        super.onStart();

        recuperarResumo();
        recuperarMovimentacoes();
    }

    //Acionado quando a Activity é encerrada
    @Override
    protected void onStop() {
        super.onStop();

        //Para desativar o Listener do Firebase quando esta activity for encerrada
        usuarioRef.removeEventListener( valueEventListenerUsuario );
        movimentacaoRef.removeEventListener( valueEventListenerMovimentacoes );
        // Log.i( "Evento", "EventListener foi remvido ao encerrar MainActivity.");
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

        CalendarDay dataAtual = calendarView.getCurrentDate();
        //formatar os números dos meses sempre com 2 dígitos
        String mesSelecionado = String.format("%02d", (dataAtual.getMonth() + 1));
        anoMesSelecionado = dataAtual.getYear() + "" + mesSelecionado;

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView materialCalendarView, CalendarDay date) {

                String mesSelecionado = String.format("%02d", (date.getMonth() + 1));
                anoMesSelecionado = date.getYear() + "" + mesSelecionado;
                // Log.i( "MES", "mes: " + anoMesSelecionado );

                /*
                A cada mês que o usuário mudar será necessário recuperar as movientações.
                Só que o método recuperarMovimentacoes() cria um listener, então,
                para não ficar acumulando Listeners a cada mês selecionado,
                será necessário remover o Listener anterior.
                 */
                movimentacaoRef.removeEventListener( valueEventListenerMovimentacoes );
                recuperarMovimentacoes();
            }
        });
    }

    public void recuperarMovimentacoes() {

        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        movimentacaoRef = firebaseRef.child("movimentacao")
                                     .child( idUsuario )
                                     .child( anoMesSelecionado );

        //Log.i( "MES INICIO", "mes: " + anoMesSelecionado );

        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                movimentacoes.clear();
                //Pata percorrer todos os filhos do anomes
                for ( DataSnapshot dados: dataSnapshot.getChildren() ) {
                    //Log.i( "dados", "retorno: " + dados.toString() );

                    Movimentacao movimentacao = dados.getValue( Movimentacao.class );
                    // Log.i( "dadosRetorno", "dados: " + movimentacao.getCategoria() );
                    movimentacoes.add( movimentacao );
                }

                //notificar que os dados foram atualizados
                adapterMovimentacao.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void recuperarResumo() {
       usuarioRef =  ConfiguracaoFirebase.getReferenciaFirebaseNoIdUsuario();

        // Log.i( "Evento", "EventListener foi adicionado para recuperarResumo em MainActivity.");
       valueEventListenerUsuario = usuarioRef.addValueEventListener(new ValueEventListener() {
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
