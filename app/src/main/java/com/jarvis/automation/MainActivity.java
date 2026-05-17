package com.jarvis.automation;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
public class MainActivity extends Activity {
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
TextView tv = new TextView(this);
tv.setText("जार्विस इंजन एक्टिव है!");
tv.setTextSize(20);
setContentView(tv);
}
}