package com.jarvis.automation;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JarvisAccessibilityService extends AccessibilityService {
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        // बैकग्राउंड में टर्मक्स सर्वर को लगातार टटोलने वाला लूप
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        URL url = new URL("http://127.0.0.1:8080/get");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(1000);
                        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String cmd = rd.readLine();
                        rd.close();
                        if (cmd != null && cmd.startsWith("CLICK")) {
                            String[] parts = cmd.split(",");
                            final float x = Float.parseFloat(parts[1]);
                            final float y = Float.parseFloat(parts[2]);
                            
                            // मुख्य स्क्रीन थ्रेड पर क्लिक डिसपैच करना
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    GestureDescription.Builder builder = new GestureDescription.Builder();
                                    Path path = new Path();
                                    path.moveTo(x, y);
                                    builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 50));
                                    dispatchGesture(builder.build(), null, null);
                                }
                            });
                        }
                    } catch (Exception e) { // टर्मक्स बंद होने पर शांत रहे }
                    try { Thread.sleep(500); } catch (Exception e) {} // हर आधे सेकंड में चेक करेगा
                }
            }
        }).start();
    }
    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt() {}
}