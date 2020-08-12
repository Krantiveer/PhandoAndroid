package com.perseverance.phando.payment.subscription;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.perseverance.phando.R;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    public interface OnClickListener {
        void onClick(PackageDetails item);
    }

    private List<PackageDetails> packageDetails;
    OnClickListener listener;


    public DataAdapter(OnClickListener listener, List<PackageDetails> packageDetails) {
        this.packageDetails = packageDetails;
        this.listener = listener;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row_package, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, final int i) {

//        String firstFourChars = "";     //substring containing first 4 characters
//
//        if (packageDetails.get(i).getPackageName().length() > 4)
//        {
//            firstFourChars = packageDetails.get(i).getPackageName().substring(0, 4);
//        }
        viewHolder.package_name_zulo.setText(packageDetails.get(i).getPackageName());
        // viewHolder.package_name.setText(packageDetails.get(i).getPackagePrice());
        viewHolder.package_price.setText(packageDetails.get(i).getPackagePrice());
        viewHolder.package_interval.setText("/-for " + packageDetails.get(i).getInterval_count() + " " + packageDetails.get(i).getPackageInterval());
        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(packageDetails.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return packageDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView package_price, package_interval, package_name_zulo;
        private CardView container;

        public ViewHolder(View view) {
            super(view);
            container = view.findViewById(R.id.container);
            //package_name = view.findViewById(R.id.package_name);
            package_name_zulo = view.findViewById(R.id.package_name_zulo);
            package_price = view.findViewById(R.id.package_price);
            package_interval = view.findViewById(R.id.package_interval);

        }
    }


}