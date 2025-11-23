package com.xae_xii.commands;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xae_xii.Messenger;
import com.xae_xii.XaeBot;

import io.github.cdimascio.dotenv.Dotenv;

public class gemini implements Command {

    private static final Dotenv dotenv = Dotenv.load();
    private static final Logger logger = LogManager.getLogger(gemini.class);
    private static final String API_KEY = dotenv.get("API_KEY");
    private static final String MODEL = "models/gemini-2.5-pro";

    // Persistent conversation memory
    private List<String> history = new ArrayList<>();

    @Override
    public void execute(long chatId, String text, XaeBot bot) {
        Messenger messenger = bot.getMessenger();

        // Handle /exit command
        if (text.equalsIgnoreCase("/exit")) {
            bot.setMode("none");
            history.clear();
            messenger.sendMsg(chatId, "Exited Gemini Mode.");
            return;
        }

        // Enter Gemini mode if not active
        if (!botModeIsGemini(bot)) {
            bot.setMode("gemini");
            messenger.sendMsg(chatId, "Entered Gemini Mode. Type /exit to leave.");
            history.clear();
            return;
        }

        try {
            String userMsg = text.trim();
            history.add("You: " + userMsg);

            String fullPrompt = buildFullPrompt();

            // Correct JSON for the API
            String body = "{ \"contents\": [{ \"parts\": [{ \"text\": \"" +
                    fullPrompt.replace("\"", "\\\"") + "\" }] }] }";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/" + MODEL + ":generateContent?key=" + API_KEY))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Save raw response to file
            try (FileWriter fw = new FileWriter("gemini_responses.txt")) { // append mode
                fw.write(response.body() + "\n\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
            // Read the entire file into a String
            String son = new String(Files.readAllBytes(Paths.get("gemini_responses.txt"))); 

            // Regex to match "text": "..."
            String regex = "\"text\"\\s*:\\s*\"([^\"]*)\"";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(son);

            if (matcher.find()) {
                String te = matcher.group(1);
                System.out.println(te);
            } else {
                System.out.println("Text not found!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
            logger.info("Status code: " + response.statusCode());

            // Parse response
            String output = extractText(response.body());
            history.add("Gemini: " + output);

            messenger.sendMsg(chatId, output);

        } catch (Exception e) {
            logger.error("Gemini error: ", e);
            messenger.sendMsg(chatId, "Error contacting Gemini.");
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
        try (FileWriter fw = new FileWriter("gemini_responses.txt")) { // append mode
                fw.write(json + "\n\n");
        } catch (IOException e) {
                e.printStackTrace();
        }
        try {
            // Read the entire file into a String
            String son = new String(Files.readAllBytes(Paths.get("gemini_responses.txt"))); 

            // Regex to match "text": "..."
            String regex = "\"text\"\\s*:\\s*\"([^\"]*)\"";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(son);

            if (matcher.find()) {
                String text = matcher.group(1);
                System.out.println(text);
                return text;
            } else {
                System.out.println("Text not found!");
                return "Text not found!";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "TEXT NOT FOUND";
    }
}
