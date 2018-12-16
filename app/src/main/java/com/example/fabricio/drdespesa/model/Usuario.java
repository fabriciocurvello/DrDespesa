package com.example.fabricio.drdespesa.model;

import android.widget.Toast;

import com.example.fabricio.drdespesa.activity.CadastroActivity;
import com.example.fabricio.drdespesa.activity.IntroActivity;
import com.example.fabricio.drdespesa.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class Usuario {

    private String idUsuario; //é o e-mail do usuário criptografado em Base64
    private String nome;
    private String email;
    private String senha;

    public Usuario() {
    }

    //Utilizado para validar login no FirebaseAuth
    public Usuario(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    //Utilizado para cadastrar o usuário no FirebaseAuth
    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    /*
    @Exclude remove este parâmetro do this quando acionado ao FirebaseDatabase.
    Foi colocado para não salvar o idUsuário ao passar this
    dentro do método salvarNoFirebaseDatabase()
    */
    @Exclude
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void salvarNoFirebaseDatabase(){
        DatabaseReference firebaseDB = ConfiguracaoFirebase.getFirebaseDatabase();
        firebaseDB.child( "usuarios" ).child( this.idUsuario ).setValue( this );
        }


    //@androidx.annotation.NonNull
    @Override
    public String toString() {

        return "USUÁRIO - Nome: " + this.nome + "idUsuario: " + this.idUsuario + " - E-mail: " + this.email + "\n" + super.toString();
    }
}
