package ExternalAPIs.Dropbox;

import Core.Main;
import Server.Server;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.WriteMode;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.dv8tion.jda.api.entities.Guild;

import java.io.*;
import java.util.Scanner;

public class Dropbox {

    protected static DbxRequestConfig config;
    protected static DbxClientV2 client;

    public static void bootUp() {
        config = DbxRequestConfig.newBuilder("odin").build();
        client = new DbxClientV2(config, System.getenv("DropboxToken"));
        syncLocalWithDropbox();

    }

    public static void syncMaintenance() {
        try {
            FileWriter fileWriter = new FileWriter("maintenance.txt", false);
            fileWriter.write(Main.nextMaintenanceDate + ":::" + Main.nextMaintenanceTime);
            fileWriter.close();
            File file = new File("maintenance.txt");
            InputStream in = new FileInputStream(file);
            client.files().uploadBuilder("/v4/" + "maintenance.txt")
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(in);
        } catch (IOException | DbxException e) {
            e.printStackTrace();
        }
    }

    public static void syncLocalWithDropbox() {
        try {
            ListFolderResult folder = client.files().listFolder("/v4");
            for (Metadata metadata : folder.getEntries()) {
                if (metadata.getName().endsWith("maintenance.txt")) {
                    OutputStream outputStream = new FileOutputStream(metadata.getName());
                    client.files().downloadBuilder("/v4/" + metadata.getName()).download(outputStream);
                    Scanner scanner = new Scanner(new File("maintenance.txt"));
                    String[] maintDateTime = scanner.nextLine().split(":::");
                    Main.nextMaintenanceDate = maintDateTime[0].equals("null") ? null : maintDateTime[0];
                    Main.nextMaintenanceTime = maintDateTime[1].equals("null") ? null : maintDateTime[1];
                    scanner.close();
                }
                if (!metadata.getName().endsWith(".json"))
                    continue;
                Guild guild = Main.getJda().getGuildById(metadata.getName().substring(0, metadata.getName().length() - 5));
                if (guild == null)
                    continue;
                String serverName = guild.getName();
                System.out.println("Registering Old Server: " + serverName);
                OutputStream outputStream = new FileOutputStream(metadata.getName());
                client.files().downloadBuilder("/v4/" + metadata.getName()).download(outputStream);
                JsonReader jsonReader = new JsonReader(new FileReader(metadata.getName()));
                Gson gson = new Gson();
                if (Main.getServerList().add(gson.fromJson(jsonReader, Server.class)))
                    System.out.println("Successfully Registered.");
                else
                    System.out.println("Registration Failed.");
            }
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void uploadServerToDropbox(Server server) {
        try {
            Guild guild = Main.getJda().getGuildById(server.getGuildId());
            if (guild == null)
                return;
            String filename = server.getGuildId() + ".json";
            FileWriter fileWriter = new FileWriter(filename, false);
            fileWriter.write(new Gson().toJson(server));
            fileWriter.close();
            File file = new File(filename);
            InputStream in = new FileInputStream(file);
            client.files().uploadBuilder("/v4/" + filename)
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(in);
        } catch (IOException | DbxException e) {
            e.printStackTrace();
        }

    }

    public static void deleteServer(Server server) {
        try {
            client.files().deleteV2("/v4/" + server.getGuildId() + ".json");
        } catch (DbxException e) {
            System.out.println("Failed to delete: " + server.getServerName() + " ::: " + server.getGuildId());
            e.printStackTrace();
        }
    }

}
