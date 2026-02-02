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
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
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

//Class listening to what blocks the player breaks.
public class PlayerBreakBlock extends EntityEventSystem<EntityStore, BreakBlockEvent> {
    public PlayerBreakBlock(@NonNullDecl Class<BreakBlockEvent> eventType) {
        super(eventType);
    }

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl BreakBlockEvent breakBlockEvent) {
        Ref<EntityStore> blockRef = archetypeChunk.getReferenceTo(i);
        Player player = store.getComponent(blockRef, Player.getComponentType());

        //Getting the player's essentials
        Ref<EntityStore> ref = player.getReference();
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        String blockId = breakBlockEvent.getBlockType().getId();
        if(blockId.contains("Ore")){
            Random rand = new Random();
            int number = rand.nextInt(100);
            if(number >= 10) return;

            //Adding morality based on the type of ore
            int moral = 0;
            if(blockId.contains("Adamantite")){
                EchoCorePlugin.moralityManager.setPlayerMorality(playerRef.getUuid(), 10);
                moral = 10;
            }
            if(blockId.contains("Copper")){
                EchoCorePlugin.moralityManager.setPlayerMorality(playerRef.getUuid(), 2);
                moral = 2;
            }
            if(blockId.contains("Iron")){
                EchoCorePlugin.moralityManager.setPlayerMorality(playerRef.getUuid(), 4);
                moral = 4;
            }
            if(blockId.contains("Silver")){
                EchoCorePlugin.moralityManager.setPlayerMorality(playerRef.getUuid(), 5);
                moral = 5;
            }
            if(blockId.contains("Gold")){
                EchoCorePlugin.moralityManager.setPlayerMorality(playerRef.getUuid(), 7);
                moral = 7;
            }
            if(blockId.contains("Thorium")){
                EchoCorePlugin.moralityManager.setPlayerMorality(playerRef.getUuid(), 8);
                moral = 8;
            }
            if(blockId.contains("Cobalt")){
                EchoCorePlugin.moralityManager.setPlayerMorality(playerRef.getUuid(), 9);
                moral = 9;
            }
            if(blockId.contains("Mithril")){
                EchoCorePlugin.moralityManager.setPlayerMorality(playerRef.getUuid(), 12);
                moral = 12;
            }
            EchoCorePlugin.moralityManager.applyReward(player, playerRef);
            //Implement other messages.

            //Sending a notification
            List<Message> messages = new ArrayList<>();
            messages.add(Message.raw("Good job. Careful not to die..").color("#7314cc"));
            messages.add(Message.raw("Isn't it Iron Pick?").color("#7314cc"));
            messages.add(Message.raw("How I wish to be able to mine as well...").color("#7314cc"));
            sendNotification(playerRef, messages, moral, true);

            //Plays a sound to the player
            playSound(store, ref);
        }

        //If the player breaks a tree
        if(blockId.contains("Trunk")){
            Random rand = new Random();
            int number = rand.nextInt(100);
            if(number >= 50) return;

            //Removing morality (AEON-79 loves the nature)
            EchoCorePlugin.moralityManager.setPlayerMorality(playerRef.getUuid(), -10);

            //Checks for applying the punishment.
            EchoCorePlugin.moralityManager.applyPunishment(player,  playerRef);

            //Sending a notification
            List<Message> messages = new ArrayList<>();
            messages.add(Message.raw("NOOO. Not my trees :(").color("#7314cc"));
            messages.add(Message.raw("Oh you will pay for that.").color("#7314cc"));
            messages.add(Message.raw("Aren't you done chopping my trees?..").color("#7314cc"));
            sendNotification(playerRef, messages, 5, false);

            //Plays a sound to the player
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

    private void sendNotification(@NonNullDecl PlayerRef playerRef, List<Message> messages, int moral, boolean give){
        ItemWithAllMetadata icon = new ItemStack("Armor_Onyxium_Head").toPacket();

        NotificationUtil.sendNotification(
                playerRef.getPacketHandler(),
                Message.join(Message.raw("[AEON-79] ").color(Color.CYAN), messages.get(new Random().nextInt(messages.size()))),
                give ? Message.raw("You got "+moral+" morality!").color(Color.MAGENTA) : Message.raw("You lost "+moral+" morality!").color(Color.MAGENTA),
                icon
        );
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }
}
