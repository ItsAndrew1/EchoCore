package com.itsandrew.echocore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Level;

public class MoralityManager {
    private final Path dataFilePath;
    private final EchoCorePlugin plugin;

    public MoralityManager(Path modDir,  EchoCorePlugin plugin) {
        this.plugin = plugin;
        this.dataFilePath = modDir.resolve("data.json");
        load();
    }

    public void onPlayerJoin(UUID playerUUID){
        plugin.getLogger().at(Level.WARNING).log(dataFilePath.toString());

        //Adding the players that join in the data file
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject root = gson.fromJson(Files.readString(dataFilePath), JsonObject.class);

            //Getting the players object
            JsonObject players;
            if(root.has("players")) players = root.getAsJsonObject("players");
            else{
                players = new JsonObject();
                root.add("players", players);
            }

            //Attaching the morality to the player
            if(!players.has(playerUUID.toString())){
                JsonObject playerData = new JsonObject();
                playerData.addProperty("morality", 0);
                players.add(playerUUID.toString(), playerData);
            }

            //Saving the root
            Files.writeString(dataFilePath, gson.toJson(root));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getPlayerMorality(UUID playerUUID){
        try{
            Gson gson = new Gson();
            if(!Files.exists(dataFilePath)) return 0;

            JsonObject root = gson.fromJson(Files.readString(dataFilePath), JsonObject.class);
            if(root == null || !root.has("players")) return 0;

            JsonObject players = root.getAsJsonObject("players");
            if(!players.has(playerUUID.toString())) return 0;

            JsonObject playerData = players.getAsJsonObject(playerUUID.toString());
            if(!playerData.has("morality")) return 0;

            return playerData.get("morality").getAsInt();
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    private void load() {
        //Creating a .json file for storing data (if it doesn't exist already)
        if(Files.exists(dataFilePath)) return;

        try{
            Path modDir = plugin.getDataDirectory();
            Path dataFile = modDir.resolve("data.json");
            if(!Files.exists(modDir)) Files.createDirectories(modDir);
            if(!Files.exists(dataFile)){
                Files.createFile(dataFile);
                Files.writeString(dataFile, "{ \"players\": {} }");
            }
        } catch (Exception e){
            plugin.getLogger().at(Level.WARNING).log("Could not create data directory.");
        }
    }
}
