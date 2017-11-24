package com.minecolonies.rankup.internal.command;

import com.google.inject.Inject;
import com.minecolonies.rankup.Rankup;
import com.minecolonies.rankup.modules.core.config.CoreConfig;
import com.minecolonies.rankup.util.CommonUtils;
import com.minecolonies.rankup.util.Constants;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Extend this in any module to create a new Rankup command.
 */
public abstract class RankupSubcommand implements CommandExecutor
{
    private static CommandElement[] empty = new CommandElement[0];
    @Inject
    private Rankup plugin;

    protected abstract String[] getAliases();

    protected abstract Optional<Text> getDescription();

    protected abstract Optional<String> getPermission();

    protected final Rankup getPlugin()
    {
        return this.plugin;
    }

    public CommandElement[] getArguments()
    {
        return empty.clone();
    }

    protected Text convertToText(final String string)
    {
        return Text.of(TextSerializers.FORMATTING_CODE.deserialize(string));
    }

    protected List<Text> convertToText(final List<String> strings)
    {
        List<Text> texts = new ArrayList<>();

        for (final String msg : strings)
        {
            texts.add(convertToText(msg));
        }

        return texts;
    }

    protected List<String> getPlayerData(final User user, final List<String> messages)
    {
        CoreConfig coreConfig = getPlugin().getConfigUtils().getCoreConfig();

        final List<String> newMessage = new ArrayList<>();

        for (String msg : messages)
        {
            msg = msg.replace(Constants.PlayerInfo.PLAYER_NAME, user.getName())
                    .replace(Constants.PlayerInfo.PLAYER_RANK, plugin.getPerms().getPlayerHighestRankingGroup(user))
                    .replace(Constants.PlayerInfo.PLAYER_PREFIX, user.getOption("prefix").orElse(coreConfig.prefixFallback))
                    .replace(Constants.PlayerInfo.PLAYER_JOIN, plugin.getAccUtils().getPlayerJoinDate(user.getUniqueId()))
                    .replace(Constants.PlayerInfo.PLAYER_LAST, plugin.getAccUtils().getPlayerLastDate(user.getUniqueId()))
                    .replace(Constants.PlayerInfo.PLAYER_TRACK, plugin.getConfigUtils().getGroupsConfig(user.getPlayer().orElse(null)).name);

            newMessage.add(msg);
        }

        return newMessage;
    }

    protected List<String> getModuleData(final User user, final List<String> messages)
    {
        int userMoney;
        if (getPlugin().getEcon() != null && getPlugin().getEcon().getOrCreateAccount(user.getUniqueId()).isPresent())
        {
            UniqueAccount acc = getPlugin().getEcon().getOrCreateAccount(user.getUniqueId()).get();
            userMoney = acc.getBalance(getPlugin().getEcon().getDefaultCurrency()).intValue();
        }
        else
        {
            userMoney = 0;
        }

        final String playTime = CommonUtils.timeDescript(plugin.getAccUtils().getPlayerTime(user.getUniqueId()), plugin);
        final String nextTime = CommonUtils.timeDescript(plugin.getPerms().timeToNextGroup(user), plugin);
        final String balance = Integer.toString(userMoney);
        final String nextBal = Integer.toString(plugin.getPerms().balanceToNextGroup(user));
        final List<String> newMessage = new ArrayList<>();

        for (String msg : messages)
        {
            msg = msg.replace(Constants.ModuleInfo.TIMING_TIME, playTime)
                    .replace(Constants.ModuleInfo.TIMING_NEXT, nextTime)
                    .replace(Constants.ModuleInfo.ECONOMY_BAL, balance)
                    .replace(Constants.ModuleInfo.ECONOMY_NEXT, nextBal);

            newMessage.add(msg);
        }
        return newMessage;
    }
}
