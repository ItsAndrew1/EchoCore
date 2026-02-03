//Developed by _ItsAndrew_
package com.itsandrew.echocore;

import com.google.gson.*;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.npc.INonPlayerCharacter;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import it.unimi.dsi.fastutil.Pair;

import java.awt.*;
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

    public MoralityManager(Path modDir, EchoCorePlugin plugin) {
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
                playerData.add("milestones", milestones);
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

            //Sends the player a message in chat about this
            Message message = Message.join(Message.raw("You reached milestone 60! Because of this, you got ").color(Color.CYAN), Message.raw("30 "+randomOre.getItem().getId()).color(Color.GREEN), Message.raw("!").color(Color.CYAN));
            player.sendMessage(message);

            //Applies the milestone
            applyMilestone(playerRef, 60);
            return;
        }
        if(getPlayerMorality(playerRef.getUuid()) >= 80 && !hasPlayerReachedMilestone(playerRef, 80)){
            //Adding some Mithril tools in a list
            List<ItemStack> rewardItems = new ArrayList<>();
            rewardItems.add(new ItemStack("Tool_Pickaxe_Mithril"));
            rewardItems.add(new ItemStack("Tool_Hatchet_Mithril"));

            ItemStack randomTool = rewardItems.get(new Random().nextInt(rewardItems.size()));
            player.getInventory().getStorage().addItemStack(randomTool);

            //Sends the player a message in chat about this
            Message message = Message.join(Message.raw("You reached milestone 80! Because of this, you got a ").color(Color.CYAN), Message.raw(randomTool.getItem().getId()).color(Color.GREEN), Message.raw("!").color(Color.CYAN));
            player.sendMessage(message);

            //Applies the milestone
            applyMilestone(playerRef, 80);
            return;
        }
        if(getPlayerMorality(playerRef.getUuid()) >= 100 && !hasPlayerReachedMilestone(playerRef, 100)){
            //Resets their morality if it gets to or over 100
            setPlayerMorality(playerRef.getUuid(), -getPlayerMorality(playerRef.getUuid()));

            List<ItemStack> rewardItems = new ArrayList<>();
            rewardItems.add(new ItemStack("Weapon_Axe_Mithril"));
            rewardItems.add(new ItemStack("Weapon_Sword_Mithril"));
            rewardItems.add(new ItemStack("Weapon_Shortbow_Mithril"));
            rewardItems.add(new ItemStack("Weapon_Mace_Mithril"));
            rewardItems.add(new ItemStack("Weapon_Longsword_Mithril"));
            rewardItems.add(new ItemStack("Weapon_Daggers_Mithril"));
            rewardItems.add(new ItemStack("Weapon_Club_Mithril"));
            rewardItems.add(new ItemStack("Weapon_Battleaxe_Mithril"));

            ItemStack randomWeapon = rewardItems.get(new Random().nextInt(rewardItems.size()));
            player.getInventory().getStorage().addItemStack(randomWeapon);

            //Sends the player a message in chat about this
            Message message = Message.join(Message.raw("You reached milestone 100! Because of this, you got ").color(Color.CYAN), Message.raw(randomWeapon.getItem().getId()).color(Color.GREEN), Message.raw("!").color(Color.CYAN));
            player.sendMessage(message);

            //Applies the milestone
            applyMilestone(playerRef, 100);
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

            //Sends the player a message in chat about this
            Message message = Message.join(Message.raw("You reached milestone 40! Because of this, you got ").color(Color.CYAN), Message.raw("50 "+randomLeather.getItem().getId()).color(Color.GREEN), Message.raw("!").color(Color.CYAN));
            player.sendMessage(message);

            //Applies the milestone
            applyMilestone(playerRef, 40);
            return;
        }
        if(getPlayerMorality(playerRef.getUuid()) >= 20 && !hasPlayerReachedMilestone(playerRef, 20)){
            //Giving the player some Caesar Salad
            ItemStack caesarSalad = new ItemStack("Food_Salad_Caesar", 40);
            player.getInventory().getStorage().addItemStack(caesarSalad);

            //Sends the player a message in chat about this
            Message message = Message.join(Message.raw("You reached milestone 20! Because of this, you got ").color(Color.CYAN), Message.raw("40 Caesar Salads").color(Color.GREEN), Message.raw("!").color(Color.CYAN));
            player.sendMessage(message);

            //Applies the milestone
            applyMilestone(playerRef, 20);
        }
    }

    private void applyMilestone(PlayerRef playerRef, int milestone){
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
            milestones.add(milestone);
            Files.writeString(dataFilePath, gson.toJson(root));
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void applyPunishment(Player player, PlayerRef playerRef){
        //Spawning different entities to the player
        List<String> entityIds = new ArrayList<>();
        entityIds.add("Goblin_Ogre");
        entityIds.add("Toad_Rhino_Magma");
        entityIds.add("Trork_Hunter");

        //Checking if the player reached a specific negative milestone
        if(getPlayerMorality(playerRef.getUuid()) <= -100){
            //Resetting the player's morality to 0
            setPlayerMorality(playerRef.getUuid(), -getPlayerMorality(playerRef.getUuid()));

            //Clears the player's inventory.
            player.getInventory().getStorage().clear();

            //Sends a chat message
            player.sendMessage(Message.raw("AEON-79 took all the items from your inventory!").color(Color.CYAN));
            return;
        }
        if(getPlayerMorality(playerRef.getUuid()) <= -80){
            //Spawns 4 enemies
            spawnEntity(player, playerRef, entityIds.get(new Random().nextInt(entityIds.size())));
            spawnEntity(player, playerRef, entityIds.get(new Random().nextInt(entityIds.size())));
            spawnEntity(player, playerRef, entityIds.get(new Random().nextInt(entityIds.size())));
            spawnEntity(player, playerRef, entityIds.get(new Random().nextInt(entityIds.size())));

            player.sendMessage(Message.raw("AEON-79 summoned 4 enemies!").color(Color.CYAN));
            return;
        }

        if(getPlayerMorality(playerRef.getUuid()) <= -60){
            //Spawns 2 enemies
            spawnEntity(player, playerRef, entityIds.get(new Random().nextInt(entityIds.size())));
            spawnEntity(player, playerRef, entityIds.get(new Random().nextInt(entityIds.size())));

            player.sendMessage(Message.raw("AEON-79 summoned 2 enemies!").color(Color.CYAN));
            return;
        }

        if(getPlayerMorality(playerRef.getUuid()) <= -40){
            //Removes 3 items from the player's inventory
            removeItemFromPlayerInv(player);
            removeItemFromPlayerInv(player);
            removeItemFromPlayerInv(player);

            //Sending a chat message about this.
            player.sendMessage(Message.join(Message.raw("AEON-79 took 3 items from your inventory because you reached a morality of -40 or lower!").color(Color.CYAN)));
            return;
        }

        if(getPlayerMorality(playerRef.getUuid()) <= -20){
            //Removes 1 item from the player's inventory
            removeItemFromPlayerInv(player);

            //Sending a chat message
            player.sendMessage(Message.raw("AEON-79 took 1 item from your inventory because you reached a morality of -20 or lower!").color(Color.CYAN));
        }
    }

    private void removeItemFromPlayerInv(Player player){
        Inventory playerInv = player.getInventory();
        ItemContainer container = playerInv.getStorage();

        container.removeItemStackFromSlot((short) new Random().nextInt(container.getCapacity()));
    }

    //Helper method to spawn entities.
    private void spawnEntity(Player player, PlayerRef playerRef, String entityId){
        World playerWorld = player.getWorld();

        EntityStore store = playerWorld.getEntityStore();

        //Getting the entity's location
        TransformComponent trComponent = store.getStore().getComponent(playerRef.getReference(), EntityModule.get().getTransformComponentType());
        double entityX = trComponent.getPosition().getX() + new Random().nextInt(10);
        double entityZ = trComponent.getPosition().getZ() + new Random().nextInt(10);
        Vector3d entityPosition = new  Vector3d(entityX, trComponent.getPosition().getY(), entityZ);
        Vector3f entityRotation = new Vector3f(0, 0, 0);

        playerWorld.execute(() -> {
            Pair<Ref<EntityStore>, INonPlayerCharacter> result = NPCPlugin.get().spawnNPC(store.getStore(), entityId, null, entityPosition, entityRotation);
        });
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
