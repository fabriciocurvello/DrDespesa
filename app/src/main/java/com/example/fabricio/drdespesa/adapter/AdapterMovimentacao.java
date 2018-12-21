package com.example.fabricio.drdespesa.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fabricio.drdespesa.R;
import com.example.fabricio.drdespesa.activity.MainActivity;
import com.example.fabricio.drdespesa.model.Movimentacao;

import java.util.List;

public class AdapterMovimentacao extends RecyclerView.Adapter<AdapterMovimentacao.MyViewHolder> {

    List<Movimentacao> movimentacoes;
    Context context;

    MainActivity mainActivity;

//    public AdapterMovimentacao(List<Movimentacao> movimentacoes, Context context) {
//        this.movimentacoes = movimentacoes;
//        this.context = context;
//    }

    public AdapterMovimentacao( List<Movimentacao> movimentacoes, MainActivity mainActivity) {
       this.movimentacoes = movimentacoes;
       context = mainActivity;
       this.mainActivity = mainActivity;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_movimentacao, parent, false);
        return new MyViewHolder(itemLista);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Movimentacao movimentacao = movimentacoes.get(position);

        holder.titulo.setText(movimentacao.getDescricao());
        holder.valor.setText(String.valueOf(movimentacao.getValor()));
        holder.categoria.setText(movimentacao.getCategoria());
        //A cor padrão será a cor da Receita
        holder.valor.setTextColor(context.getResources().getColor(R.color.colorAccentReceita));

        if (movimentacao.getTipo().equals("d")) {
            holder.valor.setTextColor(context.getResources().getColor(R.color.colorAccentDespesa));
            //Quando a virar despesa a cor muda pra despesa
            holder.valor.setText("-" + movimentacao.getValor());
        }
    }


    @Override
    public int getItemCount() {
        return movimentacoes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titulo, valor, categoria;

        public MyViewHolder(View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.textAdapterTitulo);
            valor = itemView.findViewById(R.id.textAdapterValor);
            categoria = itemView.findViewById(R.id.textAdapterCategoria);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int index = getAdapterPosition();
                    Movimentacao movimentacao = movimentacoes.get(index);

                    //delegar para outro componente
                    mainActivity.startNewActivityWithObject(movimentacao);

                }
            });
        }

    }

}
