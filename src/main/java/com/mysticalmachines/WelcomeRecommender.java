package com.mysticalmachines;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.mysticalmachines.commands.WelcomeCommand;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "welcomerecommender",
        name = "welcomerecommender",
        description = "Welcome new players more easily and get rewarded by doing it",
        authors = {
                "Rubbertjuh"
        }
)
public class WelcomeRecommender {

    public List<String> executeCommandsOnWelcome;
    public HashMap<Instant, Player> newPlayerMapCache = new HashMap<>();
    public HashMap<Player, Player> alreadyWelcomedPlayersCache = new HashMap<>();
    public Player latestPlayer;
    @Inject
    private Logger logger;
    @Inject
    private Game game;
    @Inject
    private PluginContainer pluginContainer;
    private ConfigurationNode config;
    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDir;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defaultConf;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    @Listener
    public void preInit(GamePreInitializationEvent event) {
        try {
            loadConfig();
            this.executeCommandsOnWelcome = config.getNode("welcomeCommandRewards").getList(TypeToken.of(String.class));
            for (String command :
                    this.executeCommandsOnWelcome) {
                System.out.println("Command: " + command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("Welcome Recommender Started");
        Task.builder().execute(() -> {
            deleteOldPlayersFromMap(300);
        }).async().interval(3, TimeUnit.SECONDS).submit(this);
    }

    @Listener
    public void onPlayerFirstJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        if (!player.hasPlayedBefore()) {
            newPlayerMapCache.put(player.firstPlayed().get(), player);
            this.latestPlayer = player;
        }
    }

    @Listener
    public void init(GameInitializationEvent event) {
        createAndRegisterCommands();
    }


    private void deleteOldPlayersFromMap(long seconds) {
        try {
            if (!this.newPlayerMapCache.isEmpty()) {
                Iterator<Instant> iterator = this.newPlayerMapCache.keySet().iterator();
                while (iterator.hasNext()) {
                    Instant instant = iterator.next();
                    if (instant.isBefore(Instant.now().minusSeconds(seconds))) {
                        if (this.newPlayerMapCache.get(instant) == this.latestPlayer) {
                            this.latestPlayer = null;
                        }
                        iterator.remove();
                    }
                }

            }
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * Create commands and registers them with the CommandManager.
     */
    private void createAndRegisterCommands() {

        CommandSpec welcomeCommand = CommandSpec.builder()
                .description(Text.of("Welcome a player"))
                .executor(new WelcomeCommand(this))
                .arguments(
                        GenericArguments.optional(
                                GenericArguments.player(Text.of("player"))
                        )
                )
                .build();
        game.getCommandManager().register(this, welcomeCommand, "welcome");
    }

    /**
     * Load the default config file, welcomerecommender.conf.
     */
    private void loadConfig() {
        try {
            if (!defaultConf.exists()) {
                pluginContainer.getAsset("welcomerecommender.conf").get().copyToFile(defaultConf.toPath());
            }

            this.config = loader.load();
        } catch (IOException e) {
            logger.warn("[WelcomeRecommender] Main configuration file could not be loaded/created/changed!");
        }
    }
}
