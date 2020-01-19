package com.mysticalmachines;

import com.google.inject.Inject;
import com.mysticalmachines.commands.WelcomeCommand;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Plugin(
        id = "welcomerecommender",
        name = "welcomerecommender",
        description = "Welcome new players more easily and get rewarded by doing it",
        authors = {
                "Rubbertjuh"
        }
)
public class WelcomeRecommender {

    public List<Player> newPlayerList = new ArrayList<>();

    @Inject
    private Logger logger;

    @Inject
    private Game game;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("Welcome Recommender Started");
    }

    @Listener
    public void onPlayerFirstJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
//        Value<Instant> joinDate = player.firstPlayed();
//        Value<Instant> lastPlayDate = player.lastPlayed();
        if (!player.hasPlayedBefore()) {
            newPlayerList.add(player);
        }
    }

    @Listener
    public void init(GameInitializationEvent event) {
        createAndRegisterCommands();
    }

    /**
     * Create commands and registers them with the CommandManager.
     */
    private void createAndRegisterCommands() {

        CommandSpec payCommand = CommandSpec.builder()
                .description(Text.of("Welcome a player"))
                .permission("welcomerecommender.command.welcome")
                .executor(new WelcomeCommand(this))
                .arguments(
                        GenericArguments.optional(
                                        GenericArguments.user(Text.of("player"))
                        )
                )
                .build();
        game.getCommandManager().register(this, payCommand, "pay");


    }
}
