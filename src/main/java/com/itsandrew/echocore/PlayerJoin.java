package com.itsandrew.echocore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonObject;
import com.hypixel.hytale.builtin.adventure.memories.component.PlayerMemories;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PlayerJoin {
    public static void onPlayerReady(PlayerReadyEvent event){
        Player player = event.getPlayer();

        Ref<EntityStore> ref = player.getReference();
        World world = player.getWorld();
        EntityStore store = world.getEntityStore();
        PlayerRef playerRef = store.getStore().getComponent(ref, PlayerRef.getComponentType());
        PlayerMemories memories = store.getStore().getComponent(ref, PlayerMemories.getComponentType());

        //Returns if the player memories are over 100
        if(memories != null && memories.getRecordedMemories().size() >= 100) return;
        EchoCorePlugin.moralityManager.onPlayerJoin(playerRef.getUuid());

        if(player.isFirstSpawn()){
            Message firstJoinMessage = Message.raw("Welcome to your world, "+player.getDisplayName()+". My name is AEON-79," +
                    " a Hydroid, and I have corrupted your world. If you want to get rid of me, reach 100 memories, and I shall be free of this cursed world..").color("#7314cc");
            player.sendMessage(firstJoinMessage);
        }
        else{
            Message primaryMessage = Message.raw("Took you long enough.").color("#7314cc");
            Message secondaryMessage = Message.raw("Get going. You have work to do.").color("#9045d6");
            ItemWithAllMetadata icon = new ItemStack("Armor_Onyxium_Head", 1).toPacket();

            NotificationUtil.sendNotification(
                    playerRef.getPacketHandler(),
                    primaryMessage,
                    secondaryMessage,
                    icon
            );
        }

        //Playing a sound
        int index = SoundEvent.getAssetMap().getIndex("SFX_Golem_Firesteel_Wake");
        world.execute(() -> {
            TransformComponent component = store.getStore().getComponent(ref, EntityModule.get().getTransformComponentType());
            SoundUtil.playSoundEvent3dToPlayer(ref, index, SoundCategory.SFX, component.getPosition(), store.getStore());
        });
    }
}
