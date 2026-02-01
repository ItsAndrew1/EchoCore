//Developed by _ItsAndrew_
package com.itsandrew.echocore;

import com.google.gson.*;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

                JsonArray milestones =  new JsonArray();
                players.add("milestones", milestones);
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

    public void setPlayerMorality(UUID playerUUID, int morality){
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            if(!Files.exists(dataFilePath)) return;

            JsonObject root = gson.fromJson(Files.readString(dataFilePath), JsonObject.class);
            if(root == null || !root.has("players")) return;

            JsonObject players = root.getAsJsonObject("players");
            if(!players.has(playerUUID.toString())) return;
            root.add("players", players);

            JsonObject playerData = players.getAsJsonObject(playerUUID.toString());
            if(!playerData.has("morality")) return;
            playerData.addProperty("morality", getPlayerMorality(playerUUID) + morality);
            players.add(playerUUID.toString(), playerData);

            Files.writeString(dataFilePath, gson.toJson(root));
        } catch (Exception e){
            e.printStackTrace();
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

    public void applyReward(Player player, PlayerRef playerRef){
        //Giving some rewards to the player based on the milestone
        if(getPlayerMorality(playerRef.getUuid()) >= 60 && !hasPlayerReachedMilestone(playerRef, 60)){
            //Adding some ore rewards in a list of ItemStacks
            List<ItemStack> rewardItems = new ArrayList<>();
            rewardItems.add(new ItemStack("Ore_Adamantite", 30));
            rewardItems.add(new ItemStack("Ore_Gold", 30));
            rewardItems.add(new ItemStack("Ore_Cobalt", 30));
            rewardItems.add(new ItemStack("Ore_Thorium", 30));
            rewardItems.add(new ItemStack("Ore_Silver", 30));

            ItemStack randomOre = rewardItems.get(new Random().nextInt(rewardItems.size()));
            player.getInventory().getStorage().addItemStack(randomOre);
            return;
        }
        if(getPlayerMorality(playerRef.getUuid()) >= 80 && !hasPlayerReachedMilestone(playerRef, 80)){
            //Adding some Mithril tools in a list
            List<ItemStack> rewardItems = new ArrayList<>();
            rewardItems.add(new ItemStack("Tool_Pickaxe_Mithril"));
            rewardItems.add(new ItemStack("Tool_Hatchet_Mithril"));

            ItemStack randomTool = rewardItems.get(new Random().nextInt(rewardItems.size()));
            player.getInventory().getStorage().addItemStack(randomTool);
            return;
        }
        if(getPlayerMorality(playerRef.getUuid()) >= 100 && !hasPlayerReachedMilestone(playerRef, 100)){
            //Resets their morality if it gets to or over 100
            setPlayerMorality(playerRef.getUuid(), 0);

            List<ItemStack> rewardItems = new ArrayList<>();
            rewardItems.add(new ItemStack("Weapon_Axe_Mithril"));
            rewardItems.add(new ItemStack("Weapon_Sword_Mithril"));
            rewardItems.add(new ItemStack("Weapon_Shortbow_Mithril"));
            rewardItems.add(new ItemStack("Weapon_Mace_Mithril"));
            rewardItems.add(new ItemStack("Weapon_Longsword_Mithril"));
            rewardItems.add(new ItemStack("Weapon_Daggers_Mithril"));
            rewardItems.add(new ItemStack("Weapon_Club_Mithril"));
            rewardItems.add(new ItemStack("Weapon_Battleaxe_Mithril"));

            ItemStack randomTool = rewardItems.get(new Random().nextInt(rewardItems.size()));
            player.getInventory().getStorage().addItemStack(randomTool);
            return;
        }
        if(getPlayerMorality(playerRef.getUuid()) >= 40 && !hasPlayerReachedMilestone(playerRef, 40)){
            //Adding some types of leathers in a list
            List<ItemStack> rewardItems = new ArrayList<>();
            rewardItems.add(new ItemStack("Ingredient_Leather_Heavy", 50));
            rewardItems.add(new ItemStack("Ingredient_Leather_Medium", 50));
            rewardItems.add(new ItemStack("Ingredient_Leather_Light", 50));

            ItemStack randomLeather =  rewardItems.get(new Random().nextInt(rewardItems.size()));
            player.getInventory().getStorage().addItemStack(randomLeather);
            return;
        }
        if(getPlayerMorality(playerRef.getUuid()) >= 20 && !hasPlayerReachedMilestone(playerRef, 20)){
            //Giving the player some Caesar Salad
            ItemStack caesarSalad = new ItemStack("Food_Salad_Caesar", 40);
            player.getInventory().getStorage().addItemStack(caesarSalad);
        }

        //Adding the milestone in the data.json file so the players don't abuse it.
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            if(!Files.exists(dataFilePath)) return;

            JsonObject root = gson.fromJson(Files.readString(dataFilePath), JsonObject.class);
            if(root == null || !root.has("players")) return;

            JsonObject players = root.getAsJsonObject("players");
            if(!players.has(playerRef.getUuid().toString())) return;
            root.add("players", players);

            JsonObject playerData = players.getAsJsonObject(playerRef.getUuid().toString());
            if(!playerData.has("morality") || !playerData.has("milestones")) return;
            JsonArray milestones = playerData.getAsJsonArray("milestones");

            //Adding milestone
            if(getPlayerMorality(playerRef.getUuid()) >= 100 && !hasPlayerReachedMilestone(playerRef, 100)) milestones.add(100);
            if(getPlayerMorality(playerRef.getUuid()) >= 80 && !hasPlayerReachedMilestone(playerRef, 80)) milestones.add(80);
            if(getPlayerMorality(playerRef.getUuid()) >= 60 && !hasPlayerReachedMilestone(playerRef, 60)) milestones.add(60);
            if(getPlayerMorality(playerRef.getUuid()) >= 40 && !hasPlayerReachedMilestone(playerRef, 40)) milestones.add(40);
            if(getPlayerMorality(playerRef.getUuid()) >= 20 && !hasPlayerReachedMilestone(playerRef, 20)) milestones.add(20);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean hasPlayerReachedMilestone(PlayerRef playerRef, int milestone){
        boolean hasReached = false;
        try{
            Gson gson = new Gson();
            if(!Files.exists(dataFilePath)) return false;

            JsonObject root = gson.fromJson(Files.readString(dataFilePath), JsonObject.class);
            if(root == null || !root.has("players")) return false;

            JsonObject players = root.getAsJsonObject("players");
            if(!players.has(playerRef.getUuid().toString())) return false;
            JsonObject playerData = players.getAsJsonObject(playerRef.getUuid().toString());

            if(!playerData.has("milestones") || !playerData.has("morality")) return false;
            JsonArray milestones = playerData.getAsJsonArray("milestones");
            for(JsonElement e : milestones){
                if(e.getAsInt() == milestone) {
                    hasReached = true;
                    break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return hasReached;
    }
}
