package com.mysticalmachines;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;

import java.time.Instant;
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

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("Welcome Recommender Started");
    }

    @Listener
    public void onPlayerFirstJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        Value<Instant> joinDate = player.firstPlayed();
        Value<Instant> lastPlayDate = player.lastPlayed();
        if (joinDate == lastPlayDate) {
            logger.info("NEW JOIN! WOOHOOOO");
        } else {
            logger.info("Not a new join :(");
        }
    }
}
