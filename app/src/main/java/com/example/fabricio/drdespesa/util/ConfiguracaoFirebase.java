package com.example.fabricio.drdespesa.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {

    private static FirebaseAuth autenticacao;
    private static DatabaseReference referenciaFirebase;

    //retorna a instancia do FirebaseAuth
    public static FirebaseAuth getFirebaseAutenticacao() {
        if (autenticacao == null) {
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }

    //retorna a instancia do FirebaseDatabase
    public static DatabaseReference getFirebaseDatabase() {
        if (referenciaFirebase == null) {
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaFirebase;
    }
    //retorna a referência do FirebaseDatabase passando o nó do idUsuário
    public static DatabaseReference getReferenciaFirebaseNoIdUsuario() {
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = getFirebaseDatabase().child("usuarios").child(idUsuario);
        return usuarioRef;
}

}
