package co.askseoulites.seoulcityapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.askseoulites.seoulcityapp.Constants;
import co.askseoulites.seoulcityapp.MainActivity;
import co.askseoulites.seoulcityapp.Manifest;
import co.askseoulites.seoulcityapp.R;
import co.askseoulites.seoulcityapp.helper.NetworkCheck;
import co.askseoulites.seoulcityapp.model.ModelUtils;
import co.askseoulites.seoulcityapp.model.Question;

public class NewQuestionActivity extends AppCompatActivity {

    private static final String LOG_TAG = NewQuestionActivity.class.getSimpleName();
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int GALLERY_KITKAT_INTENT_CALLED = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 3;
    private static final int REQ_WIDTH = 640;
    private static final int REQ_HEIGHT = 480;
    private static final int THUMB_SIZE = 256;

    private EditText mTitle;
    private EditText mContent;
    private ImageButton insertPhoto;
    private ImageButton insertLink;
    private String selectedImagePath;
    ProgressBar progress;
    private EditText mInput;
    private String link ="";
    private LinearLayout bottomLayout;
    private AlertDialog linkDialog;
    private TextView submit;
    private ParseFile photo;
    private Question question;


    private  int tagPosition;
    private int mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        submit = (TextView) toolbar.findViewById(R.id.postQuestion);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tagPosition = getIntent().getIntExtra(Constants.KEY_TAG_NO,0);
        mCategory = getIntent().getIntExtra(Constants.KEY_CATEGORY_NO,0);
        question = createQuestion();

