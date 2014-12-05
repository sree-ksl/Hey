package com.example.Hey;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hello on 14/08/14.
 */
public class UserAdapter extends ArrayAdapter<ParseUser> {

    protected Context mContext;
    protected List<ParseUser> mUsers;

    public UserAdapter(Context context, List<ParseUser> users) {
        super(context, R.layout.message_item, users);
        mContext = context;
        mUsers = users;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null) {
            //Inflate convertView from the layout file and using a context and return it to the list
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            //Initialise holder as a new view holder
            holder = new ViewHolder();
            //Initialise image view and text view inside it
            holder.userImageView = (ImageView) convertView.findViewById(R.id.userImageView);
            holder.checkImageView = (ImageView)convertView.findViewById(R.id.checkImageView);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            //holder.timeLabel = (TextView) convertView.findViewById(R.id.timeLabel);
            convertView.setTag(holder); //to make the list scroll correctly
        }
        else{
            holder = (ViewHolder)convertView.getTag(); //gets the viewHolder that was already created
        }

        ParseUser user = mUsers.get(position);
        String email = user.getEmail().toLowerCase(); //should be in lower case so it is converted to lower case here

        if(email.equals("")){
            holder.userImageView.setImageResource(R.drawable.avatar_empty);
        }
        else{
            //Set Gravatar for user image
            String hash = MD5Util.md5Hex(email);
            String gravatarUrl = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=404"; // to get larger images so (size)s=204

            //Log.d("TEST", gravatarUrl);

            //get images from picasso library
            Picasso.with(mContext)
                    .load(gravatarUrl)
                    .placeholder(R.drawable.avatar_empty)  //loads our default image when 404 error occurs means no image is found
                    .into(holder.userImageView);

        }

        holder.nameLabel.setText(user.getUsername());

        //get reference to grid view
        GridView gridView = (GridView)parent;
        //check if item being tapped on is checked or not
        if(gridView.isItemChecked(position)){
            //if checked then show the checkmark
            holder.checkImageView.setVisibility(View.VISIBLE);
        }
        else{
            //not checked so make it invisible
            holder.checkImageView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private static class ViewHolder {
      //includes data that's to be displayed in the custom layout
        ImageView userImageView;
        ImageView checkImageView;
        TextView nameLabel;

    }

    //refill the adapter
    public void refill(List<ParseUser> users){
       //clear the current data
        mUsers.clear();
        //add all the new ones
        mUsers.addAll(users);
        //call a method
        notifyDataSetChanged();
    }
}
