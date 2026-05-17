package com.jarvis.automation;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
public class MainActivity extends Activity {
@Override protected void onCreate(Bundle s) { super.onCreate(s);
TextView tv = new TextView(this);
tv.setText("जार्विस न्यूरल लिंक एक्टिव है! टर्मक्स से कनेक्टेड।");
setContentView(tv); } }