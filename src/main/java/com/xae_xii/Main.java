package com.xae_xii;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main extends TelegramLongPollingBot {
    private Messenger messenger = new Messenger(this);
    private static final Logger logger = LogManager.getLogger(Main.class);
    public boolean state = false;

    // Replace with your real Telegram user ID
    private static final long ALLOWED_USER_ID = 7432819887L ;  

    @Override
    public String getBotUsername() {
        return "XAE_Xii_808Bot"; 
    }

    @Override
    public String getBotToken() {
        return "8192327224:AAGiAXWvf5VhjOMshWR3-enZYrwQVI_v2MU";
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
            if(!state){
                String[] Login_inf = update.getMessage().getText().split(" ");
                DB db = new DB();
                Auth auth = new Auth();
                if(db.check(Login_inf[0], userId) && Integer.parseInt(Login_inf[1]) == auth.otp()){
                    logger.info("Log in successful by "+ userId);
                    String[] uinf = db.ret(Login_inf[0]);
                    messenger.sendMsg(chatId, "Welcome "+ uinf[0]);
                    state =true;    
                } else {
                    if(db.check(Login_inf[0], userId)){
                        messenger.sendMsg(chatId, "Authentication code incorrect...");
                    }else{
                    messenger.sendMsg(chatId, "Invalid credentials...");
                }
                }
            }else {

            }
        
    }
}


    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Main());
            logger.info("Bot started...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
