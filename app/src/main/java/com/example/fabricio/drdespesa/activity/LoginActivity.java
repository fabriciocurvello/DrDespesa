package com.example.fabricio.drdespesa.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fabricio.drdespesa.R;
import com.example.fabricio.drdespesa.config.ConfiguracaoFirebase;
import com.example.fabricio.drdespesa.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etSenha;
    private Button btEntrar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    etEmail = findViewById(R.id.editEmailLogin);
    etSenha = findViewById(R.id.editSenhaLogin);
    btEntrar = findViewById(R.id.buttonEntrarLogin);

    btEntrar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String textoEmail = etEmail.getText().toString();
            String textoSenha = etSenha.getText().toString();

            //Validar se campos foram preenchidos
            boolean validacao = validarCampos(textoEmail, textoSenha);
            if (validacao) {
                usuario = new Usuario( textoEmail, textoSenha);
                validarLoginNoFirebaseAuth();
            }
        }
    });

    }

    public boolean validarCampos (String email, String senha) {

            if ( !email.isEmpty() ) {
                if ( !senha.isEmpty() ) {

                    //Senha deve ter pelo menos 8 dígitos
                    if ( senha.length() >= 8 ) {
                        return true;
                    } else {
                        Toast.makeText(LoginActivity.this, "Senha precisa ter pelo menos 8 caracteres!",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Preencha a senha!",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(LoginActivity.this, "Preencha o e-mail!",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        public void validarLoginNoFirebaseAuth() {

            autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
            autenticacao.signInWithEmailAndPassword(
                    usuario.getEmail(),
                    usuario.getSenha()
            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if ( task.isSuccessful() ) {
                        Toast.makeText(LoginActivity.this, "Sucesso ao fazer login",
                                Toast.LENGTH_SHORT).show();

                    } else {

                        //Tratamento das exceções do FirebaseAuth
                        String excecao = "";
                        try {
                            throw task.getException();


                            //Verifica se o usuário está cadastrado
                        } catch ( FirebaseAuthInvalidUserException e ) {
                            excecao = "Usuário não está cadastrado.";

                            //Verifica se a senha digitada está correta
                        } catch ( FirebaseAuthInvalidCredentialsException e ) {
                            excecao = "E-mail e/ou senha não correspondem a um usuário cadastrado.";

                            //Para demais excecões genéricas
                        } catch (  Exception e ) {
                            excecao = "Erro ao acessar usuário: " + e.getMessage();
                            //printar a exceção no log
                            e.printStackTrace();
                        }

                        Toast.makeText(LoginActivity.this, excecao,
                                Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
}
