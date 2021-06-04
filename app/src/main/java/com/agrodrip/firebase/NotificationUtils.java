package com.agrodrip.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.agrodrip.R;
import com.agrodrip.activity.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.POWER_SERVICE;

public class NotificationUtils {
    private static final int NOTIFICATION_ID = 200;
    private static final String PUSH_NOTIFICATION = "pushNotification";
    private static final String URL = "url";
    private static final String ACTIVITY = "activity";
    public static Ringtone r;
    Map<String, Class> activityMap = new HashMap<>();
    PowerManager powerManager;
    Uri alarmSound;
    private Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
        activityMap.put("MainActivity", MainActivity.class);

    }

    public void displayNotification(NotificationVO notificationVO, Intent resultIntent) {
        {
            String message = notificationVO.getMessage();
            Spanned htmlAsSpanned = Html.fromHtml(message);
            String title = notificationVO.getTitle();
            Log.d("noti", "displayNotification: " + notificationVO.getMessage() + notificationVO.getTitle());
            String iconUrl = notificationVO.getIconUrl();
            String action = notificationVO.getAction();
            String destination = notificationVO.getActionDestination();
            Bitmap iconBitMap = null;
            if (iconUrl != null) {
                iconBitMap = getBitmapFromURL(iconUrl);
            }
                final int icon = R.mipmap.ic_launcher_round;

            PendingIntent resultPendingIntent;

            if (URL.equals(action)) {
                Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(destination));

                resultPendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
            } else if (ACTIVITY.equals(action) && activityMap.containsKey(destination)) {
                resultIntent = new Intent(mContext, activityMap.get(destination)).putExtra("fromnotification", true);

                resultPendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            } else {
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                resultPendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(mContext, notification);
                r.play();

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                NotificationChannel mChannel = new NotificationChannel(mContext.getString(R.string.notification_channel_id), PUSH_NOTIFICATION, NotificationManager.IMPORTANCE_HIGH);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setSound(alarmSound, attributes);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mNotificationManager.createNotificationChannel(mChannel);

            }


            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, mContext.getString(R.string.notification_channel_id));
            Notification notification;

            if (iconBitMap == null) {
//When Inbox Style is applied, user can expand the notification
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.addLine(message);

                powerManager = (PowerManager) mContext.getSystemService(POWER_SERVICE);
                boolean result = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH && powerManager.isInteractive() || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH && powerManager.isScreenOn();

                notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setLights(Color.BLUE, 3000, 3000)
                        .setContentTitle(title)
                        .setContentIntent(resultPendingIntent)
                        .setStyle(inboxStyle)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                        .setContentText(htmlAsSpanned)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .build();


            } else {
//If Bitmap is created from URL, show big icon
                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
                bigPictureStyle.setBigContentTitle(title);
                bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
                bigPictureStyle.bigPicture(iconBitMap);
                notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setContentTitle(title)
                        .setLights(Color.BLUE, 3000, 3000)
                        .setContentIntent(resultPendingIntent)
                        .setStyle(bigPictureStyle)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentText(htmlAsSpanned)
                        .build();
                powerManager = (PowerManager) mContext.getSystemService(POWER_SERVICE);
                boolean result = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH && powerManager.isInteractive() || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH && powerManager.isScreenOn();
                notification.contentIntent = resultPendingIntent;


            }

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    /**
     * Downloads push notification image before displaying it in
     * the notification tray
     *
     * @param strURL : URL of the notification Image
     * @return : BitMap representation of notification Image
     */
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            java.net.URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void playNotificationSound() {

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(mContext, notification);
            r.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}