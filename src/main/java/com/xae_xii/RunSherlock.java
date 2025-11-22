package com.xae_xii;
import java.io.BufferedReader;
import java.io.InputStreamReader;
public class RunSherlock {
    public static void main(String[] args) {
        try {
            // Command to execute
            ProcessBuilder pb = new ProcessBuilder(
                "C:\\Users\\Ahmad\\AppData\\Local\\Programs\\Python\\Python310\\Scripts\\sherlock.exe", "sherlock", "elon musk"
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Read output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
