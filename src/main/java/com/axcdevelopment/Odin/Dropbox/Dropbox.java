package com.axcdevelopment.Odin.Dropbox;

import com.axcdevelopment.Odin.Discord.Discord;
import com.axcdevelopment.Odin.Server.Server;
import com.axcdevelopment.Odin.Support.BotData;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.WriteMode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import net.dv8tion.jda.api.entities.Guild;

import java.io.*;
import java.nio.file.Files;

/**
 * @author Alan Xiao (axcdevelopment@gmail.com)
 */

public class Dropbox {

    protected static DbxRequestConfig config;
    protected static DbxClientV2 client;

    public static void sync() {
        config = DbxRequestConfig.newBuilder("odin").build();
        client = new DbxClientV2(config, System.getenv("DropboxToken"));
        downloadDropboxInfo();

    }

    public static void uploadBotInfo() {
        try {
            FileWriter fileWriter = new FileWriter("BotData.txt", false);
            fileWriter.write(BotData.asString());
            fileWriter.close();
            File file = new File("BotData.txt");
            InputStream in = new FileInputStream(file);
            client.files().uploadBuilder("/v5/" + "BotData.txt")
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(in);
        } catch (IOException | DbxException e) {
            e.printStackTrace();
        }
    }

    public static void downloadDropboxInfo() {
        try {
            Gson gson = new Gson();
            ListFolderResult folder = client.files().listFolder("/v5");
            for (Metadata metadata : folder.getEntries()) {
                if (metadata.getName().endsWith("BotData.txt")) {
                    OutputStream outputStream = new FileOutputStream(metadata.getName());
                    client.files().downloadBuilder("/v5/" + metadata.getName()).download(outputStream);
                    String data = Files.readString(new File("BotData.txt").toPath());
                    BotData.fromString(data);
                }
                if (!metadata.getName().endsWith(".json"))
                    continue;
                Guild guild = Discord.getJda().getGuildById(metadata.getName().substring(0, metadata.getName().length() - 5));
                if (guild == null)
                    continue;
                String serverName = guild.getName();
                System.out.println("Registering Old Server: " + serverName);
                OutputStream outputStream = new FileOutputStream(metadata.getName());
                client.files().downloadBuilder("/v5/" + metadata.getName()).download(outputStream);
                JsonReader jsonReader = new JsonReader(new FileReader(metadata.getName()));
                if (Discord.getServers().add(gson.fromJson(jsonReader, Server.class)))
                    System.out.println("Successfully Registered.");
                else
                    System.out.println("Registration Failed.");
            }
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void uploadServerInfo(Server server) {
        try {
            Guild guild = Discord.getJda().getGuildById(server.getGuildId());
            if (guild == null)
                return;
            String filename = server.getGuildId() + ".json";
            FileWriter fileWriter = new FileWriter(filename, false);
            fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(server));
            fileWriter.close();
            File file = new File(filename);
            InputStream in = new FileInputStream(file);
            client.files().uploadBuilder("/v5/" + filename)
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(in);
        } catch (IOException | DbxException e) {
            e.printStackTrace();
        }

    }



}
