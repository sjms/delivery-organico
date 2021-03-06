package br.com.deliveryorganico.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import br.com.deliveryorganico.R;

public class SplashActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    new Handler(Looper.getMainLooper()).postDelayed(this::abrirAutenticacao, 3000);

  }

  private void abrirAutenticacao() {
    Intent i = new Intent(SplashActivity.this, AutenticacaoActivity.class);
    startActivity(i);
    finish();
  }

}
