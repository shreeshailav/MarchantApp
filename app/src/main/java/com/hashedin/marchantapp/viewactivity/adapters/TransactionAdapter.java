package com.hashedin.marchantapp.viewactivity.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
 import androidx.recyclerview.widget.RecyclerView;

import com.hashedin.marchantapp.BR;
import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.models.Transaction;
import com.hashedin.marchantapp.viewmodel.TransactionViewModel;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.GenericViewHolder> {

    private int layoutId;
    private List<Transaction> transactions;
    private TransactionViewModel viewModel;

    public TransactionAdapter(@LayoutRes int layoutId, TransactionViewModel viewModel) {
        this.layoutId = layoutId;
        this.viewModel = viewModel;
    }

    private int getLayoutIdForPosition(int position) {
        return layoutId;
    }

    @Override
    public int getItemCount() {
        return transactions == null ? 0 : transactions.size();
    }

    public GenericViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, viewType, parent, false);

        return new GenericViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder holder, int position) {
        holder.bind(viewModel, position);
    }

    @Override
    public int getItemViewType(int position) {
        return getLayoutIdForPosition(position);
    }

    public void setDogBreeds(List<Transaction> breeds) {
        this.transactions = breeds;
    }

    public void addItems(List<Transaction> transactions) {
        this.transactions.addAll(transactions);
        notifyDataSetChanged();
    }
    class GenericViewHolder extends RecyclerView.ViewHolder {
        final ViewDataBinding binding;

        GenericViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TransactionViewModel viewModel, Integer position) {
            //viewModel.fetchDogBreedImagesAt(position);

            binding.setVariable(com.hashedin.marchantapp.BR.viewModel, viewModel);
            binding.setVariable(BR.position, position);
            binding.executePendingBindings();

        }

    }


}