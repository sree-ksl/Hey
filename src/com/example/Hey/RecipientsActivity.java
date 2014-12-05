package com.example.Hey;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.parse.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hello on 13/08/14.
 */
public class RecipientsActivity extends Activity {

    public static final String TAG = RecipientsActivity.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected MenuItem mSendMenuItem;
    protected Uri mMediaUri;
    protected String mFileType;
    protected GridView mGridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.user_grid);
        //Show the action bar
        setupActionBar();

        mGridView = (GridView)findViewById(R.id.friendsGrid);
        mGridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClickListener);

        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);


        TextView emptyTextView = (TextView)findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {

                setProgressBarIndeterminateVisibility(false);

                if(e == null) {
                    mFriends = friends;

                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }

                    if(mGridView.getAdapter() == null) {
                        //create the adapter and set it
                        UserAdapter adapter = new UserAdapter(RecipientsActivity.this, mFriends);
                        mGridView.setAdapter(adapter);
                    }
                    else{
                        ((UserAdapter)mGridView.getAdapter()).refill(mFriends);
                    }
                }
                else{
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder= new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipients, menu);
        mSendMenuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_send:
                ParseObject message = createMessage();
                if(message == null){
                    //error
                    AlertDialog .Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.error_selecting_file);
                    builder.setTitle("We're sorry!");
                    builder.setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else{
                    send(message);
                    finish();

                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar(){

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    protected ParseObject createMessage(){
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIds());
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

         if(fileBytes == null){
             return null;
         }
        else{
             if(mFileType.equals(ParseConstants.TYPE_IMAGE)){
                 fileBytes = FileHelper.reduceImageForUpload(fileBytes);
             }

             String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
             ParseFile file = new ParseFile(fileName, fileBytes);
             message.put(ParseConstants.KEY_FILE, file);
             return message;
         }

    }

    protected ArrayList<String> getRecipientIds(){
        ArrayList<String> recipientsIds = new ArrayList<String>();
        for(int i=0; i < mGridView.getCount(); i++){
            if(mGridView.isItemChecked(i)){
                recipientsIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientsIds;
    }

    protected  void send(ParseObject message){
      message.saveInBackground(new SaveCallback() {
          @Override
          public void done(ParseException e) {
              if(e == null){
                  //Successful
                  Toast.makeText(RecipientsActivity.this, R.string.success_message, Toast.LENGTH_LONG).show();
                  sendPushNotifications();
              }
              else{
                  AlertDialog .Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                  builder.setMessage(R.string.error_sending_message);
                  builder.setTitle("We're sorry!");
                  builder.setPositiveButton(android.R.string.ok, null);

                  AlertDialog dialog = builder.create();
                  dialog.show();

              }
          }
      });
    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            ImageView checkImageView = (ImageView)view.findViewById(R.id.checkImageView);

            if(mGridView.getCheckedItemCount() > 0){
                mSendMenuItem.setVisible(true);
            }
            else{
                mSendMenuItem.setVisible(false);
            }

            if(mGridView.isItemChecked(position)){
                //add recipient
                checkImageView.setVisibility(View.VISIBLE);
            }
            else{
                //remove recipient
                checkImageView.setVisibility(View.INVISIBLE);

            }
        }
    };

    protected void sendPushNotifications(){
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        //check which user ids are present in the recipients ids of the message,in the backend and they are notified
        query.whereContainedIn(ParseConstants.KEY_USER_ID, getRecipientIds());

        //send the push notification
        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(getString(R.string.push_message, ParseUser.getCurrentUser().getUsername()));
        push.sendInBackground();


    }
}