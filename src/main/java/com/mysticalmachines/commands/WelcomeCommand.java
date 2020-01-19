package com.mysticalmachines.commands;

import com.mysticalmachines.WelcomeRecommender;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;

import java.util.Optional;

public class WelcomeCommand implements CommandExecutor {
    private WelcomeRecommender welcomeRecommender;

    public WelcomeCommand(WelcomeRecommender welcomeRecommender) {
        this.welcomeRecommender = welcomeRecommender;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<User> targetPlayer = args.getOne("player");
//        this.welcomeRecommender.newPlayerList;
        return null;
    }
}
