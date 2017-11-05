package com.minecolonies.rankup.modules.core.command;

import com.minecolonies.rankup.internal.command.RankupSubcommand;
import com.minecolonies.rankup.modules.core.config.CoreConfig;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.List;
import java.util.Optional;

@NonnullByDefault
public class checkCommand extends RankupSubcommand
{
    private static final Text USER_KEY = Text.of("player");

    @Override
    protected String[] getAliases()
    {
        return new String[] {"check"};
    }

    @Override
    protected Optional<String> getPermission()
    {
        return Optional.of("rankup.check.base");
    }

    @Override
    public Optional<Text> getDescription()
    {
        return Optional.of(Text.of("Allows a player to see their (Or another players) RankUp stats. \n Usage: /ru check {player}"));
    }

    @Override
    public CommandElement[] getArguments()
    {
        return new CommandElement[] {
          GenericArguments.optionalWeak(GenericArguments.user(USER_KEY))
        };
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
    {
        if (args.<User>getOne(USER_KEY).isPresent())
        {
            sendCheck(src, args.<User>getOne(USER_KEY).get());
        }
        else if (src instanceof User)
        {
            final User user = (User) src;
            sendCheck(src, user);
        }
        else
        {
            src.sendMessage(Text.of(TextColors.DARK_RED, "Please supply a User Argument"));
        }
        return CommandResult.success();
    }

    private void sendCheck(final CommandSource src, final User user)
    {
        CoreConfig coreConfig = getPlugin().configUtils.getCoreConfig();

        if (getPlugin().accUtils.doesPlayerExist(user.getUniqueId()))
        {
            final List<String> message = getModuleData(user, getPlayerData(user, coreConfig.checkMessageTemplate));

            final List<Text> finalMessage = convertToText(message);

            for (final Text text : finalMessage)
            {
                src.sendMessage(text);
            }
        }
        else
        {
            src.sendMessage(Text.of(TextColors.DARK_RED, "Invalid User - (Probably hasn't been online since last restart)"));
        }
    }
}
