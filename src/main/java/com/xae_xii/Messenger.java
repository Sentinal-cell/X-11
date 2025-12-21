package com.xae_xii;
import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
public class Messenger {
    A3log alog = new A3log();
    private final TelegramLongPollingBot bot;
    public Messenger(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    public void sendMsg(long chatId, String text, String preference, String caller) {
        switch (preference) {
            case "text":
                textmsg(chatId, text, caller);
                break;
            case "voice":
                voicemsg(chatId, text);
                break;
            case "any":

            default:
                break;
        }
    }
    public void textmsg(long chatId, String text, String call){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        alog.gen_chat(text, call);
        try {
            bot.execute(message);
        } catch (Exception e) {
        }
    }
    public void voicemsg(long chatId, String text){
        try {
            ZonedDateTime now = ZonedDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String filename = now.format(formatter) + "-" + chatId + ".ogg";
            // Here, call your Python script to generate TTS
            Process p = new ProcessBuilder(
                "python", "C:\\\\Users\\\\Ahmad\\\\Desktop\\\\a3on\\\\src\\\\main\\\\java\\\\com\\\\xae_xii\\\\converter.py", text, filename
            ).start();
            p.waitFor(); // Wait for Python to finish

            // Send the generated voice file
            SendVoice voiceMessage = new SendVoice();
            voiceMessage.setChatId(chatId);
            voiceMessage.setVoice(new InputFile(new File("logs/voice_files/" + filename)));
            bot.execute(voiceMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    public void selector(long chatId, String text){
        String choice = Math.random() < 0.75 ? "text" : "voice";
        if(choice.equals("text")){
            textmsg(chatId, text, "a3on");
        }else{
            voicemsg(chatId, text);
        }
    }
}
