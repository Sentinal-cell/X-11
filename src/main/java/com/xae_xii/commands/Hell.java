package com.xae_xii.commands;
import com.xae_xii.XaeBot;
public class Hell implements Command {
    @Override
    public void execute(long chatId, String text, XaeBot bot) {
        bot.getMessenger().sendMsg(chatId, "Hello there! 👋 How are you?");
    }
}
