package com.xae_xii;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Voice;
public class A3log {
    public String voiceFolderPath ="logs/vnc";
    private static final Logger logger = LogManager.getLogger(A3log.class);
    public void gen_chat(String text, String s){
        String W = null;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("logs/gen/main_log.txt", true))) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = "[" + now.format(formatter) + "]";
            switch (s) {
                case "G":
                    W = "Gemini";
                    break;
                case "T":
                    W = "x11";
                    break;
            }
            writer.write(formattedDateTime + "-" +W+ ": "+ text);
            writer.write("Another line");
            writer.newLine();
    }catch(IOException e){
        logger.error("Gemini error: ", e);
    }
        }
    public java.io.File logVoice(Voice voice, Long userId, XaeBot bot) throws Exception {
        java.io.File folder = new java.io.File(voiceFolderPath);
        if (!folder.exists()) folder.mkdirs();
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String filename = now.format(formatter) + "-" + userId + ".ogg";
        java.io.File output = new java.io.File(folder, filename);
        GetFile getFile = new GetFile(voice.getFileId());
        org.telegram.telegrambots.meta.api.objects.File tgFile = bot.execute(getFile);
        try (InputStream is = new URL("https://api.telegram.org/file/bot" + bot.getBotToken() + "/" + tgFile.getFilePath()).openStream();
            FileOutputStream fos = new FileOutputStream(output)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
    }
        return output;
}
}
