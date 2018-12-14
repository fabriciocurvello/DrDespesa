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
                    cadastrarUsuario();
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

    public void cadastrarUsuario () {

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if ( task.isSuccessful() ) {
                    Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar usuário!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CadastroActivity.this, "Erro ao cadastrar usuário!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
