package com.jarvis.automation;
import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {
    EditText inputField;
    TextView chatBox;
    Button sendBtn;
    TextToSpeech tts;
    // आपकी असली API Key अब सीधे ऐप के अंदर है!
    String apiKey = "AIzaSyAgnlIv3NC_8QAAY6-llG8HF2mZ6J-0k_s"; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        inputField = findViewById(R.id.inputField);
        chatBox = findViewById(R.id.chatBox);
        sendBtn = findViewById(R.id.sendBtn);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("hi", "IN"));
            }
        });

        sendBtn.setOnClickListener(v -> {
            String userText = inputField.getText().toString();
            if (!userText.isEmpty()) {
                chatBox.append("\nममतेश: " + userText);
                inputField.setText("");
                askJarvis(userText);
            }
        });
    }

    private void askJarvis(String prompt) {
        new Thread(() -> {
            try {
                URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String systemPrompt = "तुम ममतेश के फोन में बैठे जार्विस हो। जवाब सीधा और हिंदी में देना। अगर क्लिक करना हो तो [CLICK: X, Y] लिखना।";
                String jsonBody = "{\"system_instruction\":{\"parts\":{\"text\":\"" + systemPrompt + "\"}},\"contents\":[{\"parts\":[{\"text\":\"" + prompt + "\"}]}]}";

                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.getBytes("UTF-8"));
                os.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) { response.append(line); }
                br.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray candidates = jsonResponse.getJSONArray("candidates");
                String reply = candidates.getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");

                // क्लिक कमांड चेक करना
                if (reply.contains("[CLICK:")) {
                    String[] parts = reply.split("CLICK:")[1].split("]")[0].split(",");
                    float x = Float.parseFloat(parts[0].trim());
                    float y = Float.parseFloat(parts[1].trim());
                    if(JarvisAccessibilityService.instance != null) {
                        JarvisAccessibilityService.instance.performClick(x, y);
                    }
                }

                String cleanReply = reply.replaceAll("\\[.*?\\]", "").trim();
                
                runOnUiThread(() -> {
                    chatBox.append("\nजार्विस: " + cleanReply);
                    tts.speak(cleanReply, TextToSpeech.QUEUE_FLUSH, null, null);
                });

            } catch (Exception e) {
                runOnUiThread(() -> chatBox.append("\n[सिस्टम एरर]: " + e.getMessage()));
            }
        }).start();
    }
}
