package com.xae_xii;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class Messenger {

    private final TelegramLongPollingBot bot;

    public Messenger(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    public void sendMsg(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            bot.execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
