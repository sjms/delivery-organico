package br.com.deliveryorganico.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.deliveryorganico.R;
import br.com.deliveryorganico.adapter.AdapterProduto;
import br.com.deliveryorganico.helper.ConfiguracaoFirebase;
import br.com.deliveryorganico.helper.UsuarioFirebase;
import br.com.deliveryorganico.listener.RecyclerItemClickListener;
import br.com.deliveryorganico.model.Empresa;
import br.com.deliveryorganico.model.ItemPedido;
import br.com.deliveryorganico.model.Pedido;
import br.com.deliveryorganico.model.Produto;
import br.com.deliveryorganico.model.Usuario;
import dmax.dialog.SpotsDialog;

public class CardapioActivity extends AppCompatActivity {

  private final List<Produto> produtos = new ArrayList<>();
  private RecyclerView recyclerProdutosCardapio;
  private ImageView imageEmpresaCardapio;
  private TextView textNomeEmpresaCardapio;
  private AlertDialog dialog;
  private TextView textCarrinhoQtd;
  private TextView textCarrinhoTotal;
  private AdapterProduto adapterProduto;
  private List<ItemPedido> itensCarrinho = new ArrayList<>();
  private DatabaseReference firebaseRef;
  private String idEmpresa;
  private String idUsuarioLogado;
  private Usuario usuario;
  private Pedido pedidoRecuperado;
  private int qtdItensCarrinho;
  private Double totalCarrinho;
  private int metodoPagamento;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cardapio);

    //Configurações iniciais
    inicializarComponentes();
    firebaseRef = ConfiguracaoFirebase.getFirebase();
    idUsuarioLogado = UsuarioFirebase.getIdUsuario();

    //Recuperar empresa selecionada
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      Empresa empresaSelecionada = (Empresa) bundle.getSerializable("empresa");

      textNomeEmpresaCardapio.setText(empresaSelecionada.getNome());
      idEmpresa = empresaSelecionada.getIdUsuario();

      String url = empresaSelecionada.getUrlImagem();

      if (url == null || url.isEmpty()) {
        Picasso.get().load(R.drawable.pedido).into(imageEmpresaCardapio);
      } else {
        Picasso.get().load(url).into(imageEmpresaCardapio);
      }


    }

    //Configurações Toolbar
    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle("Cardápio");
    setSupportActionBar(toolbar);
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    //Configura recyclerview
    recyclerProdutosCardapio.setLayoutManager(new LinearLayoutManager(this));
    recyclerProdutosCardapio.setHasFixedSize(true);
    adapterProduto = new AdapterProduto(produtos, this);
    recyclerProdutosCardapio.setAdapter(adapterProduto);

    //Configurar evento de clique
    recyclerProdutosCardapio.addOnItemTouchListener(
            new RecyclerItemClickListener(
                    this,
                    recyclerProdutosCardapio,
                    new RecyclerItemClickListener.OnItemClickListener() {
                      @Override
                      public void onItemClick(View view, int position) {
                        confirmarQuantidade(position);
                      }

                      @Override
                      public void onLongItemClick(View view, int position) {
                        //nao faz nada
                      }

                      @Override
                      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // nao faz nada
                      }
                    }
            )
    );

    //Recupera produtos para empresa
    recuperarProdutos();
    recuperarDadosUsuario();

  }

  private void confirmarQuantidade(final int posicao) {

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Quantidade");
    builder.setMessage("Digite a quantidade");

    final EditText editQuantidade = new EditText(this);
    editQuantidade.setText("1");

    builder.setView(editQuantidade);

    builder.setPositiveButton("Confirmar", (dialog, which) -> {

      String quantidade = editQuantidade.getText().toString();

      Produto produtoSelecionado = produtos.get(posicao);
      ItemPedido itemPedido = new ItemPedido();
      itemPedido.setIdProduto(produtoSelecionado.getIdProduto());
      itemPedido.setNomeProduto(produtoSelecionado.getNome());
      itemPedido.setPreco(produtoSelecionado.getPreco());
      itemPedido.setQuantidade(Integer.parseInt(quantidade));

      itensCarrinho.add(itemPedido);

      if (pedidoRecuperado == null) {
        pedidoRecuperado = new Pedido(idUsuarioLogado, idEmpresa);
      }

      pedidoRecuperado.setNome(usuario.getNome());
      pedidoRecuperado.setEndereco(usuario.getEndereco());
      pedidoRecuperado.setItens(itensCarrinho);
      pedidoRecuperado.salvar();


    });

    builder.setNegativeButton("Cancelar", (dialog, which) -> {

    });
    AlertDialog alertDialog = builder.create();
    alertDialog.show();

  }

  private void recuperarDadosUsuario() {

    dialog = new SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Carregando dados")
            .setCancelable(false)
            .build();
    dialog.show();

    DatabaseReference usuariosRef = firebaseRef
            .child("usuarios")
            .child(idUsuarioLogado);

    usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() != null) {
          usuario = dataSnapshot.getValue(Usuario.class);
        }
        recuperPedido();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        // nao faz nada
      }
    });

  }

  private void recuperPedido() {

    DatabaseReference pedidoRef = firebaseRef
            .child("pedidos_usuario")
            .child(idEmpresa)
            .child(idUsuarioLogado);

    pedidoRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        qtdItensCarrinho = 0;
        totalCarrinho = 0.0;
        itensCarrinho = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {

          pedidoRecuperado = dataSnapshot.getValue(Pedido.class);

          if (pedidoRecuperado != null) {
            itensCarrinho = pedidoRecuperado.getItens();
            if (itensCarrinho != null) {
              for (ItemPedido itemPedido : itensCarrinho) {
                int quantidade = itemPedido.getQuantidade();
                Double preco = itemPedido.getPreco();
                totalCarrinho += (quantidade * preco);
                qtdItensCarrinho += quantidade;
              }
            }
          }

        }

        DecimalFormat df = new DecimalFormat(getString(R.string.formato_decimal));

        textCarrinhoQtd.setText(getString(R.string.quantidade_abreviado).concat(String.valueOf(qtdItensCarrinho)));
        textCarrinhoTotal.setText(getString(R.string.simbolo_reais).concat(df.format(totalCarrinho)));

        dialog.dismiss();

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        //nao faz nada
      }

    });


  }

  private void recuperarProdutos() {

    DatabaseReference produtosRef = firebaseRef
            .child("produtos")
            .child(idEmpresa);

    produtosRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        produtos.clear();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
          produtos.add(ds.getValue(Produto.class));
        }

        adapterProduto.notifyDataSetChanged();

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        // nao faz nada
      }
    });

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_cardapio, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == R.id.menuPedido) {
      confirmarPedido();
    }

    return super.onOptionsItemSelected(item);
  }

  private void confirmarPedido() {

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Selecione um método de pagamento");

    CharSequence[] itens = new CharSequence[]{
            "Dinheiro", "Máquina de cartão"
    };

    builder.setSingleChoiceItems(itens, 0, (dialog, which) -> metodoPagamento = which);

    final EditText editObservacao = new EditText(this);
    editObservacao.setHint("Adicione uma observação caso deseje");
    builder.setView(editObservacao);

    builder.setPositiveButton("Confirmar", (dialog, which) -> {

      String observacao = editObservacao.getText().toString();
      pedidoRecuperado.setMetodoPagamento(metodoPagamento);
      pedidoRecuperado.setObservacao(observacao);
      pedidoRecuperado.setStatus("confirmado");
      pedidoRecuperado.confimar();
      pedidoRecuperado.remover();
      pedidoRecuperado = null;

    });

    builder.setNegativeButton("Cancelar", (dialog, which) -> {
    });

    AlertDialog alertDialog = builder.create();
    alertDialog.show();

  }

  private void inicializarComponentes() {
    recyclerProdutosCardapio = findViewById(R.id.recyclerProdutosCardapio);
    imageEmpresaCardapio = findViewById(R.id.imageEmpresaCardapio);
    textNomeEmpresaCardapio = findViewById(R.id.textNomeEmpresaCardapio);

    textCarrinhoQtd = findViewById(R.id.textCarrinhoQtd);
    textCarrinhoTotal = findViewById(R.id.textCarrinhoTotal);

  }

}
