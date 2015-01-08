package com.gcmexample.firstgcm;

import android.os.AsyncTask;
import android.widget.Toast;
import com.google.android.gcm.GCMRegistrar;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.owlike.genson.Genson;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class GCMMainActivity extends Activity {

	String TAG = "GCMMainActivity";
	Genson genson = new Genson();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);

		// Register Device Button
		Button regbtn = (Button) findViewById(R.id.register);

		regbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Registering device");
				// Retrive the sender ID from GCMIntentService.java
				// Sender ID will be registered into GCMRegistrar
				GCMRegistrar.register(GCMMainActivity.this, Config.GOOGLE_SENDER_ID);
				UserDetail userDetail = new UserDetail(GCMRegistrar.getRegistrationId(GCMMainActivity.this));

				String result = genson.serialize(userDetail);
				Log.d("JSON", result);
				new BackgroundTask().execute(result);
			}
		});
	}

	class BackgroundTask extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			HttpClient httpClient = new DefaultHttpClient();
			try {
				HttpPost post = new HttpPost(Config.URL+"sendMessage");
				post.setHeader("Content-type", "application/json");
				post.setEntity(new StringEntity(params[0]));
				HttpResponse response = httpClient.execute(post);
				int status = response.getStatusLine().getStatusCode();
				return status == HttpStatus.SC_CREATED;
			}
			catch (Exception e){
				e.printStackTrace();
				Log.d("Exception", e.getMessage());
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			super.onPostExecute(aBoolean);
			String text = aBoolean ? "Data saved" : "Data not saved";
			Toast.makeText(getApplication(), text, Toast.LENGTH_SHORT).show();
		}
	}
}