package com.example.fabricio.drdespesa.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fabricio.drdespesa.OnCardClickListener;
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

public class MainActivity extends AppCompatActivity  {

    private MaterialCalendarView calendarView;
    private TextView txtSaudacao, txtSaldo, txtEmpty;
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
    private Movimentacao movimentacao;
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
        txtEmpty = findViewById(R.id.emptyContentMain);

        configuraCalendarView();
        swipe();

        //Configurar Adapter
        adapterMovimentacao = new AdapterMovimentacao( movimentacoes , this );

       // adapterMovimentacao.setOnCardClickListener (movimentacoes)

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

    //Configurar o ato de deslizar um item da RecyclerView para a lateral e ter a opção de excluir
    public void swipe(){

        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

                //Inativando o drag em drop (p todos os lados)
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;

                //Ativando o swipe (arrastar para os 2 lados)
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags( dragFlags, swipeFlags );
            }

            //Para trocar itens de posição na RecyclerView
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Log.i ("swipe", "Item foi arrastado");
                excluirMovimentacao( viewHolder );
            }
        };

        //Adicionando o swipe ao RecyclerView
        new ItemTouchHelper( itemTouch ).attachToRecyclerView( recyclerView );

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

    public void atualizarSaldo() {

        usuarioRef =  ConfiguracaoFirebase.getReferenciaFirebaseNoIdUsuario();

        if ( movimentacao.getTipo().equals( "r" ) ) {
            receitaTotal = receitaTotal - movimentacao.getValor();

            usuarioRef.child( "receitaTotal" ).setValue( receitaTotal );
        }

        if ( movimentacao.getTipo().equals( "d" ) ) {
            despesaTotal = despesaTotal - movimentacao.getValor();

            usuarioRef.child( "despesaTotal" ).setValue( despesaTotal );
        }

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

                if ( dataSnapshot == null || !dataSnapshot.exists() ) {
//                    Toast.makeText( MainActivity.this, "Nenhuma movimentação\nregistrada neste mês.",
//                            Toast.LENGTH_SHORT).show();


                    txtEmpty.setVisibility(View.VISIBLE);
                } else {
                    txtEmpty.setVisibility(View.GONE);
                }

                movimentacoes.clear();
                //Pata percorrer todos os filhos do anomes
                for ( DataSnapshot dados: dataSnapshot.getChildren() ) {
                    //Log.i( "dados", "retorno: " + dados.toString() );

                    Movimentacao movimentacao = dados.getValue( Movimentacao.class );
                    // Log.i( "dadosRetorno", "dados: " + movimentacao.getCategoria() );

                    //Recuperando a chave gerada pelo FirebaseDatabase ao salvar a movimentação
                    movimentacao.setKey( dados.getKey() );
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

    public void excluirMovimentacao(final RecyclerView.ViewHolder viewHolder) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder( this );

        //Configurando o AlertDialog
        alertDialog.setTitle("Excluir Movimentação da Conta");
        alertDialog.setMessage("Você tem certeza que deseja realmente excluir esta movimentação de sua conta?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                int position = viewHolder.getAdapterPosition();
                movimentacao = movimentacoes.get( position );

                FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                String emailUsuario = autenticacao.getCurrentUser().getEmail();
                String idUsuario = Base64Custom.codificarBase64(emailUsuario);
                movimentacaoRef = firebaseRef.child("movimentacao")
                        .child( idUsuario )
                        .child( anoMesSelecionado );

                movimentacaoRef.child( movimentacao.getKey() ).removeValue();

                adapterMovimentacao.notifyItemRemoved( position );

                atualizarSaldo();

                Toast.makeText( MainActivity.this, "Movimentação excluída",
                        Toast.LENGTH_LONG).show();
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Retornar o item movimentado para a lista
                adapterMovimentacao.notifyDataSetChanged();

                Toast.makeText( MainActivity.this, "Cancelado",
                        Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();

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

    public void startNewActivityWithObject(Movimentacao movimentacao) {
        //Inicia activity nova aqui

        Intent intent = new Intent(MainActivity.this,DetalheActivity.class);
        intent.putExtra("movimentacao", movimentacao);


        startActivity(intent);

        Toast.makeText(this, movimentacao.getDescricao(),
                Toast.LENGTH_SHORT).show();
    }

}
