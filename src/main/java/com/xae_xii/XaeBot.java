package com.xae_xii;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.xae_xii.commands.Command;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.xae_xii.commands.Hell;
import com.xae_xii.commands.Logout;

public class XaeBot extends TelegramLongPollingBot {
    private static final Dotenv dotenv = Dotenv.load();
    private Messenger messenger = new Messenger(this);
    private Map<String, Command> commands = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(XaeBot.class);
    private static final long TIMEOUT_MINUTES = 5;
    private volatile long lastActivityTime;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean state = false;
    private long chatId;
    private long userId;
    private String mode = "none";
    private static final long ALLOWED_USER_ID = Long.parseLong(dotenv.get("ALLOWED_USER_ID"));
    public XaeBot(){
        commands.put("start", new Hell());
        commands.put("logout", new Logout());
        startSessionWatcher();
    }
    private void startSessionWatcher() {
        scheduler.scheduleAtFixedRate(() -> {
            if (state) {
                long now = System.currentTimeMillis();
                long inactiveMinutes = (now - lastActivityTime) / 1000 / 60;
                if (inactiveMinutes >= TIMEOUT_MINUTES) {
                    state = false;
                    messenger.sendMsg(chatId, "Session expired due to inactivity. Please log in again.");
                    logger.info("User " + userId + " logged out automatically due to inactivity.");
                }
            }
        }, 1, 1, TimeUnit.MINUTES); // checks every minute
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public void setMode(String newMode) {
        this.mode = newMode;
    }
    public void setState(boolean newState) {
        this.state = newState;
        logger.info("Logout successful by "+userId);
    }
    @Override
    public String getBotUsername() {
        return "XAE_Xii_808Bot"; 
    }

    @Override
    public String getBotToken() {
        return dotenv.get("TELEGRAM_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            userId = update.getMessage().getFrom().getId();
            chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            lastActivityTime = System.currentTimeMillis();

            if (userId != ALLOWED_USER_ID) {
                System.out.println("Unauthorized user tried to access the bot: " + userId);
                return;
            }
            if(!state){
                String[] login_inf = update.getMessage().getText().split(" ");
                DB db = new DB();
                Auth auth = new Auth();
                if (login_inf.length < 2) {
                    messenger.sendMsg(chatId, "Please enter both username and code (e.g., `user 1234`)");
                    return;
                }
                if(db.check(login_inf[0], userId) && Integer.parseInt(login_inf[1]) == auth.otp()){
                    logger.info("Login successful by "+ userId);
                    String[] uinf = db.ret(login_inf[0]);
                    messenger.sendMsg(chatId, "Welcome "+ uinf[0]);
                    state = true;    
                } else {
                    if(db.check(login_inf[0], userId)){
                        messenger.sendMsg(chatId, "Authentication code incorrect...");
                    }else{
                    messenger.sendMsg(chatId, "Invalid credentials...");
                }
                }
            }else {
                if(text.startsWith("/")){
                    String cmd = text.substring(1).toLowerCase();
                    Command actions = commands.get(cmd);
                    if (actions != null) {
                        actions.execute(chatId, text, this);
                    } else {
                        messenger.sendMsg(chatId, "Unknown command: " + cmd);
                    }
                }
            }
        
    }
}


    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new XaeBot());
            logger.info("Bot started...");
        } catch (Exception e) {
            logger.error("Error starting bot: ", e);
        }
    }

}
