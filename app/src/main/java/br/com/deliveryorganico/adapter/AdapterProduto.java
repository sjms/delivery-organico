package br.com.deliveryorganico.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

import br.com.deliveryorganico.R;
import br.com.deliveryorganico.model.Produto;


public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder> {

  private final List<Produto> produtos;
  private final Context context;

  public AdapterProduto(List<Produto> produtos, Context context) {
    this.produtos = produtos;
    this.context = context;
  }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
    View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto, parent, false);
    return new MyViewHolder(itemLista);
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
    Produto produto = produtos.get(i);
    holder.nome.setText(produto.getNome());
    holder.descricao.setText(produto.getDescricao());
    DecimalFormat df = new DecimalFormat(context.getString(R.string.formato_decimal));
    holder.valor.setText(context.getString(R.string.simbolo_reais).concat(df.format(produto.getPreco())));
  }

  @Override
  public int getItemCount() {
    return produtos.size();
  }

  public static class MyViewHolder extends RecyclerView.ViewHolder {
    TextView nome;
    TextView descricao;
    TextView valor;

    public MyViewHolder(View itemView) {
      super(itemView);

      nome = itemView.findViewById(R.id.textNomeRefeicao);
      descricao = itemView.findViewById(R.id.textDescricaoRefeicao);
      valor = itemView.findViewById(R.id.textPreco);
    }
  }


}
