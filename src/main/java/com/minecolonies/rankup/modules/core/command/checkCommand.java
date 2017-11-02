package com.minecolonies.rankup.modules.core.command;

import com.minecolonies.rankup.internal.command.RankupSubcommand;
import com.minecolonies.rankup.modules.core.CoreModule;
import com.minecolonies.rankup.modules.core.config.AccountConfigData;
import com.minecolonies.rankup.modules.core.config.GroupsConfig;
import com.minecolonies.rankup.util.CommonUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import uk.co.drnaylor.quickstart.exceptions.NoModuleException;

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
        src.sendMessage(Text.of(MIDDLE, "Current Player Group: ", TextSerializers.FORMATTING_CODE.deserialize(playerPrefix)));

        final List<String> playerGroups = CoreModule.perms.getPlayerGroupIds(user);

        boolean inDisabledGroup = false;

        final GroupsConfig groupsConfig = CoreModule.perms.getGroupConfig(user);

        //Check if player is in disabled group.
        for (final String subject : CoreModule.perms.getDisabledGroups())
        {
            if (playerGroups.contains(subject))
            {
                inDisabledGroup = true;
            }
        }

        try
        {
            if (getPlugin().getModuleContainer().isModuleLoaded("timing"))
            {
                final Integer time = CoreModule.perms.timeToNextGroup(user);

                src.sendMessage(Text.of(MIDDLE, "Current Player Time: ", TextColors.AQUA, CommonUtils.timeDescript(playerConf.timePlayed)));

                if (!inDisabledGroup && time != -1)
                {
                    src.sendMessage(Text.of(MIDDLE, "Time to next Group: ", TextColors.GOLD, CommonUtils.timeDescript(time)));
                }
                else if (time == -1)
                {
                    src.sendMessage(Text.of(MIDDLE, "Time to next Group: ", TextColors.GOLD, "You have progressed to the"));
                    src.sendMessage(Text.of(MIDDLE, TextColors.GOLD, "highest possible group!"));
                }
            }

            if (getPlugin().getModuleContainer().isModuleLoaded("economy"))
            {
                final String nextGroup = CoreModule.perms.getNextGroup(user);

                UniqueAccount acc = getPlugin().econ.getOrCreateAccount(user.getUniqueId()).get();
                int userMoney = acc.getBalance(getPlugin().econ.getDefaultCurrency()).intValue();
                src.sendMessage(Text.of(MIDDLE, "Current Balance: ", TextColors.AQUA, userMoney));
                if (!inDisabledGroup && nextGroup != "")
                {
                    final Integer moneyNeeded = groupsConfig.groups.get(nextGroup).moneyNeeded - userMoney;

                    if (moneyNeeded > 0)
                    {
                        src.sendMessage(Text.of(MIDDLE, "Money needed for next group: ", TextColors.GOLD, moneyNeeded));
                    }
                    else
                    {
                        src.sendMessage(Text.of(MIDDLE, "You should rankup on the next balance check!"));
                    }
                }
                else if (!inDisabledGroup)
                {
                    src.sendMessage(Text.of(MIDDLE, "Time to next Group: ", TextColors.GOLD, "You have progressed to the"));
                    src.sendMessage(Text.of(MIDDLE, TextColors.GOLD, "highest possible group!"));
                }
            }
        }
        catch (NoModuleException e)
        {
            e.printStackTrace();
        }

        src.sendMessage(Text.of(MIDDLE, "Join Date: ", TextColors.DARK_GREEN, playerConf.joinDate));
        src.sendMessage(Text.of(MIDDLE, "Last Join: ", TextColors.BLUE, playerConf.lastVisit));

        src.sendMessage(BOTTOM);
    }
}
