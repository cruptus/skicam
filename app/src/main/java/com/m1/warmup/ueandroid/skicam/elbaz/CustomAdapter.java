package com.m1.warmup.ueandroid.skicam.elbaz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomAdapter extends BaseAdapter{
    ArrayList<HashMap<String, Object>> result;
    Context context;

    private static LayoutInflater inflater=null;

    public CustomAdapter(MainActivity mainActivity, ArrayList<HashMap<String, Object>> prgmNameList) {
        // TODO Auto-generated constructor stub
        context = mainActivity;


        result = prgmNameList;

        inflater = ( LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub

        return result.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class ViewHolder
    {
        TextView title;
        ImageView img;
        TextView temperature;
        ImageButton tel;
        ImageButton maps;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder=new ViewHolder();
        View rowView;

        rowView = inflater.inflate(R.layout.item_list_cam, parent, false);

        holder.title=(TextView) rowView.findViewById(R.id.title_textview);
        holder.img=(ImageView) rowView.findViewById(R.id.imageView);
        holder.temperature = (TextView) rowView.findViewById(R.id.temperature_textView);
        holder.tel = (ImageButton) rowView.findViewById(R.id.imageButton);
        holder.maps = (ImageButton) rowView.findViewById(R.id.imageButton_map);

        if(result.get(position).get("coord") != null) {
            holder.maps = (ImageButton) rowView.findViewById(R.id.imageButton_map);

            holder.maps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MapActivity.class);
                    intent.putExtra("coord", result.get(position).get("coord").toString());
                    context.startActivity(intent);
                }
            });
        }else{
            holder.maps.setVisibility(View.INVISIBLE);
        }
        holder.temperature.setText(result.get(position).get("place").toString());
        holder.title.setText(result.get(position).get("nom").toString());
        holder.img.setImageBitmap((Bitmap) result.get(position).get("url"));
        if(result.get(position).get("phone") == null){
            holder.tel.setVisibility(View.INVISIBLE);
        }else {
            holder.tel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("ee", "tel:" + result.get(position).get("phone").toString());
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + result.get(position).get("phone").toString()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        MediaStore.Images.Media.insertImage(context.getContentResolver(), (Bitmap) result.get(position).get("url"), result.get(position).get("nom").toString(), null);
                        //Yes button clicked
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        dialog.cancel();
                        break;
                }
            }
        };

        holder.img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Voulez vous sauvegarder cette image ?").setPositiveButton("Oui", dialogClickListener)
                        .setNegativeButton("Non", dialogClickListener).show();
                return false;
            }
        });

        rowView.setTag(holder);

        return rowView;
    }

}
