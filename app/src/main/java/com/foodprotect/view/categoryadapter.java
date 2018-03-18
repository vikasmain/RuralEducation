package com.foodprotect.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.foodprotect.R;
import com.foodprotect.model.category;

import java.util.List;



public class categoryadapter extends RecyclerView.Adapter<categoryadapter.ProductViewHolder> {


    private Context mCtx;
    private List<category> productList;

    public categoryadapter(Context mCtx, List<category> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.model_get, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        category product = productList.get(position);

        Glide.with(mCtx)
                .load(product.getImage())
                .into(holder.imageView);

        holder.textViewTitle.setText(product.getTitle());


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewShortDesc, textViewRating, textViewPrice;
        ImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.t12);

            imageView = itemView.findViewById(R.id.imageView1);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences sharedPreferences=mCtx.getSharedPreferences("Hellonakoli",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    String text=textViewTitle.getText().toString().toLowerCase();
                    editor.putString("categ",text);
                    editor.commit();
                    mCtx.startActivity(new Intent(mCtx,PostThirdActivity.class));
                }
            });
        }
    }
}