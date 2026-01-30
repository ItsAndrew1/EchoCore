package com.itsandrew.echocore;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;

import java.awt.*;

public class PlayerJoin {
    public static void onPlayerReady(PlayerReadyEvent event){
        Player player = event.getPlayer();
        if(player.isFirstSpawn()){
            Message firstJoinMessage = Message.raw("Welcome to your world, "+player.getDisplayName()+". My name is AEON-79," +
                    " a Hydroid, and I have corrupted your world. If you want to get rid of me, reach 100 memories, and I shall be free of this cursed world..").color(Color.MAGENTA);
            player.sendMessage(firstJoinMessage);
        }
        else{
            Message joinMessage = Message.raw("Took you long enough. Now get going, we have work to do.").color(Color.MAGENTA);
            player.sendMessage(joinMessage);
        }
    }
}
