package br.com.deliveryorganico.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.deliveryorganico.R;
import br.com.deliveryorganico.adapter.AdapterPedido;
import br.com.deliveryorganico.helper.ConfiguracaoFirebase;
import br.com.deliveryorganico.helper.UsuarioFirebase;
import br.com.deliveryorganico.listener.RecyclerItemClickListener;
import br.com.deliveryorganico.model.Pedido;
import dmax.dialog.SpotsDialog;

public class PedidosActivity extends AppCompatActivity {

  private final List<Pedido> pedidos = new ArrayList<>();
  private RecyclerView recyclerPedidos;
  private AdapterPedido adapterPedido;
  private AlertDialog dialog;
  private DatabaseReference firebaseRef;
  private String idEmpresa;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pedidos);

    //Configurações iniciais
    inicializarComponentes();
    firebaseRef = ConfiguracaoFirebase.getFirebase();
    idEmpresa = UsuarioFirebase.getIdUsuario();

    //Configuração Toolbar
    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle("Pedidos");
    setSupportActionBar(toolbar);
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    //Configura recyclerview
    recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
    recyclerPedidos.setHasFixedSize(true);
    adapterPedido = new AdapterPedido(pedidos);
    recyclerPedidos.setAdapter(adapterPedido);

    recuperarPedidos();

    //Adiciona evento de clique no recyclerview
    recyclerPedidos.addOnItemTouchListener(
            new RecyclerItemClickListener(
                    this,
                    recyclerPedidos,
                    new RecyclerItemClickListener.OnItemClickListener() {
                      @Override
                      public void onItemClick(View view, int position) {
                        // nao faz nada
                      }

                      @Override
                      public void onLongItemClick(View view, int position) {
                        Pedido pedido = pedidos.get(position);
                        pedido.setStatus("finalizado");
                        pedido.atualizarStatus();
                      }

                      @Override
                      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // nao faz nada
                      }
                    }
            )
    );

  }

  private void recuperarPedidos() {

    dialog = new SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Carregando dados")
            .setCancelable(false)
            .build();
    dialog.show();

    DatabaseReference pedidoRef = firebaseRef
            .child("pedidos")
            .child(idEmpresa);

    Query pedidoPesquisa = pedidoRef.orderByChild("status")
            .equalTo("confirmado");

    pedidoPesquisa.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        pedidos.clear();
        if (dataSnapshot.getValue() != null) {
          for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Pedido pedido = ds.getValue(Pedido.class);
            pedidos.add(pedido);
          }
          adapterPedido.notifyDataSetChanged();
          dialog.dismiss();
        }

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        // nao faz nada
      }
    });

  }

  private void inicializarComponentes() {
    recyclerPedidos = findViewById(R.id.recyclerPedidos);
  }

}
