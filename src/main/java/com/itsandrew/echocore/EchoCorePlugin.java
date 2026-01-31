//Developed by _ItsAndrew_
package com.itsandrew.echocore;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.logger.HytaleLogger;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;

public class EchoCorePlugin extends JavaPlugin {
    private final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private EchoCorePlugin instance;

    public static MoralityManager moralityManager;

    public EchoCorePlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public EchoCorePlugin getInstance() {
        return instance;
    }

    @Override
    protected void setup() {
        LOGGER.at(Level.INFO).log("Setting up...");

        //Registering commands
        this.getCommandRegistry().registerCommand(new MoralStatusCommand("moralstatus", "Displays your moral status."));

        //Registering events
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, PlayerJoin::onPlayerReady);

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
        instance = null;
    }
}