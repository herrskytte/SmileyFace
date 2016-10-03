package no.skytte.smileyface.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import no.skytte.smileyface.R;

public class SimpleImageArrayAdapter extends ArrayAdapter<Integer> {

    private Integer[] images;
    private Integer[] texts;
    LayoutInflater mInflater;

    public SimpleImageArrayAdapter(Context context, Integer[] images, Integer[] texts) {
        super(context, android.R.layout.simple_spinner_item, images);
        this.images = images;
        this.texts = texts;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_smiley_dropdown, null);
        }
        ((ImageView) convertView.findViewById(R.id.smiley_image)).setImageResource(images[position]);
        ((TextView) convertView.findViewById(R.id.smiley_text)).setText(texts[position]);
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_smiley, null);
        }
        ((ImageView) convertView).setImageResource(images[position]);
        return convertView;
    }

}