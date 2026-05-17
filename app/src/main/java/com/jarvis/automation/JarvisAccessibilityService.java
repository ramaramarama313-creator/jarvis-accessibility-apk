package com.jarvis.automation;
import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
public class JarvisAccessibilityService extends AccessibilityService {
@Override
protected void onServiceConnected() { super.onServiceConnected(); }
@Override public void onAccessibilityEvent(AccessibilityEvent event) {}
@Override public void onInterrupt() {}
}