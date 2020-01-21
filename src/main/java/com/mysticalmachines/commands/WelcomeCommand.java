package com.mysticalmachines.commands;

import com.mysticalmachines.WelcomeRecommender;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class WelcomeCommand implements CommandExecutor {
    private WelcomeRecommender welcome;

    public WelcomeCommand(WelcomeRecommender welcome) {
        this.welcome = welcome;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Optional<Player> targetPlayer = args.getOne("player");
            Player senderPlayer = (Player) src;
            if (targetPlayer.isPresent() && this.welcome.newPlayerMapCache.containsValue(targetPlayer.get())) {
                if (isPlayerAllowedToWelcomeTargetPlayer(targetPlayer.get(), senderPlayer)) {
                    if (targetPlayer.get() == senderPlayer) {
                        throw new CommandException(Text.of(TextColors.RED, "Tell me, why are you trying to welcome yourself?"));
                    }
                    MessageChannel.TO_ALL.send(Text.of("Welcome, " + targetPlayer.get().getName() + ", enjoy your stay!"));
                    this.executeConfiguredCommands(targetPlayer.get(), senderPlayer);
                    this.welcome.alreadyWelcomedPlayersCache.put(targetPlayer.get(), senderPlayer);
                    return CommandResult.success();
                }
                throw new CommandException(Text.of(TextColors.RED, "You already welcomed this player"));
            } else {
                if (this.welcome.latestPlayer != null && this.welcome.latestPlayer != senderPlayer) {
                    if (isPlayerAllowedToWelcomeTargetPlayer(this.welcome.latestPlayer, senderPlayer)) {
                        MessageChannel.TO_ALL.send(Text.of("Welcome, " + this.welcome.latestPlayer.getName() + ", enjoy your stay!"));
                        this.executeConfiguredCommands(this.welcome.latestPlayer, senderPlayer);
                        this.welcome.alreadyWelcomedPlayersCache.put(this.welcome.latestPlayer, senderPlayer);
                        return CommandResult.success();
                    }
                    throw new CommandException(Text.of(TextColors.RED, "You already welcomed this player"));
                } else {
                    throw new CommandException(Text.of(TextColors.RED, "There are no new players to welcome"));
                }
            }
        } else {
            throw new CommandException(Text.of(TextColors.RED, "Command can only be executed by a player, not a console, you dummy."));
        }
    }

    private void executeConfiguredCommands(Player targetPlayer, Player commandExecutingPlayer) {
        for (String command :
                this.welcome.executeCommandsOnWelcome) {
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command
                    .replace("%targetPlayerName%", targetPlayer.getName())
                    .replace("%commandExecutingPlayerName%", commandExecutingPlayer.getName()));
        }
    }

    private boolean isPlayerAllowedToWelcomeTargetPlayer(Player targetPlayer, Player senderPlayer) {
        if (!this.welcome.alreadyWelcomedPlayersCache.isEmpty()) {
            Player returnedPlayer = this.welcome.alreadyWelcomedPlayersCache.get(targetPlayer);
            return returnedPlayer == null || returnedPlayer != senderPlayer;
        }
        return true;
    }
}
