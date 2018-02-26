package com.minecolonies.rankup.internal.command;

import com.google.inject.Inject;
import com.minecolonies.rankup.Rankup;
import com.minecolonies.rankup.modules.core.config.CoreConfig;
import com.minecolonies.rankup.modules.core.config.GroupsConfig;
import com.minecolonies.rankup.util.CommonUtils;
import com.minecolonies.rankup.util.Constants;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

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
        return TextSerializers.FORMATTING_CODE.deserialize(string);
    }

    protected List<Text> convertToText(final List<String> strings)
    {
        List<Text> texts = new ArrayList<>();

        for (final String msg : strings)
        {
            if (msg.contains(Constants.ModuleInfo.PURCHASE_BUTTON))
            {
                final Text button = Text.builder("Rankup!").onClick(TextActions.runCommand("/ru buy true")).build();

                final String[] text = msg.split(Pattern.quote(Constants.ModuleInfo.PURCHASE_BUTTON));

                texts.add(Text.of(convertToText(text[0]), button, convertToText(text[1])));
            }
            else
            {
                texts.add(convertToText(msg));
            }
        }

        return texts;
    }

    protected List<String> getPlayerData(final User user, final List<String> messages)
    {
        CoreConfig coreConfig = getPlugin().getConfigUtils().getCoreConfig();

        final List<String> newMessage = new ArrayList<>();

        String nextRank = plugin.getPerms().getNextGroup(user);

        if ("".equals(nextRank))
        {
            nextRank = "You are at max rank";
        }
        
        final GroupsConfig groupConfig = plugin.getConfigUtils().getGroupsConfig(user.getPlayer().orElse(null));

        final String trackName = groupConfig == null ? "" : groupConfig.name;

        for (String msg : messages)
        {
            msg = msg.replace(Constants.PlayerInfo.PLAYER_NAME, user.getName())
                    .replace(Constants.PlayerInfo.PLAYER_RANK, plugin.getPerms().getPlayerHighestRankingGroup(user))
                    .replace(Constants.PlayerInfo.PLAYER_NEXT, nextRank)
                    .replace(Constants.PlayerInfo.PLAYER_PREFIX, user.getOption("prefix").orElse(coreConfig.prefixFallback))
                    .replace(Constants.PlayerInfo.PLAYER_JOIN, plugin.getAccUtils().getPlayerJoinDate(user.getUniqueId()))
                    .replace(Constants.PlayerInfo.PLAYER_LAST, plugin.getAccUtils().getPlayerLastDate(user.getUniqueId()))
                    .replace(Constants.PlayerInfo.PLAYER_TRACK, trackName);

            newMessage.add(msg);
        }

        return newMessage;
    }

    protected List<String> getModuleData(final User user, final List<String> messages)
    {
        int userMoney;
        if (getPlugin().getEcon() != null)
        {
            UniqueAccount acc = getPlugin().getEcon().getOrCreateAccount(user.getUniqueId()).orElse(null);
            if (acc != null)
            {
                userMoney = acc.getBalance(getPlugin().getEcon().getDefaultCurrency()).intValue();
            }
            else
            {
                userMoney = 0;
            }
        }
        else
        {
            userMoney = 0;
        }

        final String playTime = CommonUtils.timeDescript(plugin.getAccUtils().getPlayerTime(user.getUniqueId()), plugin);
        final String nextTime = CommonUtils.timeDescript(plugin.getPerms().timeToNextGroup(user), plugin);
        final String balance = Integer.toString(userMoney);
        String nextBal = Integer.toString(plugin.getPerms().balanceToNextGroup(user));
        String balLeft = Integer.toString(userMoney - plugin.getPerms().balanceToNextGroup(user));

        if (Integer.parseInt(nextBal) < 0)
        {
            nextBal = "You are able to rankup";
        }

        final List<String> newMessage = new ArrayList<>();

        for (String msg : messages)
        {
            msg = msg.replace(Constants.ModuleInfo.TIMING_TIME, playTime)
                    .replace(Constants.ModuleInfo.TIMING_NEXT, nextTime)
                    .replace(Constants.ModuleInfo.PURCHASE_LEFT, balLeft)
                    .replace(Constants.ModuleInfo.ECONOMY_BAL, balance)
                    .replace(Constants.ModuleInfo.ECONOMY_NEXT, nextBal);

            newMessage.add(msg);
        }
        return newMessage;
    }
}
