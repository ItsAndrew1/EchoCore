package com.itsandrew.echocore;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MoralStatusCommand extends AbstractPlayerCommand {

    public MoralStatusCommand(@NonNullDecl String name, @NonNullDecl String description) {
        super(name, description);
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        //Displays to the sender his morality level.
        int moralityLevel = EchoCorePlugin.moralityManager.getPlayerMorality(playerRef.getUuid());

        //Sending different messages
        List<Message> messages = new ArrayList<>();
        messages.add(Message.raw("Could you stop asking me that? ").color("#7314cc"));
        messages.add(Message.raw("One day I will stop telling you this. I swear it... ").color("#7314cc"));
        messages.add(Message.raw("Don't you have something better to do? ").color("#7314cc"));
        messages.add(Message.raw("Can you stop? Like really... ").color("#7314cc"));

        Random rand = new Random();
        commandContext.sendMessage(Message.join(messages.get(rand.nextInt(messages.size())), Message.raw("Your morality level is "+moralityLevel).color("#7314cc")));

        //Plays multiple sounds
        List<Integer> sounds = new ArrayList<>();
        sounds.add(SoundEvent.getAssetMap().getIndex("SFX_Golem_Firesteel_Alerted_01"));
        sounds.add(SoundEvent.getAssetMap().getIndex("SFX_Golem_Firesteel_Alerted_02"));
        sounds.add(SoundEvent.getAssetMap().getIndex("SFX_Golem_Firesteel_Laydown"));

        int index = sounds.get(new Random().nextInt(sounds.size()));
        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        SoundUtil.playSoundEvent3dToPlayer(ref, index, SoundCategory.SFX, transform.getPosition(), store);
    }
}
