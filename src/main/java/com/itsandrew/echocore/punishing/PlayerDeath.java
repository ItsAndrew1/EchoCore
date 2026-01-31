//Developed by _ItsAndrew_
package com.itsandrew.echocore.punishing;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.itsandrew.echocore.EchoCorePlugin;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.awt.*;

//Class listening to a player's death
public class PlayerDeath extends DeathSystems.OnDeathSystem{
    @Override
    public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl DeathComponent deathComponent, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        assert player != null;

        //Sends a message to the player's screen
        player.sendMessage(Message.join(Message.raw("[AEON-79] ").color(Color.CYAN),
                Message.raw("Maybe I should go find someone else at this point...").color("#7314cc")
                ));

        //Sending a notification
        ItemWithAllMetadata icon = new ItemStack("Recipe_Book_Magic_Air").toPacket();
        NotificationUtil.sendNotification(
                playerRef.getPacketHandler(),
                Message.raw("You Died!").color("#7314cc"),
                Message.raw("Because of this, you lose 20 moral points.").color("#15d3e8"),
                icon
        );

        //Also playing a sound
        int index = SoundEvent.getAssetMap().getIndex("SFX_Golem_Firesteel_Death");
        player.getWorld().execute(() -> {
            TransformComponent component = store.getComponent(ref, TransformComponent.getComponentType());
            SoundUtil.playSoundEvent3dToPlayer(ref, index, SoundCategory.SFX, component.getPosition(), store);
        });

        //Decreasing the player's morality by 20
        EchoCorePlugin.moralityManager.setPlayerMorality(store.getComponent(ref, PlayerRef.getComponentType()).getUuid(), -20);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType());
    }
}
