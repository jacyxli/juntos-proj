package org.vamosjuntos.juntos;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.*;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class ReportActivity extends Activity {

	// Key String for Preferences
	private static final String REPORT_TYPE = "REPORT_TYPE";
	private static final String NUM_PHOTO_ACTIVITY = "NUM_PHOTO_ACTIVITY";
	public static final String REPORT_DATE = "REPORT_DATE";
	public static final String TEXT_CONTENT = "TEXT_CONTENT";
	public static final String IF_ANONYMOUS = "IF_ANONYMOUS";
	public static final String IF_INFORM = "IF_INFORM";
	public static final String REPORT_USER_NAME = "REPORT_USER_NAME";
	public static final String REPORT_CATEGORY = "REPORT_CATEGORY";
	public static final String REPORT_LOCATION = "REPORT_LOCATION";

	private static int type = 0;
	private static final int MAX_ADDRESSES = 10;
	private static final int DESIRED_WIDTH = 400;
	private static final int DESIRED_HEIGHT = 400;
	private static int numPic = 0;
	private ImageView imageView;
	private Uri imageUri;
	private Uri audioUri;
	private String[] receivers = new String[]{"jacy.xli@gmail.com","wenpeiying@gmail.com"};
	private String text, city, person, category;
	private String latLongString = "None";
	private String addressString = "None";

	public static final int ABUSE_NOTIFICATION_ID = 1;
	public static final String NOTIFICATION_TYPE = "NOTIFICATION_TYPE";
	public static final String ABUSE_STRINGS = "NOTIFICATION_STRINGS";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report);

		Intent intent = getIntent();
		type = intent.getIntExtra(REPORT_TYPE, 0);

		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor prefsEditor = mPrefs.edit();
		int photoCount = mPrefs.getInt(NUM_PHOTO_ACTIVITY, 0);
		numPic = ++photoCount;
		prefsEditor.putInt(NUM_PHOTO_ACTIVITY, photoCount);
		prefsEditor.commit();

		getMyLocation();

		switch(type) {
		case DocumentAbuse.REPORT_CAMERA:
			takePhoto(); break;
		case DocumentAbuse.REPORT_TEXT:
			Intent textIntent = new Intent(ReportActivity.this, TextReportActivity.class);
			startActivityForResult(textIntent, DocumentAbuse.REPORT_TEXT);
			break;
		case DocumentAbuse.REPORT_AUDIO:
			Intent audioIntent = new Intent(ReportActivity.this, AudioRecord.class);
			startActivityForResult(audioIntent, DocumentAbuse.REPORT_AUDIO);
			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (DocumentAbuse.REPORT_CAMERA):
			if (resultCode == RESULT_OK) {
				Uri selectedImage;
				if (!hasImageCaptureBug()) {
					selectedImage = imageUri;
					File out = new File(selectedImage.getPath());
					ContentResolver cr = getContentResolver();
					cr.notifyChange(selectedImage, null);
					imageView = (ImageView) findViewById(R.id.myPhoto);
					Bitmap bitmap;
					try {
						//bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
						bitmap = decodeFile(out, DESIRED_WIDTH, DESIRED_HEIGHT);
						imageView.setImageBitmap(bitmap);
						Toast.makeText(this, selectedImage.toString(), Toast.LENGTH_LONG).show();
					} catch (Exception e) {
						Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
						Log.e("Camera", e.toString());
					}
				} else {
					selectedImage = data.getData();
				}
			}
		sendViaEmail();
		break;
		case (DocumentAbuse.REPORT_TEXT):
			if (resultCode == RESULT_OK) {
				text = data.getStringExtra(TEXT_CONTENT);
				if (!data.getBooleanExtra(IF_ANONYMOUS, false)) 
					person = data.getStringExtra(REPORT_USER_NAME);
				category = data.getStringExtra(REPORT_CATEGORY);
				if (data.getBooleanExtra(IF_INFORM, true)) 
					createNotification();
				sendViaEmail();
			}
		break;
		case (DocumentAbuse.REPORT_AUDIO):
			if (resultCode == RESULT_OK) {
				String mFileName = data.getStringExtra(AudioRecord.AUDIO_PATH);
				audioUri = Uri.fromFile(new File(mFileName));
				sendViaEmail();
			}
			break;
		default:
			break;
		}
		finish();
	}


	private void getMyLocation() {
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(true);
		String provider = locationManager.getBestProvider(criteria, true);
		Location l = locationManager.getLastKnownLocation(provider);
		if (l != null) {
			reverseGeocode(l);
		} else {
			Log.d("Location", "Unable to provide last known location.");
		}
	}

	private void reverseGeocode(Location l) {
		double latitude = l.getLatitude();
		double longitude = l.getLongitude();
		latLongString = "Lat: " + latitude + " Long: " + longitude;
		List<Address> addresses = null;
		Geocoder gc = new Geocoder(this, Locale.getDefault());
		try {
			addresses = gc.getFromLocation(latitude, longitude, MAX_ADDRESSES);
			StringBuilder sb = new StringBuilder();
			if (addresses.size() > 0) {
				Address address = addresses.get(0);
				sb.append(address.getAddressLine(0)).append(" ");
				city = address.getLocality();
				sb.append(city).append(" ");
				sb.append(address.getCountryName());
				addressString = sb.toString();
			}
		} catch (IOException e) {
			Log.e("Geocoder", e.toString());
		}
	}

	private void takePhoto() {
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File photo = new File(Environment.getExternalStorageDirectory(),  
				"JuntosPhoto" + Integer.toString(numPic) + ".jpg");
		imageUri = Uri.fromFile(photo);
		if (!hasImageCaptureBug()) {
			takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		} else {
			takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(takePhotoIntent, DocumentAbuse.REPORT_CAMERA);
	}

	// send photo via email
	private void sendViaEmail() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_EMAIL, receivers); 
		String locationString = "Location:\n" + latLongString + "\nAddress:\n" + addressString;
		String map = "http://maps.google.co.in/maps?q=" + addressString;
		String textString = locationString + "\nRedirecting to Google Map: " + map
				+ "\nDocumented text:\n" + text;
		intent.putExtra(Intent.EXTRA_TEXT, textString);

		switch(type) {
		case (DocumentAbuse.REPORT_CAMERA):
			intent.setType("image/jpeg");
		intent.putExtra(Intent.EXTRA_STREAM, imageUri); 
		intent.putExtra(Intent.EXTRA_SUBJECT, "Documented Abuse - Picture"); 
		break;
		case (DocumentAbuse.REPORT_TEXT):
			intent.setType("text/html");
		intent.putExtra(Intent.EXTRA_SUBJECT, "Documented Abuse - Text"); 
		break;
		case (DocumentAbuse.REPORT_AUDIO):
        intent.setType("video/3gp");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Documented Abuse - Audio");
      	//intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://sdcard/dcim/Camera/filename.3gp"));
        intent.putExtra(Intent.EXTRA_STREAM, audioUri);
			break;
		default:
			break;
		}
		startActivity(createEmailOnlyChooserIntent(intent, "Send via email"));
	}

	// Sending the email with only the mailto: protocol intent
	private Intent createEmailOnlyChooserIntent(Intent source,
			CharSequence chooserTitle) {
		Stack<Intent> intents = new Stack<Intent>();
		Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
				"info@domain.com", null));
		List<ResolveInfo> activities;
		activities = getPackageManager().queryIntentActivities(i, 0);
		for(ResolveInfo ri : activities) {
			Intent target = new Intent(source);
			target.setPackage(ri.activityInfo.packageName);
			intents.add(target);
		}
		if(!intents.isEmpty()) {
			Intent chooserIntent = Intent.createChooser(intents.remove(0), chooserTitle);
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
					intents.toArray(new Parcelable[intents.size()]));
			return chooserIntent;
		} else {
			return Intent.createChooser(source, chooserTitle);
		}
	}

	private boolean hasImageCaptureBug() {
		// list of known devices that have the bug
		ArrayList<String> devices = new ArrayList<String>();
		devices.add("android-devphone1/dream_devphone/dream");
		devices.add("generic/sdk/generic");
		devices.add("vodafone/vfpioneer/sapphire");
		devices.add("tmobile/kila/dream");
		devices.add("verizon/voles/sholes");
		devices.add("google_ion/google_ion/sapphire");
		return devices.contains(android.os.Build.BRAND + "/" + android.os.Build.PRODUCT + "/"
				+ android.os.Build.DEVICE);
	}

	// decode the image file to avoid OutOfMemoery Exception
	private static Bitmap decodeFile(File f,int WIDTH,int HIGHT){
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f),null,o);
			// calculate the new size we want to scale to
			final int REQUIRED_WIDTH=WIDTH;
			final int REQUIRED_HIGHT=HIGHT;
			//Find the correct scale value. It should be the power of 2.
			int scale=1;
			while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HIGHT)
				scale*=2;
			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize=scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {}
		return null;
	}

	private void createNotification() {
		Date today = Calendar.getInstance().getTime();
		AbuseInstance a = new AbuseInstance(today, person, category, city, text);
		broadcastAbuse(a);
	}

	public void broadcastAbuse(AbuseInstance anAbuse) {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

		NotificationCompat.Builder abuseNotificationBuilder = new NotificationCompat.Builder(this);  

		Intent ni = new Intent(this, AbuseActivity.class);
		ni.putExtra(NOTIFICATION_TYPE, 0);
		String[] contents = new String[5];
		contents[0] = anAbuse.getDescription();
		contents[1] = anAbuse.getPerson();
		contents[2] = anAbuse.getType();
		contents[3] = anAbuse.getLocation();
		contents[4] = df.format(anAbuse.getDate());
		ni.putExtra(ABUSE_STRINGS, contents);
		PendingIntent launchIntent = PendingIntent.getActivity(this, 0, ni, 0);

		abuseNotificationBuilder.setAutoCancel(true)
		.setSmallIcon(R.drawable.ic_action_news)
		.setContentIntent(launchIntent)
		.setWhen(anAbuse.getDate().getTime())
		.setContentTitle("Reminder: New Abuse Report")
		.setContentText("You have sent a new abuse report.")
		.setContentInfo(anAbuse.getLocation());

		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		inboxStyle.addLine("When: " + df.format(anAbuse.getDate()));
		inboxStyle.addLine("Where: " + anAbuse.getLocation());
		inboxStyle.addLine("Who: " + anAbuse.getPerson());
		inboxStyle.addLine("What: " + anAbuse.getType());
		inboxStyle.addLine("Click to see more details.");
		abuseNotificationBuilder.setStyle(inboxStyle);

		// set vibration and sound
		long[] vibrate = new long[] {1000,1000,1000,1000};
		abuseNotificationBuilder.setVibrate(vibrate);
		Uri ringURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		abuseNotificationBuilder.setSound(ringURI);

		Notification n = abuseNotificationBuilder.getNotification();
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(ABUSE_NOTIFICATION_ID, n);
	}

}