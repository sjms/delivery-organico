package br.com.deliveryorganico.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.deliveryorganico.R;
import br.com.deliveryorganico.model.ItemPedido;
import br.com.deliveryorganico.model.Pedido;


public class AdapterPedido extends RecyclerView.Adapter<AdapterPedido.MyViewHolder> {

  private final List<Pedido> pedidos;

  private static final String ENDERECO = "Endereço: ";
  private static final String OBS = "Obs.: ";
  private static final String TIPO_PAGAMENTO = "Meio de pagamento: ";
  private static final String DINHEIRO = "Dinheiro";
  private static final String CARTAO = "Máquina de cartão";

  public AdapterPedido(List<Pedido> pedidos) {
    this.pedidos = pedidos;
  }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
    View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pedidos, parent, false);
    return new MyViewHolder(itemLista);
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

    Pedido pedido = pedidos.get(i);
    holder.nome.setText(pedido.getNome());
    holder.endereco.setText(ENDERECO.concat(pedido.getEndereco()));
    holder.observacao.setText(OBS.concat(pedido.getObservacao()));

    List<ItemPedido> itens = pedido.getItens();
    StringBuilder descricaoItens = new StringBuilder();

    int numeroItem = 1;
    double total = 0.0;

    for (ItemPedido itemPedido : itens) {
      System.out.println("aaaa" + itemPedido.getNomeProduto());
      int quantidadeItems = itemPedido.getQuantidade();
      Double preco = itemPedido.getPreco();
      total += (quantidadeItems * preco);

      String nome = itemPedido.getNomeProduto();
      descricaoItens.append(numeroItem).append(") ")
              .append(nome).append(": (")
              .append(quantidadeItems)
              .append(" x R$ ")
              .append(preco)
              .append(") \n");
      numeroItem++;
    }
    descricaoItens.append("Total: R$ ").append(total);

    holder.itens.setText(descricaoItens);

    int metodoPagamento = pedido.getMetodoPagamento();
    String pagamento = metodoPagamento == 0 ? DINHEIRO : CARTAO;
    holder.pagamento.setText(TIPO_PAGAMENTO.concat(pagamento));

  }

  @Override
  public int getItemCount() {
    return pedidos.size();
  }

  public static class MyViewHolder extends RecyclerView.ViewHolder {

    TextView nome;
    TextView endereco;
    TextView pagamento;
    TextView observacao;
    TextView itens;

    public MyViewHolder(View itemView) {
      super(itemView);

      nome = itemView.findViewById(R.id.textPedidoNome);
      endereco = itemView.findViewById(R.id.textPedidoEndereco);
      pagamento = itemView.findViewById(R.id.textPedidoPgto);
      observacao = itemView.findViewById(R.id.textPedidoObs);
      itens = itemView.findViewById(R.id.textPedidoItens);

    }
  }

}
