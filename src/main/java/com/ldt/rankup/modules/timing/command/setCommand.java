package com.ldt.rankup.modules.timing.command;

import com.ldt.rankup.internal.command.RankupSubcommand;
import com.ldt.rankup.util.Constants;
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

import java.util.Optional;

@NonnullByDefault
public class setCommand extends RankupSubcommand
{
    private static final Text USER_KEY = Text.of("user");
    private static final Text TIME_KEY = Text.of("time");

    @Override
    protected String[] getAliases()
    {
        return new String[] {"set"};
    }

    @Override
    protected Optional<String> getPermission()
    {
        return Optional.of("rankup.timing.admin");
    }

    @Override
    public Optional<Text> getDescription()
    {
        return Optional.of(Text.of(
          "Allows players with the rankup.timing.base to set another players playtime. \n Usage: /ru set [time] " + Constants.PlayerInfo.PLAYER_NAME + ""));
    }

    @Override
    public CommandElement[] getArguments()
    {
        return new CommandElement[] {
          GenericArguments.integer(TIME_KEY),
          GenericArguments.optionalWeak(GenericArguments.user(USER_KEY)),
        };
    }

    @Override
    public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException
    {
        if (args.<User>getOne(USER_KEY).isPresent() && args.<Integer>getOne(TIME_KEY).isPresent())
        {
            addTime(src, args.<User>getOne(USER_KEY).get(), args.<Integer>getOne(TIME_KEY).get());
        }
        else if (src instanceof User && args.<Integer>getOne(TIME_KEY).isPresent())
        {
            final User user = (User) src;
            addTime(src, user, args.<Integer>getOne(TIME_KEY).get());
        }
        else
        {
            src.sendMessage(Text.of(TextColors.DARK_RED, "Please supply a User Argument"));
        }

        return CommandResult.success();
    }

    private void addTime(final CommandSource src, final User user, final Integer time)
    {

        if (!getPlugin().getAccUtils().doesPlayerExist(user.getUniqueId()))
        {
            src.sendMessage(Text.of(TextColors.DARK_RED, "Invalid User"));
            return;
        }

        getPlugin().getAccUtils().updatePlayerTime(user.getUniqueId(), time);

        src.sendMessage(Text.of(user.getName() + "(s) new playTime is: " + getPlugin().getAccUtils().getPlayerTime(user.getUniqueId())));
    }
}
