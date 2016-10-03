package com.srh.birthdayassistant;

import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import managers.SharedPrefManager;
import utils.CollectionUtils;
import utils.ContactUtils;
import utils.ImageViewUtils;
import utils.TextViewUtils;
import utils.ViewUtils;

public class ContactCardsAdapter extends RecyclerView.Adapter<ContactCardsAdapter.ViewHolder> {
    public interface ContactClickdEvents {
        void onContactRowClicked(int pos, ContactUtils.Contact contact);
    }
    ContactClickdEvents listener;
    public void setEventsListener(ContactClickdEvents listener){
        this.listener = listener;
    }

    private List<ContactUtils.Contact> items;
    private Drawable defaultContactImage = ImageViewUtils.getSvgDrawable(R.drawable.ic_contact);

    ContactCardsAdapter(List<ContactUtils.Contact> items){
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ContactUtils.Contact item = items.get(position);
        TextViewUtils.setTextAndVisibility(holder.title, item.getName());
        TextViewUtils.setTextAndVisibility(holder.subtitle, item.getBirthDate());
        ImageViewUtils.setImageUri(holder.image, item.getThumbnailUri(), defaultContactImage);
        holder.rowContainer.setOnClickListener(rowClickedListener);
    }

    private View.OnClickListener rowClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewUtils.vibrate(view);
            final int pos = ((ViewHolder)view.getTag()).getAdapterPosition();
            if(pos >= 0 && pos < items.size()) {
                final ContactUtils.Contact contact = items.get(pos);
                if(listener != null) {
                    listener.onContactRowClicked(pos, contact);
                }
            }
        }
    };

    @Override
    public int getItemCount() {
        return CollectionUtils.getItemCount(items);
    }

    public void setNewData(List<ContactUtils.Contact> newData) {
        this.items = newData;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private final View rowContainer;
        private final ImageView image;
        private final TextView title;
        private final TextView subtitle;
        public ViewHolder(View itemView) {
            super(itemView);
            rowContainer = itemView.findViewById(R.id.row_container);
            rowContainer.setTag(this);
            title = (TextView) itemView.findViewById(R.id.contact_title);
            subtitle = (TextView) itemView.findViewById(R.id.contact_subtitle);
            image = (ImageView) itemView.findViewById(R.id.contact_image);
        }
    }
}
