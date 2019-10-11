package com.hashedin.marchantapp.viewactivity.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.hashedin.marchantapp.BR;
import com.hashedin.marchantapp.Services.models.TransactionHistory.Result;
import com.hashedin.marchantapp.viewmodel.TransactionViewModel;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.GenericViewHolder> {

    private int layoutId;
    private List<Result> results;
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
        return results == null ? 0 : results.size();
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

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public void addItems(List<Result> results) {
        this.results.addAll(results);
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