package umn.ac.id.fbtest.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import umn.ac.id.fbtest.Interface.ItemClickListener;
import umn.ac.id.fbtest.R;

public class MemberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtName, txtIdentification;
    public CircleImageView MemberView;
    public ItemClickListener listener;


    public MemberViewHolder(View itemView)
    {
        super(itemView);


        MemberView = (CircleImageView) itemView.findViewById(R.id.member_image);
        txtName = (TextView) itemView.findViewById(R.id.member_name);
        txtIdentification = (TextView) itemView.findViewById(R.id.member_identification);
    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View view)
    {
        listener.onClick(view, getAdapterPosition(), false);
    }
}
