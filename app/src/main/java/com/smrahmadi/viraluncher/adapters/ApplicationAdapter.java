package com.smrahmadi.viraluncher.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.smrahmadi.viraluncher.R;
import com.smrahmadi.viraluncher.models.Application;

import java.util.ArrayList;

/**
 * Created by lincoln on 3/24/18.
 */

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.myViewHolder> {


    private Context context ;
    private ArrayList<Application> applications = new ArrayList<>();

    public ApplicationAdapter(ArrayList<Application> applications) {
        this.applications = applications;
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        ImageView imgIcon ;
        TextView txtName ;

        myViewHolder(final View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.itemImgApp);
            txtName = itemView.findViewById(R.id.itemTxtName);

            imgIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    YoYo.with(Techniques.RotateIn)
                            .duration(700)
                            .playOn(itemView.findViewById(R.id.itemImgApp));
                }
            });
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context) ;

        View view = inflater.inflate(R.layout.item_app,parent,false);

        return new myViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        final Application application = applications.get(position);

        ImageView imgIcon = holder.imgIcon;
        TextView txtName = holder.txtName;

        imgIcon.setImageDrawable(application.getIcon());
        txtName.setText(application.getName());



    }



    @Override
    public int getItemCount() {
        return applications.size();
    }


}

