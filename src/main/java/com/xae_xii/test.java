package com.xae_xii;


import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class test {
    public static void main(String[] args) {
        // Get current time with system time zone
        ZonedDateTime now = ZonedDateTime.now();

        // Format like [yyyy-MM-dd HH:mm:ss]
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = "[" + now.format(formatter) + "]";

        // Example usage
        String myLogMessage = currentTime + " This is my log message";
        System.out.println(myLogMessage);
    }
}

