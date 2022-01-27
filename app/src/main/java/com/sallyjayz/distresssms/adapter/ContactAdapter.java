package com.sallyjayz.distresssms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.sallyjayz.distresssms.R;
import com.sallyjayz.distresssms.model.DistressContact;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private final LayoutInflater mInflater;
    private List<DistressContact> mDistressContactList;

    public ContactAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.contact_list, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        if (mDistressContactList != null) {
            DistressContact currentDistressContact = mDistressContactList.get(position);
            holder.contactName.setText(currentDistressContact.getContactName());
        } else {
            holder.contactName.setText("No Contact Added");
        }
    }

    @Override
    public int getItemCount() {
        return mDistressContactList.size();
    }

    public void setDistressContact(List<DistressContact> distressContacts) {
//        this.mDistressContactList = distressContacts;
        notifyDataSetChanged();
        this.mDistressContactList = distressContacts;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        ShapeableImageView contactImage;
        MaterialTextView contactName;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.contact_image);
            contactName = itemView.findViewById(R.id.contact_name);
        }
    }

    public DistressContact getContactAtPosition(int position) {
        return mDistressContactList.get(position);
    }

    public void removeContact(int position) {
        mDistressContactList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,mDistressContactList.size());
    }

    public void deleteAll() {
        mDistressContactList.clear();
        notifyDataSetChanged();
    }
}
