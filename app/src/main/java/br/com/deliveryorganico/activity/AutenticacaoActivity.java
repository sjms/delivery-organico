package br.com.deliveryorganico.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import br.com.deliveryorganico.R;
import br.com.deliveryorganico.helper.ConfiguracaoFirebase;
import br.com.deliveryorganico.helper.UsuarioFirebase;

public class AutenticacaoActivity extends AppCompatActivity {

  private Button botaoAcessar;
  private EditText campoEmail;
  private EditText campoSenha;
  private Switch tipoAcesso;
  private Switch tipoUsuario;
  private LinearLayout linearTipoUsuario;

  private FirebaseAuth autenticacao;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FirebaseApp.initializeApp(this);
    setContentView(R.layout.activity_autenticacao);


    inicializaComponentes();
    autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

    //Verificar usuario logado
    verificarUsuarioLogado();

    tipoAcesso.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (isChecked) {//empresa
        linearTipoUsuario.setVisibility(View.VISIBLE);
      } else {//usuario
        linearTipoUsuario.setVisibility(View.GONE);
      }
    });

    botaoAcessar.setOnClickListener(v -> {

      String email = campoEmail.getText().toString();
      String senha = campoSenha.getText().toString();

      if (!email.isEmpty()) {
        if (!senha.isEmpty()) {

          //Verifica estado do switch
          if (tipoAcesso.isChecked()) {//Cadastro

            autenticacao.createUserWithEmailAndPassword(
                    email, senha
            ).addOnCompleteListener(task -> {

              if (task.isSuccessful()) {

                Toast.makeText(AutenticacaoActivity.this,
                        getString(R.string.cadastro_ok),
                        Toast.LENGTH_SHORT).show();

                String tipoUsuarioAutenticado = getTipoUsuario();
                UsuarioFirebase.atualizarTipoUsuario(tipoUsuarioAutenticado);
                abrirTelaPrincipal(tipoUsuarioAutenticado);

              } else {
                tratarErroCadastro(task);
              }

            });

          } else {//Login

            autenticacao.signInWithEmailAndPassword(
                    email, senha
            ).addOnCompleteListener(task -> {
              if (task.isSuccessful()) {

                Toast.makeText(AutenticacaoActivity.this,
                        "Logado com sucesso",
                        Toast.LENGTH_SHORT).show();
                String nomeUsuarioLogin = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getDisplayName();
                abrirTelaPrincipal(nomeUsuarioLogin);

              } else {
                tratarErroLogin(task);

              }
            });

          }

        } else {
          Toast.makeText(AutenticacaoActivity.this,
                  "Preencha a senha!",
                  Toast.LENGTH_SHORT).show();
        }
      } else {
        Toast.makeText(AutenticacaoActivity.this,
                "Preencha o E-mail!",
                Toast.LENGTH_SHORT).show();
      }

    });

  }

  private void tratarErroLogin(Task<AuthResult> task) {
    String erroExcecao = "";
    try {
      throw Objects.requireNonNull(task.getException());
    } catch (FirebaseAuthInvalidCredentialsException e) {
      erroExcecao = "O e-mail de login é inválido, favor confirme os dados";
    } catch (FirebaseAuthInvalidUserException e) {
      erroExcecao = "Não existe este usuário cadastrado, favor confirme os dados";
    } catch (Exception e) {
      erroExcecao = "ao fazer login: " + e.getMessage();
      e.printStackTrace();
    }
    Toast.makeText(AutenticacaoActivity.this,
            "Erro ao fazer login : " + erroExcecao,
            Toast.LENGTH_SHORT).show();
  }

  private void tratarErroCadastro(Task<AuthResult> task) {
    String erroExcecao = "";
    try {
      throw Objects.requireNonNull(task.getException());
    } catch (FirebaseAuthWeakPasswordException e) {
      erroExcecao = "Por favor, digite uma senha mais segura!";
    } catch (FirebaseAuthInvalidCredentialsException e) {
      erroExcecao = "Por favor, digite um e-mail válido.";
    } catch (FirebaseAuthUserCollisionException e) {
      erroExcecao = "Este conta já foi incluída anteriormente!";
    } catch (Exception e) {
      erroExcecao = "ao cadastrar usuário: " + e.getMessage();
      e.printStackTrace();
    }
    Toast.makeText(AutenticacaoActivity.this,
            "Erro: " + erroExcecao,
            Toast.LENGTH_SHORT).show();
  }

  private void verificarUsuarioLogado() {
    FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
    if (usuarioAtual != null) {
      String nomeUsuarioLogado = usuarioAtual.getDisplayName();
      abrirTelaPrincipal(nomeUsuarioLogado);
    }
  }

  private String getTipoUsuario() {
    return tipoUsuario.isChecked() ? getString(R.string.sigla_empresa) : getString(R.string.sigla_usuario);
  }

  private void abrirTelaPrincipal(String tipoUsuario) {
    if (getString(R.string.sigla_empresa).equals(tipoUsuario)) {//empresa
      startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
    } else {//usuario
      startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }
  }

  private void inicializaComponentes() {
    campoEmail = findViewById(R.id.editCadastroEmail);
    campoSenha = findViewById(R.id.editCadastroSenha);
    botaoAcessar = findViewById(R.id.buttonAcesso);
    tipoAcesso = findViewById(R.id.switchAcesso);
    tipoUsuario = findViewById(R.id.switchTipoUsuario);
    linearTipoUsuario = findViewById(R.id.linearTipoUsuario);
  }

}
