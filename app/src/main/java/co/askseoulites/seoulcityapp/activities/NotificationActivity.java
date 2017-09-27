/**
 * Copyright 2014 Facebook, Inc.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to
 * use, copy, modify, and distribute this software in source code or binary
 * form for use in connection with the web services and APIs provided by
 * Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use
 * of this software is subject to the Facebook Developer Principles and
 * Policies [http://developers.facebook.com/policy/]. This copyright notice
 * shall be included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 */

package co.askseoulites.seoulcityapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import co.askseoulites.seoulcityapp.Constants;
import co.askseoulites.seoulcityapp.R;

import java.util.List;

import co.askseoulites.seoulcityapp.model.ModelUtils;
import co.askseoulites.seoulcityapp.model.Notification;

public class NotificationActivity extends AppCompatActivity {

	private AlertsAdapter alertsAdapter;
	private ListView alertsView;
	private LinearLayout noAlertsView;
    
    public static final String ALERT_MSG_ID = "messageId";

    public static final String LOG_TAG = NotificationActivity.class.getSimpleName();

    boolean isFeed = true;
    private View mContentView;
    private View mLoadingView;
    private int mShortAnimationDuration;
    List<Notification> results;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alerts);
		// Set up the views
		alertsView = (ListView) findViewById(R.id.alerts_view);
		noAlertsView = (LinearLayout) findViewById(R.id.no_alerts_view);

        mContentView = findViewById(R.id.no_alerts_view);
        mLoadingView = findViewById(R.id.loading_spinner_noti);

        // Initially hide the content view.
        mContentView.setVisibility(View.GONE);

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        setTitle(getResources().getString(R.string.action_alerts));

		// Set up the adapter for the alert list
		alertsAdapter = new AlertsAdapter(this);
		alertsView.setAdapter(alertsAdapter);
		alertsView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Notification message = alertsAdapter.getItem(position);
                if(message.getPostId() == null) {
                    if (message.getUrl() != null) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(message.getUrl()));
                        if (canResolveIntent(i)) {
                            startActivity(i);
                        }
                    }
                    return;
                }
                Intent intent= null;
                switch(message.getPayload()) {

                    case Constants.ACTIVITY_QUESTION_DETAIL:
                        intent = new Intent(NotificationActivity.this,QuestionDetailActivity.class);
                        intent.putExtra(ModelUtils.OBJECT_ID,message.getPostId());
                        startActivity(intent);
                        break;

                    case Constants.ACTIVITY_COLLECTION_DETAIL:
                        intent = new Intent(NotificationActivity.this,CollectionDetailActivity.class);
                        intent.putExtra(ModelUtils.OBJECT_ID,message.getPostId());
                        startActivity(intent);
                        break;

                    default:
                        break;
                }


			}
		});

	}

	public void checkForAlerts() {
		alertsAdapter.clear();

		ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser == null ) {
            finish();
        }

        new RemoteDataTask().execute(0);


	}

	@Override
	public void onResume() {
		super.onResume();
		checkForAlerts();
	}

	private class AlertsAdapter extends ArrayAdapter<Notification> {

		private ViewHolder holder;
		private LayoutInflater inflater;

		public AlertsAdapter(Context context) {
			super(context, 0);
			inflater = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {

			if (v == null) {
				v = inflater.inflate(R.layout.list_item_alert, parent, false);
				holder = new ViewHolder();
				holder.surveyTitle = (TextView) v
						.findViewById(R.id.survey_title);
                holder.time = (TextView) v.findViewById(R.id.noti_time);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}

			Notification message = getItem(position);
			TextView surveyTitle = holder.surveyTitle;
            TextView notiTime = holder.time;
            notiTime.setText(Notification.convertDateToString(message.getCreatedAt()));
			surveyTitle.setText(message.getTitle());

			return v;
		}

    }

	private static class ViewHolder {
		TextView surveyTitle;
        TextView time;
	}

    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = getPackageManager()
                .queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }



    private void crossfade() {

        mLoadingView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoadingView.setVisibility(View.GONE);
                    }
                });

    }


    private class RemoteDataTask extends AsyncTask<Integer, Void, Void> {
        protected Void doInBackground(Integer... params) {

            ParseQuery<Notification> messageQuery = new ParseQuery<Notification>(Notification.class);
            messageQuery.addDescendingOrder("createdAt");
            messageQuery.setLimit(20);

            Log.d(LOG_TAG, "doinBackground !");

            try {
                Log.d(LOG_TAG, "fetch notifications");
                results = messageQuery.find();

            } catch (ParseException e) {
                Log.d(LOG_TAG, "exception --> " + e.getMessage());

            }
            return null;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(LOG_TAG, "onPostExecute !");
//            progressDialog.dismiss();
            crossfade();
            if (results != null) {
                if(!results.isEmpty()) {
                    Log.d(LOG_TAG, "on success Task ");

                    for (Notification message : results) {
                        alertsAdapter.add(message);
                    }
                }
            } else {
                if (alertsView != null)
                    alertsView.setEmptyView(noAlertsView);
                mContentView.setAlpha(0f);
                mContentView.setVisibility(View.VISIBLE);

                // Animate the content view to 100% opacity, and clear any animation
                // listener set on the view.
                mContentView.animate()
                        .alpha(1f)
                        .setDuration(mShortAnimationDuration)
                        .setListener(null);
                crossfade();

            }

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    }
