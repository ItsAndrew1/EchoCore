//Developed by _ItsAndrew_
package com.itsandrew.echocore;

import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.event.events.ecs.DiscoverZoneEvent;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.logger.HytaleLogger;
import com.itsandrew.echocore.RewardingAndPunishing.*;

import javax.annotation.Nonnull;
import java.util.logging.Level;

public class EchoCorePlugin extends JavaPlugin {
    private final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static MoralityManager moralityManager;

    public EchoCorePlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }


    @Override
    protected void setup() {
        LOGGER.at(Level.INFO).log("Setting up...");

        //Registering commands
        this.getCommandRegistry().registerCommand(new MoralStatusCommand("moralstatus", "Displays your moral status."));

        //Registering events
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, PlayerJoin::onPlayerReady);
        this.getEntityStoreRegistry().registerSystem(new PlayerDeath());
        this.getEntityStoreRegistry().registerSystem(new PlayerPlaceBlock(PlaceBlockEvent.class));
        this.getEntityStoreRegistry().registerSystem(new PlayerBreakBlock(BreakBlockEvent.class));

        //Initializing the .json file
        try{
            moralityManager = new MoralityManager(this.getDataDirectory(), this);
        } catch (Exception e){
            e.printStackTrace();
        }

        LOGGER.at(Level.INFO).log("Setup complete!");
    }

    @Override
    protected void start() {
        LOGGER.at(Level.INFO).log("Started!");
    }

    @Override
    protected void shutdown() {
        LOGGER.at(Level.INFO).log("Shutting down...");
    }
}