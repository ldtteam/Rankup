package com.minecolonies.rankup.modules.timing.command;

import com.minecolonies.rankup.internal.command.RankupSubcommand;
import com.minecolonies.rankup.modules.core.config.AccountConfigData;
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
public class addCommand extends RankupSubcommand
{
    private static final Text USER_KEY = Text.of("user");
    private static final Text TIME_KEY = Text.of("time");

    @Override
    protected String[] getAliases()
    {
        return new String[] {"add"};
    }

    @Override
    protected Optional<String> getPermission()
    {
        return Optional.of("rankup.timing.admin");
    }

    @Override
    public Optional<Text> getDescription()
    {
        return Optional.of(Text.of("Allows players with the rankup.timing.base to add time to another players playtime. \n Usage: /ru add [time] {player}"));
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
        AccountConfigData playerData = (AccountConfigData) getPlugin().getAllConfigs().get(AccountConfigData.class);

        final AccountConfigData.PlayerConfig playerConfig = playerData.playerData.get(user.getUniqueId());

        if (playerConfig == null)
        {
            src.sendMessage(Text.of(TextColors.DARK_RED, "User must have been online at least once since server restart (Sorry)"));
            return;
        }

        playerConfig.timePlayed += time;

        playerData.save();

        src.sendMessage(Text.of(user.getName() + "(s) new playTime is: " + playerConfig.timePlayed));
    }
}
