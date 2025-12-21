package com.xae_xii.commands;

import com.xae_xii.XaeBot;

public class Logout implements Command {
    public void execute(long chatId, String text, XaeBot bot) {
        bot.getMessenger().sendMsg(chatId, "You have been logged out. Goodbye!👋", XaeBot.preference, "a3on");
        bot.setState(false);
    }
}
