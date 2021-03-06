package br.com.deliveryorganico.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.deliveryorganico.R;
import br.com.deliveryorganico.model.Empresa;


public class AdapterEmpresa extends RecyclerView.Adapter<AdapterEmpresa.MyViewHolder> {

  private final List<Empresa> empresas;
  private final  Context context;

  public AdapterEmpresa(List<Empresa> empresas, Context context) {
    this.empresas = empresas;
    this.context = context;
  }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
    View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_empresa, parent, false);
    return new MyViewHolder(itemLista);
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
    Empresa empresa = empresas.get(i);

    holder.nomeEmpresa.setText(empresa.getNome());
    holder.categoria.setText(empresa.getCategoria().concat(context.getString(R.string.separador)));
    holder.entrega.setText(context.getString(R.string.simbolo_reais).concat(empresa.getPrecoEntrega().toString()));
    holder.tempo.setText(empresa.getTempo().concat(context.getString(R.string.sigla_min)));

    //Carregar imagem
    String urlImagem = empresa.getUrlImagem();
    if (urlImagem == null || urlImagem.isEmpty()) {
      Picasso.get().load(R.drawable.perfil).into(holder.imagemEmpresa);
    } else {
      Picasso.get().load(urlImagem).into(holder.imagemEmpresa);
    }


  }

  @Override
  public int getItemCount() {
    return empresas.size();
  }

  public static class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView imagemEmpresa;
    TextView nomeEmpresa;
    TextView categoria;
    TextView tempo;
    TextView entrega;

    public MyViewHolder(View itemView) {
      super(itemView);

      nomeEmpresa = itemView.findViewById(R.id.textNomeEmpresa);
      categoria = itemView.findViewById(R.id.textCategoriaEmpresa);
      tempo = itemView.findViewById(R.id.textTempoEmpresa);
      entrega = itemView.findViewById(R.id.textEntregaEmpresa);
      imagemEmpresa = itemView.findViewById(R.id.imageEmpresa);

    }
  }
}
