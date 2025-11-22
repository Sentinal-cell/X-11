package com.xae_xii.commands;

import com.xae_xii.Messenger;
import com.xae_xii.XaeBot;

public class chatmode implements Command {
    private boolean active = false;

    @Override
    public void execute(long chatId, String text, XaeBot bot) {
        Messenger messenger = bot.getMessenger();

        if (!active) {
            messenger.sendMsg(chatId, "Entered Chat Mode. Type /exit to leave.");
            active = true;
            bot.setMode("chat");
        } else {
            String msg = text.trim();

            if (msg.equalsIgnoreCase("/exit")) {
                messenger.sendMsg(chatId, "Exited Chat Mode.");
                active = false;
                bot.setMode("none");
            } else {
                messenger.sendMsg(chatId, "[Chat Mode] You said: " + msg);
            }
        }
    }
}
