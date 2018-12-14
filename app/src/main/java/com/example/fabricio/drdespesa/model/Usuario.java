package com.example.fabricio.drdespesa.model;

public class Usuario {

    private String nome;
    private String email;
    private String senha;

    public Usuario() {
    }

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    //@androidx.annotation.NonNull
    @Override
    public String toString() {

        return super.toString() + "--- USUÁRIO - Nome: " + this.nome + " - E-mail: " + this.email + "\n";
    }
}
