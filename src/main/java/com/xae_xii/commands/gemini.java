package com.xae_xii.commands;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.xae_xii.Messenger;
import com.xae_xii.XaeBot;
import io.github.cdimascio.dotenv.Dotenv;
public class gemini implements  Command{
    private boolean active = false;
    private static final Dotenv dotenv = Dotenv.load();
    private static final Logger logger = LogManager.getLogger(gemini.class);
    private static final String API_KEY = dotenv.get("API_KEY");
    private static final String MODEL = "gemini-1.5-flash";
    @Override
    public void execute(long chatId, String text, XaeBot bot) {
        Messenger messenger = bot.getMessenger();
        if (!active) {
            messenger.sendMsg(chatId, "Entered Gemini. Type /exit to leave.");
            active = true;
            bot.setMode("gemini");
        }else{
            Scanner scanner = new Scanner(System.in);
            HttpClient client = HttpClient.newHttpClient();
            List<String> history = new ArrayList<>(); // conversation history
           while (true) {
            String prompt = text.trim();
            if (prompt.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }
           }

        }





    }
    private static String extractText(String json) {
        String find = "\"text\":\"";
        int start = json.indexOf(find);

        if (start == -1) return "(No response)";
        start += find.length();
        int end = json.indexOf("\"", start);

        if (end == -1) return "(Malformed response)";

        return json.substring(start, end)
                   .replace("\\n", "\n")
                   .replace("\\\"", "\"");
    }
}
