package com.minecolonies.rankup.modules.core.command;

import com.minecolonies.rankup.internal.command.RankupSubcommand;
import com.minecolonies.rankup.modules.core.CoreModule;
import com.minecolonies.rankup.modules.core.config.AccountConfigData;
import com.minecolonies.rankup.util.CommonUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
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
        AccountConfigData playerData = (AccountConfigData) getPlugin().getAllConfigs().get(AccountConfigData.class);

        final AccountConfigData.PlayerConfig playerConf = playerData.playerData.get(user.getUniqueId());

        if (playerConf == null)
        {
            src.sendMessage(Text.of(TextColors.DARK_RED, "User must have been online at least once since server restart (Sorry)"));
            return;
        }

        final String playerPrefix = user.getOption("prefix").orElse(CoreModule.perms.getPlayerHighestRankingGroup(user));

        src.sendMessage(TOP);

        src.sendMessage(Text.of(MIDDLE, "Current Player Time: ", TextColors.AQUA, CommonUtils.timeDescript(playerConf.timePlayed)));
        src.sendMessage(Text.of(MIDDLE, "Current Player Group: ", TextSerializers.FORMATTING_CODE.deserialize(playerPrefix)));

        final List<String> playerGroups = CoreModule.perms.getPlayerGroupIds(user);

        boolean inDisabledGroup = false;

        //Check if player is in disabled group.
        for (final Subject subject : CoreModule.perms.getDisabledGroups())
        {
            if (playerGroups.contains(subject.getIdentifier()))
            {
                inDisabledGroup = true;
            }
        }

        final Integer time = CoreModule.perms.timeToNextGroup(user);

        if (!inDisabledGroup && time != -1)
        {
            src.sendMessage(Text.of(MIDDLE, "Time to next Group: ", TextColors.GOLD, CommonUtils.timeDescript(time)));
        }
        else if (time == -1)
        {
            src.sendMessage(Text.of(MIDDLE, "Time to next Group: ", TextColors.GOLD, "You have progressed to the"));
            src.sendMessage(Text.of(MIDDLE, TextColors.GOLD, "highest possible group!"));
        }

        src.sendMessage(Text.of(MIDDLE, "Join Date: ", TextColors.DARK_GREEN, playerConf.joinDate));
        src.sendMessage(Text.of(MIDDLE, "Last Join: ", TextColors.BLUE, playerConf.lastVisit));

        src.sendMessage(BOTTOM);
    }
}