        mTitle = (EditText ) findViewById(R.id.questionTitle);
        mContent = (EditText) findViewById(R.id.questionContent);
        insertPhoto = (ImageButton) findViewById(R.id.insertPhoto);
        insertLink = (ImageButton) findViewById(R.id.insertLink);
        progress = (ProgressBar) findViewById(R.id.progressQuesion);
//        bottomLayout = (LinearLayout) findViewById(R.id.bottomLayoutQuestion);
        mTitle.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mTitle, InputMethodManager.SHOW_IMPLICIT);

        /* select Photo */
        insertPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(NewQuestionActivity.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(NewQuestionActivity.this,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                            // Show an expanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.

                        } else {

                            // No explanation needed, we can request the permission.

                            ActivityCompat.requestPermissions(NewQuestionActivity.this,
                                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_MEDIA);
                        }
                    } else {
                        startPhotoChooser();
                    }

                } else {
                    startPhotoChooser();
                }
            }
        });

        /* Insert Link */
        mInput = new EditText(this);
        insertLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkDialog = createLinkDialog(mInput);
                linkDialog.show();
            }
        });
        submit.setEnabled(false);
        /* TextWatch Listener */
        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0 ) {
                    submit.setEnabled(true);
                } else {
                    submit.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /* Submit Question */
        if(NetworkCheck.isNetworkAvailable(this)) {
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveQuestion();
                }
            });
        } else {
            Toast.makeText(getBaseContext(),getResources().getString(R.string.network_not_available),
                    Toast.LENGTH_SHORT).show();

        }
    }

    private void saveQuestion() {

        final String title = mTitle.getText().toString();
        final String content = mContent.getText().toString();
        progress.setVisibility(View.VISIBLE);
        if(title.length() != 0  && content.length() != 0) {
            /* If image is present */
            submit.setEnabled(false);
            if (selectedImagePath != null && selectedImagePath.length() != 0) {
                Bitmap bitmap = decodeSampledBitmapFromFile(selectedImagePath, REQ_WIDTH, REQ_HEIGHT);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] originImage = bos.toByteArray();
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                photo = new ParseFile("question_photo.jpg", originImage);
                photo.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            question.setTitle(title);
                            question.setContent(content);
                            List<Integer> tagList = new ArrayList<Integer>();
                            tagList.add(tagPosition);
                            question.setCatNo(mCategory);
                            question.setTags(tagList);
                            question.setPhoto(photo);
                            question.setlink(link);
                            question.setVotes(0);
                            ParseACL acl = new ParseACL();
                            acl.setPublicReadAccess(true);
                            acl.setWriteAccess(ParseUser.getCurrentUser(), true);
                            acl.setRoleWriteAccess("Moderators", true);
                            acl.setRoleWriteAccess("Administrator", true);
                            question.setACL(acl);
                            question.setWriter(ParseUser.getCurrentUser());
                            question.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null) {
                                        Log.d(LOG_TAG, "question saved successfully | with Image!");
                                        progress.setVisibility(View.GONE);
                                        showFinishToast();
                                        submit.setEnabled(true);
                                        question.pinInBackground(ModelUtils.QUESTIONS_PIN);
                                    } else {
                                        Log.d(LOG_TAG,"question exception :" + e.getMessage());
                                    }
                                }
                            });
                        }
                    }
                });

            } else {
                /* Image not selected */
                question.setTitle(title);
                question.setContent(content);
                List<Integer> tagList = new ArrayList<Integer>();
                tagList.add(tagPosition);
                question.setCatNo(mCategory);
                question.setTags(tagList);
                question.setlink(link);
                question.setVotes(0);
                ParseACL acl = new ParseACL();
                acl.setPublicReadAccess(true);
                acl.setWriteAccess(ParseUser.getCurrentUser(), true);
                acl.setRoleWriteAccess("Moderators", true);
                acl.setRoleWriteAccess("Administrator", true);
                question.setACL(acl);
                question.setWriter(ParseUser.getCurrentUser());
                question.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            Log.d(LOG_TAG,"question saved successfully!");
                            progress.setVisibility(View.GONE);
                            Log.d(LOG_TAG, "Saved in Background Board + title : " + title);
//                                                board.pinInBackground("board_"+ universityPos);
                            showFinishToast();
                            submit.setEnabled(true);
                            question.pinInBackground(ModelUtils.QUESTIONS_PIN);
                        } else {
                            Log.d(LOG_TAG,"question exception :" + e.getMessage());

                        }
                    }
                });
            }
        } else {
            progress.setVisibility(View.GONE);
            Toast.makeText(getBaseContext(), getResources().getString(R.string.enter_some_text),
                    Toast.LENGTH_SHORT).show();

        }

    }

    private AlertDialog createLinkDialog(final EditText input) {

        // 1. Instantiate an AlertDialog.Builder with its constructor
        final AlertDialog.Builder builder = new AlertDialog.Builder(NewQuestionActivity.this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(getResources().getString(R.string.insert_link_message))
                .setTitle(getResources().getString(R.string.insert_link_title))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Editable value = input.getText();
                        link = value.toString();
                        if(!Patterns.WEB_URL.matcher(link).matches()) {

                            link = "";
                            Toast toast = Toast.makeText(NewQuestionActivity.this,
                                    getResources().getString(R.string.enter_valid_url),
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP,0,60);
                            toast.show();
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                linkDialog.dismiss();
            }
        });
        if(input.getParent() == null) {
            Log.d(LOG_TAG, "input.getParent() == null");
            if(!link.isEmpty())
                input.setText(link);
            builder.setView(input);
        } else {
            Log.d(LOG_TAG,"input.getParent() != null");
            mInput = null;
            mInput = new EditText(this);
            if(!link.isEmpty())
                mInput.setText(link);
            builder.setView(mInput);
        }

        // 3. Get the AlertDialog from create()
        return builder.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;
        if (null == data) return;
        Uri originalUri = null;
        if (requestCode == PICK_IMAGE_REQUEST) {
            originalUri = data.getData();
            Log.d(LOG_TAG,"originalURI : " + originalUri);
            getAbsolutePath(originalUri);

        } else if (requestCode == GALLERY_KITKAT_INTENT_CALLED) {
            originalUri = data.getData();
            Log.d(LOG_TAG,"originalURI : " + originalUri);
            getAbsolutePathKit(originalUri);

        }
    }

    private void getAbsolutePath(Uri photoUri) {

        Uri uri = photoUri;
        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();

        Log.d(LOG_TAG, DatabaseUtils.dumpCursorToString(cursor));

        int columnIndex = cursor.getColumnIndex(projection[0]);
        String picturePath = cursor.getString(columnIndex); // returns null
        cursor.close();
        selectedImagePath = picturePath;
        insertPhoto.setImageBitmap(getThumbnail(selectedImagePath));
        Log.d(LOG_TAG,"Picture path : " + picturePath);
    }

    private void getAbsolutePathKit(Uri originalUri) {

        String id = originalUri.getLastPathSegment().split(":")[1];
        final String[] imageColumns = {MediaStore.Images.Media.DATA };
        final String imageOrderBy = null;

        Uri uri = getUri();


        Cursor imageCursor = managedQuery(uri, imageColumns,
                MediaStore.Images.Media._ID + "=" + id, null, imageOrderBy);

        if (imageCursor.moveToFirst()) {
            selectedImagePath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
        }
        insertPhoto.setImageBitmap(getThumbnail(selectedImagePath));
        Log.e(LOG_TAG, selectedImagePath); // use selectedImagePath
    }

    private Bitmap getThumbnail(String path) {

        final int ThumbSIZE = 128;
        return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), ThumbSIZE, ThumbSIZE);

    }

    private Uri getUri() {
        String state = Environment.getExternalStorageState();
        if(!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    /**
     * This function takes the path to our file from which we want to create
     * a Bitmap, and decodes it using a smaller sample size in an effort to
     * avoid an OutOfMemoryError.
     *
     * See: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     *
     * @param path The path to the file resource
     * @param reqWidth The required width that the image should be prepared for
     * @param reqHeight The required height that the image should be prepared for
     * @return A Bitmap resource that has been scaled down to an appropriate size, so that it can conserve memory
     *
     */
    public static Bitmap decodeSampledBitmapFromFile(String path,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }


    /**
     * This is a helper function used to calculate the appropriate sampleSize
     * for use in the decodeSampledBitmapFromFile() function.
     *
     * See: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     *
     * @param options The BitmapOptions for the Bitmap resource that we want to scale down
     * @param reqWidth The target width of the UI element in which we want to fit the Bitmap
     * @param reqHeight The target height of the UI element in which we want to fit the Bitmap
     * @return A sample size value that is a power of two based on a target width and height
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private Question createQuestion() {
        return new Question();
    }

    private void showFinishToast() {

        Toast.makeText(NewQuestionActivity.this,
                getResources().getString(R.string.question_posted_toast),
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(NewQuestionActivity.this, SingleCatVPActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.KEY_CATEGORY_NO,mCategory);
        startActivity(intent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_MEDIA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startPhotoChooser();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private void startPhotoChooser() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    getResources().getString(R.string.select_picture))
                    , PICK_IMAGE_REQUEST);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);
        }

    }
}
