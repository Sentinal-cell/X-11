package com.xae_xii;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main extends TelegramLongPollingBot {
    public boolean state = false;

    // Replace with your real Telegram user ID
    private static final long ALLOWED_USER_ID = 7432819887L ;  

    @Override
    public String getBotUsername() {
        return "XAE_Xii_808Bot"; // from BotFather
    }

    @Override
    public String getBotToken() {
        return "8192327224:AAGiAXWvf5VhjOMshWR3-enZYrwQVI_v2MU"; // from BotFather
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long userId = update.getMessage().getFrom().getId();
            long chatId = update.getMessage().getChatId();

            // Only allow your account
            if (userId != ALLOWED_USER_ID) {
                System.out.println("Unauthorized user tried to access the bot: " + userId);
                return;
            }
            while(!state){
                String[] Login_inf = update.getMessage().getText().split(" ");
                SendMessage message = new SendMessage();
                message.setChatId(String.valueOf(chatId));
             //   message.setText("You kai said: " + messageText);
            }
            try {
               // execute(message); // sends reply
            } catch (Exception e) {
                e.printStackTrace();
            }
        
    }
}


    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Main());
            System.out.println("Bot started...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
