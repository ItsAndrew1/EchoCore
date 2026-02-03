//Developed by _ItsAndrew_
package com.itsandrew.echocore.RewardingAndPunishing;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.itsandrew.echocore.EchoCorePlugin;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerPlaceBlock extends EntityEventSystem<EntityStore, PlaceBlockEvent> {
    public PlayerPlaceBlock(@NonNullDecl Class<PlaceBlockEvent> eventType) {
        super(eventType);
    }

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl PlaceBlockEvent placeBlockEvent) {
        Ref<EntityStore> blockRef = archetypeChunk.getReferenceTo(i);

        Player player = store.getComponent(blockRef, Player.getComponentType());
        Ref<EntityStore> ref = player.getReference();

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        //Getting the player's item
        ItemStack playerItem = placeBlockEvent.getItemInHand();
        String itemId = playerItem.getItemId();

        //Adding morality based on crops
        if(itemId.contains("Plant_Seeds") || itemId.contains("Plant_Crop")){
            Random rand = new Random();
            int number = rand.nextInt(100);
            if(number >= 20) return;

            //Adding morality
            EchoCorePlugin.moralityManager.setPlayerMorality(store.getComponent(ref, PlayerRef.getComponentType()).getUuid(), 5);

            //Playing a sound
            playSound(store, ref);

            //Applies the reward (checking if he got to a milestone)
            EchoCorePlugin.moralityManager.applyReward(player, playerRef);

            //Sending a notification
            List<Message> messages = new ArrayList<>();
            messages.add(Message.raw("Nice farming skills you've got.").color("#7314cc"));
            messages.add(Message.raw("Was your dad a farmer by any chance?").color("#7314cc"));
            sendNotification(playerRef, messages, 5);
        }

        //Adding morality based on building stuff
        if(itemId.contains("Roof") || itemId.contains("Fence") || itemId.contains("Beam") || itemId.contains("Planks") || itemId.contains("Pillar")
            || itemId.contains("Wall") || itemId.contains("Half")
        ){
            Random rand = new Random();
            int number = rand.nextInt(100);
            if(number >= 20) return;

            //Adding morality
            EchoCorePlugin.moralityManager.setPlayerMorality(store.getComponent(ref, PlayerRef.getComponentType()).getUuid(), 3);
            EchoCorePlugin.moralityManager.applyReward(player, playerRef);
            //Implement other messages.

            //Sending a notification
            List<Message> messages = new ArrayList<>();
            messages.add(Message.raw("Are you on your building arc?").color("#7314cc"));
            messages.add(Message.raw("Never thought you liked building").color("#7314cc"));
            messages.add(Message.raw("A building won't help you get rid of me...").color("#7314cc"));
            sendNotification(playerRef, messages, 3);

            //Playing a sound
            playSound(store, ref);
        }

        //Adding morality based on benches and furniture
        if(itemId.contains("Bench") || itemId.contains("Furniture")){
            Random rand = new Random();
            int number = rand.nextInt(100);
            if(number >= 20) return;

            //Adding morality
            EchoCorePlugin.moralityManager.setPlayerMorality(store.getComponent(ref, PlayerRef.getComponentType()).getUuid(), 6);
            EchoCorePlugin.moralityManager.applyReward(player, playerRef);
            //Implement other messages.

            //Sending a notification
            List<Message> messages = new ArrayList<>();
            messages.add(Message.raw("Aren't you getting tired of placing furnitures?..").color("#7314cc"));
            messages.add(Message.raw("Never understood this whole furniture aspect..").color("#7314cc"));
            messages.add(Message.raw("Those memories ain't getting discovered. Chop chop.").color("#7314cc"));
            sendNotification(playerRef, messages, 6);

            //Playing a sound
            playSound(store, ref);
        }
    }

    private void playSound(@NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref){
        List<Integer> sounds = new ArrayList<>();
        sounds.add(SoundEvent.getAssetMap().getIndex("SFX_Golem_Firesteel_Alerted_01"));
        sounds.add(SoundEvent.getAssetMap().getIndex("SFX_Golem_Firesteel_Alerted_02"));
        sounds.add(SoundEvent.getAssetMap().getIndex("SFX_Golem_Firesteel_Laydown"));

        int index = sounds.get(new Random().nextInt(sounds.size()));
        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        SoundUtil.playSoundEvent3dToPlayer(ref, index, SoundCategory.SFX, transform.getPosition(), store);
    }

    private void sendNotification(@NonNullDecl PlayerRef playerRef, List<Message> messages, int moral){
        ItemWithAllMetadata icon = new ItemStack("Armor_Onyxium_Head").toPacket();

        NotificationUtil.sendNotification(
                playerRef.getPacketHandler(),
                Message.join(Message.raw("[AEON-79] ").color(Color.CYAN), messages.get(new Random().nextInt(messages.size()))),
                Message.raw("You got "+moral+" morality!").color(Color.MAGENTA),
                icon
        );
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }
}
