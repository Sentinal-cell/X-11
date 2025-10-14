package com.xae_xii.commands;

import com.xae_xii.XaeBot;

public interface Command {
    void execute(long chatId, String text, XaeBot bot);
}
