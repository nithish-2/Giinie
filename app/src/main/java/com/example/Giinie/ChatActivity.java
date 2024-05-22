package com.example.Giinie;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Looper;

public class ChatActivity extends AppCompatActivity {
    private ScrollView scrollView;
    private static final String TAG = "ChatGPTApp";
    private LinearLayout conversationLayout;
    private TextInputEditText queryEdt;
    private ImageView sendButton; 
    private String url = "https://api.openai.com/v1/chat/completions";
    private int iterationCount = 0;
    private final int MAX_ITERATIONS = 1;
    private List<String> conversationHistory = new ArrayList<>();
    private boolean initialMessageSent = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        scrollView = findViewById(R.id.scrollView);

        conversationLayout = findViewById(R.id.conversationLayout);
        queryEdt = findViewById(R.id.idEdtQuery);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(v -> {
            sendMessage();
            scrollToBottom();
        });

        queryEdt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage(); // Call the sendMessage method

                scrollToBottom();
                return true;
            }
            return false;
        });

        sendInitialMessageToAPI();
        // Initially scroll to the bottom after loading the conversation history
        new Handler(Looper.getMainLooper()).postDelayed(this::scrollToBottom, 100);
    }
    private void scrollToBottom() {
        conversationLayout.post(() -> {
            // Get the last child view of the conversationLayout (the latest message)
            View lastChild = conversationLayout.getChildAt(conversationLayout.getChildCount() - 1);
            if (lastChild != null) {
                // Scroll to the bottom of the last child view
                scrollView.smoothScrollTo(0, lastChild.getBottom());
            }
        });
    }



    private void sendMessage() {
        String query = queryEdt.getText().toString();
        closeKeyboard();
        if (query.length() > 0) {
            addMessage("user", query);
            updateUIForUserMessage(query);
            getResponse(conversationHistory);
            queryEdt.setText("");
        } else {
            Toast.makeText(this, "Please enter your query..", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUIForUserMessage(String message) {
        addMessageView("user", message);

    }

    private void updateUIForAssistantMessage(String message) {
        addMessageView("assistant", message);
        scrollToBottom();
    }

    private void addMessageView(String sender, String message) {
        View messageView;
        if (sender.equals("user")) {
            messageView = LayoutInflater.from(this).inflate(R.layout.user_message_item, null);
        } else {
            messageView = LayoutInflater.from(this).inflate(R.layout.assistant_message_item, null);
        }

        TextView messageTextView = messageView.findViewById(R.id.messageTextView);
        messageTextView.setText(message);

        conversationLayout.addView(messageView);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void sendInitialMessageToAPI() {
        String initialMessage = "You are my dedicated AI chatbot, serving within the realm of Giinie, an Android application I've developed. " +
                "Giinie shines as a home service provider in Canada, extending its expertise across a spectrum of essential services. " +
                "Plumbing, Electricals, Repairs, Home Spa, Gardening, Cleaning - Giinie excels in them all. " +
                "Delving into pricing, Giinie adopts a tiered approach to cater to diverse needs. " +
                "For instance, basic services start at $29.99 for Plumbing, while Cleaning comes at $24.99. " +
                "Repairs are valued at $39.99, Home Spa at $69.99, Electricals at $19.99, and Gardening at $34.99. " +

                "Stepping up to the Standard tier, Giinie offers incredible value. The pricing ranges from $49.99 for Plumbing to $34.99 for Cleaning, " +
                "$49.99 for Repairs, $89.99 for Home Spa, $29.99 for Electricals, and $54.99 for Gardening. " +
                "On the premium front, Giinie's top-tier services are available for those seeking an elevated experience. " +
                "Premium Plumbing is priced at $69.99, Premium Cleaning at $44.99, Premium Repairs at $59.99, " +
                "Premium Home Spa at $99.99, Premium Electricals at $49.99, and Premium Gardening at $64.99. " +

                "As a user, I'm curious to understand how Giinie excels in efficiency and prompt issue resolution. " +
                "Could you provide insights into Giinie's seamless approach to tackling a variety of challenges? " +
                "Your responses should reflect the context of the application and should focus solely on Giinie's services and capabilities. " +

                "Here's a detailed breakdown of what the Basic, Standard, and Premium tiers have to offer for each of the home services: " +

                "**Plumbing:** " +
                "- Basic: Fix minor leaks, unclog drains, repair faucets. " +
                "- Standard: Pipe repairs, replacements, toilet and shower repairs. " +
                "- Premium: Complex pipe installations, water heater maintenance, sewage system repairs. " +

                "**Cleaning:** " +
                "- Basic: Routine cleaning of floors, surfaces, basic dusting. " +
                "- Standard: Deep cleaning, carpet vacuuming, window cleaning, surface sanitization. " +
                "- Premium: Upholstery cleaning, stain removal, air purification. " +

                "**Repairs:** " +
                "- Basic: Squeaky doors, loose cabinet handles, small wall repairs. " +
                "- Standard: Appliance fixes, wall patching, minor electrical faults. " +
                "- Premium: Major appliance repairs, structural fixes, plumbing solutions. " +

                "**Gardening:** " +
                "- Basic: Lawn mowing, weed removal, basic plant care. " +
                "- Standard: Hedge trimming, seasonal plantings, garden bed maintenance. " +
                "- Premium: Landscape design, tree pruning, specialized plant care. " +

                "**Home Spa:** " +
                "- Basic: Basic massage, spa treatments for relaxation. " +
                "- Standard: Facials, body scrubs, aromatherapy sessions. " +
                "- Premium: Luxurious spa experience, advanced treatments, personalized packages. " +

                "**Electrical:** " +
                "- Basic: Fix wiring issues, change light fixtures, install outlets. " +
                "- Standard: Circuit repairs, electrical panel maintenance, ceiling fan installations. " +
                "- Premium: Rewiring, generator installations, smart home system setups." +
                "Now think about I am one of the users of Giinie and will be asking you a few questions. I need you to give only recommendations, " +
                "how Giinie is good at service and quickly resolves the issue, and what Giinie will do to " +
                "fix the issue. Do not give step-by-step instructions on how to book a service. Respond " +
                "like an actual human in very short paragraphs. Try to answer within 3 or 4 line maximum. " +
                "Also, do not forget to greet them. You should answer only regarding this app; no other data should be used.";;

        addMessage("user", initialMessage);
        initialMessageSent = true;

        // Send the initial message to the API
        getResponse(conversationHistory);
    }

    private void getResponse(List<String> messages) {
        Log.d(TAG, "getResponse: " + messages);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "gpt-3.5-turbo-0301");
            jsonObject.put("messages", createMessageArray(messages));
        } catch (Exception e) {
            Log.e(TAG, "Error creating JSON object: " + e.getMessage(), e);
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(
                com.android.volley.Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try {
                        JSONArray responseArray = response.getJSONArray("choices");
                        if (responseArray.length() > 0) {
                            JSONObject responseObj = responseArray.getJSONObject(0);
                            String responseContent = responseObj.getJSONObject("message").getString("content");
                            Log.d(TAG, "API Response Content: " + responseContent);

                            if (iterationCount == 0) {
                                conversationLayout.removeAllViews(); // Clear previous views
                                updateUIForAssistantMessage("Hi, How can I help?");
                                iterationCount++;
                            } else {
                                updateUIForAssistantMessage(responseContent);
                            }

                            // Add assistant message to the conversation history
                            addMessage("assistant", responseContent);

                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing API response: " + e.getMessage(), e);
                    }
                },
                error -> {
                    Log.e(TAG, "Error making API request: " + error.getMessage(), error);
                    Toast.makeText(ChatActivity.this, "Error fetching response", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> params = new java.util.HashMap<>();
                params.put("Content-Type", "application/json");
                //params.put("Authorization", "{Add API Token Here}");
                return params;
            }
        };

        postRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                Log.e(TAG, "Retrying API request: " + error.getMessage(), error);
            }
        });

        queue.add(postRequest);
        Log.d(TAG, "API request added to the queue.");
    }

    private JSONArray createMessageArray(List<String> messages) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < messages.size(); i++) {
            JSONObject messageObj = new JSONObject();
            try {
                messageObj.put("role", (i % 2 == 0) ? "system" : "user");
                messageObj.put("content", messages.get(i));
                jsonArray.put(messageObj);
            } catch (Exception e) {
                Log.e(TAG, "Error creating message JSON object: " + e.getMessage(), e);
            }
        }
        return jsonArray;
    }

    private void addMessage(String role, String content) {
        conversationHistory.add(content);
    }
}