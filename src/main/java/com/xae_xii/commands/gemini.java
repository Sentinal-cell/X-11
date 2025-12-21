package com.xae_xii.commands;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.xae_xii.Messenger;
import com.xae_xii.XaeBot;

import io.github.cdimascio.dotenv.Dotenv;

public class gemini implements Command {

    private static final Dotenv dotenv = Dotenv.load();
    private static final Logger logger = LogManager.getLogger(gemini.class);
    private static final String API_KEY = dotenv.get("API_KEY");
    private static final String MODEL = "models/gemini-2.5-flash-lite";

    private final List<String> history = new ArrayList<>();

    @Override
    public void execute(long chatId, String text, XaeBot bot) {
        Messenger messenger = bot.getMessenger();

        // Exit Gemini mode
        if ("/exit".equalsIgnoreCase(text)) {
            bot.setMode("none");
            history.clear();
            messenger.sendMsg(chatId, "Exited Gemini Mode.", XaeBot.preference, "gemini");
            return;
        }

        // Enter Gemini mode if not active
        if (!botModeIsGemini(bot)) {
            bot.setMode("gemini");
            messenger.sendMsg(chatId, "Entered Gemini Mode. Type /exit to leave.", XaeBot.preference, "gemini");
            history.clear();
            return;
        }

        try {
            String userMsg = text.trim();
            history.add("You: " + userMsg);

            String fullPrompt = buildFullPrompt();

            // Create request body
            String body = "{ \"contents\": [{ \"parts\": [{ \"text\": \"" +
                    fullPrompt.replace("\"", "\\\"") + "\" }] }] }";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/" + MODEL + ":generateContent?key=" + API_KEY))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            logger.info("Status code: " + response.statusCode());

            // Extract text from response
            String output = extractText(response.body());
            history.add("Gemini: " + output);

            messenger.sendMsg(chatId, output, XaeBot.preference, "gemini");

        } catch (Exception e) {
            logger.error("Gemini error: ", e);
            messenger.sendMsg(chatId, "Error contacting Gemini.", XaeBot.preference, "gemini");
        }
    }

    private boolean botModeIsGemini(XaeBot bot) {
        return bot != null && "gemini".equals(bot.mode);
    }

    private String buildFullPrompt() {
        StringBuilder sb = new StringBuilder();
        for (String msg : history) sb.append(msg).append("\n");
        sb.append("Gemini:");
        return sb.toString();
    }

    private static String extractText(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray candidates = obj.getJSONArray("candidates");
            JSONObject firstCandidate = candidates.getJSONObject(0);
            JSONObject content = firstCandidate.getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");
            String mainText = parts.getJSONObject(0).getString("text");

            // Write JSON to file
            try (FileWriter fw = new FileWriter("gemini_responses.txt", true)) { // append mode
                fw.write(json + "\n\n");
            } catch (IOException e) {
                logger.error("Failed to write Gemini response to file", e);
            }
            return mainText;
        } catch (Exception e) {
            logger.error("Failed to extract text from Gemini response", e);
            return "text not found";
        }
    }
}
