package com.xae_xii;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.xae_xii.commands.Command;
import com.xae_xii.commands.Hell;
import com.xae_xii.commands.Logout;
import com.xae_xii.commands.chatmode;
import com.xae_xii.commands.gemini;

import io.github.cdimascio.dotenv.Dotenv;

public class XaeBot extends TelegramLongPollingBot {
    private static final Dotenv dotenv = Dotenv.load();
    private Messenger messenger = new Messenger(this);
    private Map<String, Command> commands = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(XaeBot.class);
    private static final long TIMEOUT_MINUTES = 5;
    private volatile long lastActivityTime;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean state = false;
    public static String preference;
    private long chatId;
    private long userId;
    public String mode = "none";
    private final String folderPath = "vnc";
    private static final long ALLOWED_USER_ID = Long.parseLong(dotenv.get("ALLOWED_USER_ID"));

    public XaeBot() {
        commands.put("start", new Hell());
        commands.put("logout", new Logout());
        commands.put("chatmode", new chatmode());
        commands.put("gemini", new gemini());
        startSessionWatcher();
    }

    private void startSessionWatcher() {
        scheduler.scheduleAtFixedRate(() -> {
            if (state) {
                long now = System.currentTimeMillis();
                long inactiveMinutes = (now - lastActivityTime) / 1000 / 60;
                if (inactiveMinutes >= TIMEOUT_MINUTES) {
                    state = false;
                    messenger.sendMsg(chatId, "Session expired due to inactivity. Please log in again.", "text");
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
        logger.info("Logout successful by " + userId);
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
        if (update.hasCallbackQuery()) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (callbackData.equals("voice")) {
            messenger.sendMsg(chatId, "You chose Voice 🔊", "text");
           preference = "voice";
        } else if (callbackData.equals("text")) {
            messenger.sendMsg(chatId, "You chose Text 💬", "text");
            preference = "text";
        } else if (callbackData.equals("any")) {
            messenger.sendMsg(chatId, "You chose any 🔊&💬", "text");
            preference = "any";
        }
        return; 
    }

        // AUDIO
        if (update.hasMessage()) {
            Message msg = update.getMessage();
            chatId = update.getMessage().getChatId();
            userId = update.getMessage().getFrom().getId();
            if (msg.hasVoice()) {
                if(state){
                    String fileId = msg.getVoice().getFileId();
                    System.out.println("User sent voice message: " + fileId);
                    try {
                        java.io.File folder = new java.io.File(folderPath);
                        if (!folder.exists()) {
                            folder.mkdirs(); // creates the folder if it doesn't exist
                        }
                        ZonedDateTime now = ZonedDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
                        String currentTime_id = now.format(formatter) +"-"+ String.valueOf(userId);
                        java.io.File output = new java.io.File(folder, currentTime_id+".ogg");
                        GetFile getFile = new GetFile(fileId);
                        org.telegram.telegrambots.meta.api.objects.File tgFile = execute(getFile);
                        downloadFile(tgFile, output);
                        System.out.println("Downloaded voice message to: " + output.getAbsolutePath());
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
            }else{
                messenger.sendMsg(chatId, "Please enter both username and code by text (e.g., `user 1234`)", "text");
            }
            }

            // audio file
            if (msg.hasAudio()) {
                String fileId = msg.getAudio().getFileId();
                System.out.println("User sent audio file: " + fileId);
            }
        }
        // --- TEXT HANDLER 
        if (update.hasMessage() && update.getMessage().hasText()) {
            userId = update.getMessage().getFrom().getId();
            chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            lastActivityTime = System.currentTimeMillis();

            if (userId != ALLOWED_USER_ID) {
                logger.info("Unauthorized user tried to access the bot: " + userId);
                return;
            }

            if (!state) {
                String[] login_inf = update.getMessage().getText().split(" ");
                DB db = new DB();
                Auth auth = new Auth();
                if (login_inf.length < 2) {
                    messenger.sendMsg(chatId, "Please enter both username  (e.g., `user 1234`)", "text");
                    return;
                }
                if (db.check(login_inf[0], userId) && Integer.parseInt(login_inf[1]) == auth.otp()) {
                    logger.info("Login successful by " + userId);
                    String[] uinf = db.ret(login_inf[0]);
                    messenger.sendMsg(chatId, "Welcome " + uinf[0], "text");
                    state = true;
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("Do you prefer text messages 💬 or voice replies 🔊?");
                    InlineKeyboardButton voicer = new InlineKeyboardButton();
                    voicer.setText("Voice🔊");
                    voicer.setCallbackData("voice");
                    InlineKeyboardButton textr = new InlineKeyboardButton();
                    textr.setText("Text💬");
                    textr.setCallbackData("text");
                    InlineKeyboardButton v_t = new InlineKeyboardButton();
                    v_t.setText("Any");
                    v_t.setCallbackData("any");
                    List<InlineKeyboardButton> row = new ArrayList<>();
                    row.add(voicer);
                    row.add(textr);
                    row.add(v_t);

                    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                    keyboard.add(row);

                    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                    markup.setKeyboard(keyboard);

                    message.setReplyMarkup(markup);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (db.check(login_inf[0], userId)) {
                        messenger.sendMsg(chatId, "Authentication code incorrect...", "text");
                    } else {
                        messenger.sendMsg(chatId, "Invalid credentials...", "text");
                    }
                }
            } else {

                // <-- MODE HANDLER
                if (text.startsWith("/")) {
                    String cmd = text.substring(1).toLowerCase();

                    if (cmd.equals("chatmode")) {
                        mode = "chat";
                        messenger.sendMsg(chatId, "Entered Chat Mode. Type /exit to leave.", "text");
                        return;
                    } else if (cmd.equals("gemini")) {
                        commands.get("gemini").execute(chatId, text, this);
                        return;
                    } else if (cmd.equals("exit")) {
                        mode = "none";
                        messenger.sendMsg(chatId, "Exited current mode.", "text");
                        return;
                    }

                    Command actions = commands.get(cmd);
                    if (actions != null) {
                        actions.execute(chatId, text, this);
                    } else {
                        messenger.sendMsg(chatId, "Unknown command: " + cmd, "text");
                    }
                } else {
                    if (mode.equals("gemini")) {
                        commands.get("gemini").execute(chatId, text, this);
                        return;
                    }
                    if (mode.equals("chat")) {
                        messenger.sendMsg(chatId, "[Chat Mode] You said: " + text, "text");
                    } else {
                        messenger.sendMsg(chatId, "You said: " + text, "text");
                    }
                }
                // <-- MODE
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
    public String getPref(){
        return preference;
    }
}
