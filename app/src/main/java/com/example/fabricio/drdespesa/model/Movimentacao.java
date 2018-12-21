package com.example.fabricio.drdespesa.model;

import com.example.fabricio.drdespesa.util.Base64Custom;
import com.example.fabricio.drdespesa.util.ConfiguracaoFirebase;
import com.example.fabricio.drdespesa.util.DateCustom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Movimentacao implements Serializable {

    private String data;
    private String categoria;
    private String descricao;
    private String tipo;
    private double valor;
    private String key; //Chave gerada pelo FirebaseDatabase ao salvar a movimentação

    public Movimentacao() {
    }

    public Movimentacao(String data, String categoria, String descricao, String tipo, double valor) {
        this.data = data;
        this.categoria = categoria;
        this.descricao = descricao;
        this.tipo = tipo;
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void salvarMovimentacaoNoFirebaseDatabase(String dataEscolhida ) {

        //Para recuperar o e-mail do usuário e codificar como idUsuario
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String idUsuario = Base64Custom.codificarBase64( autenticacao.getCurrentUser().getEmail() );

        //Recebe o ano e mês da data escolhida pelo usuário
        String anoMes = DateCustom.anoMesDataEscolhida( dataEscolhida );

        //Obtedo a referência Firebase Database
        DatabaseReference firebaseDB = ConfiguracaoFirebase.getFirebaseDatabase();

        //Salvando no Firebase Database
        //push - Cria o identificador do Firebase
        firebaseDB.child( "movimentacao" )
                .child( idUsuario )
                .child( anoMes )
                .push()
                .setValue( this );
    }
}
