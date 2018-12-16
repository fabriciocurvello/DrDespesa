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
import com.example.fabricio.drdespesa.helper.Base64Custom;
import com.example.fabricio.drdespesa.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private EditText etNome, etEmail, etSenha;
    private Button btCadastrar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        etNome = findViewById(R.id.editNomeCadastro);
        etEmail = findViewById(R.id.editEmailCadastro);
        etSenha = findViewById(R.id.editSenhaCadastro);
        btCadastrar = findViewById(R.id.buttonCadastrarCadastro);

        btCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoNome = etNome.getText().toString();
                String textoEmail = etEmail.getText().toString();
                String textoSenha = etSenha.getText().toString();

                //Validar se campos foram preenchidos
                boolean validacao = validarCampos(textoNome, textoEmail, textoSenha);
                if (validacao) {
                    usuario = new Usuario(textoNome, textoEmail, textoSenha);
                    //Toast.makeText(CadastroActivity.this, "onClick: " + usuario,
                    //        Toast.LENGTH_SHORT).show();
                    cadastrarUsuarioNoFirebase();
                }

            }
        });

    }

    public boolean validarCampos (String nome, String email, String senha) {
        if ( !nome.isEmpty() ) {
            if ( !email.isEmpty() ) {
                if ( !senha.isEmpty() ) {

                    //Senha deve ter pelo menos 8 dígitos
                    if ( senha.length() >= 8 ) {
                        return true;
                    } else {
                        Toast.makeText(CadastroActivity.this, "Senha precisa ter pelo menos 8 caracteres!",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                } else {
                    Toast.makeText(CadastroActivity.this, "Preencha a senha!",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(CadastroActivity.this, "Preencha o e-mail!",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(CadastroActivity.this, "Preencha o nome!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public void cadastrarUsuarioNoFirebase() {

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if ( task.isSuccessful() ) {

                    /*
                    Para salvar no FirebaseDatabase será utilizado o e-mail do usuário como
                    nó identificador. O FirebaseDatabase não aceita os caracteres do e-mail
                    como nó, por isso o e-mail será criptografado em Base64.
                     */
                    String idUsuario = Base64Custom.codificarBase64( usuario.getEmail() );
                    usuario.setIdUsuario( idUsuario );
                    usuario.salvarNoFirebaseDatabase();

                    Toast.makeText(CadastroActivity.this, "TESTE: " + usuario,
                            Toast.LENGTH_LONG).show();


                    Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar usuário!",
                            Toast.LENGTH_LONG).show();

                    //Encerra esta activity, retornando à anterior da pilha, que é a IntroActivity
                    finish();
                } else {

                    //Tratamento das exceções do FirebaseAuth
                    String excecao = "";
                    try {
                        throw task.getException();

                        //A senha exigida pelo FirebaseAuth deve ter pelo menos 6 dígitos
                    } catch ( FirebaseAuthWeakPasswordException e ) {
                        excecao = "Digite uma senha mais forte!";

                        //o e-mail deve ter @ e .
                    } catch ( FirebaseAuthInvalidCredentialsException e ) {
                        excecao = "Digite um e-mail válido!";

                        //Não é possível cadastrar o mesmo e-mail mais de uma vez
                    } catch ( FirebaseAuthUserCollisionException e ) {
                        excecao = "Esta conta já foi cadastrada";

                        //Para demais excecões genéricas
                    } catch (  Exception e ) {
                        excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                        //printar a exceção no log
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this, excecao,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
