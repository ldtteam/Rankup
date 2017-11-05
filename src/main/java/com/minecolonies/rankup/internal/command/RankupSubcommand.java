package com.minecolonies.rankup.internal.command;

import com.google.inject.Inject;
import com.minecolonies.rankup.Rankup;
import com.minecolonies.rankup.modules.core.CoreModule;
import com.minecolonies.rankup.modules.core.config.AccountConfigData;
import com.minecolonies.rankup.modules.core.config.CoreConfig;
import com.minecolonies.rankup.modules.core.config.CoreConfigAdapter;
import com.minecolonies.rankup.util.CommonUtils;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.service.permission.Subject;

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
        return empty;
    }

    public Text convertToText(final String string)
    {
        return Text.of(TextSerializers.FORMATTING_CODE.deserialize(string));
    }

    public List<Text> convertToText(final List<String> strings)
    {
        List<Text> texts = new ArrayList<>();

        for (final String msg : strings)
        {
            texts.add(convertToText(msg));
        }

        return texts;
    }

    public List<String> getPlayerData(final User user, final List<String> messages, final AccountConfigData.PlayerConfig playerConfig)
    {
        CoreConfig coreConfig = getPlugin().getConfigAdapter(CoreModule.ID, CoreConfigAdapter.class).get().getNodeOrDefault();

        final List<String> newMessage = new ArrayList<>();

        for (final String msg : messages)
        {
            String message = msg;
            message = msg.replace("{player}", user.getName())
                        .replace("{rank}", CoreModule.perms.getPlayerHighestRankingGroup(user))
                        .replace("{prefix}", user.getOption("prefix").orElse(coreConfig.prefixFallback))
                        .replace("{joindate}", playerConfig.joinDate)
                        .replace("{lastjoin}", playerConfig.lastVisit);

            newMessage.add(message);
        }

        return newMessage;
    }

    public List<String> getModuleData(final User user, final List<String> messages, final AccountConfigData.PlayerConfig playerConfig)
    {
        int userMoney;
        if (getPlugin().econ != null && getPlugin().econ.getOrCreateAccount(user.getUniqueId()).isPresent())
        {
            UniqueAccount acc = getPlugin().econ.getOrCreateAccount(user.getUniqueId()).get();
            userMoney = acc.getBalance(getPlugin().econ.getDefaultCurrency()).intValue();
        }
        else
        {
            userMoney = 0;
        }

        final String playTime = CommonUtils.timeDescript(playerConfig.timePlayed);
        String nextTime;
        boolean inDisabled = false;
        
        final List<String> playerGroups = CoreModule.perms.getPlayerGroupIds(player);
        
        for (final Subject subject : CoreModule.perms.getDisabledGroups())
        {
            if (playerGroups.contains(subject.getIdentifier()))
            {
                inDisabled = true;//IF the player is withing a disabled group, stop here.
            }
        }
        
        if (inDisabled)
        {
            nextTime = "You are in a disabled group!";
        }
        else if (CoreModule.perms.getPlayerHighestRankingGroup(user) != "")
        {
            nextTime = CommonUtils.timeDescript(CoreModule.perms.timeToNextGroup(user));
        }
        else
        {
            nextTime = "You have reached the max rank!";
        }
        
        final String balance = Integer.toString(userMoney);
        final String nextBal = Integer.toString(CoreModule.perms.balanceToNextGroup(user));
        final List<String> newMessage = new ArrayList<>();

        for (final String msg : messages)
        {
            String message = msg;
            message = msg.replace("{timing-time}", playTime)
                        .replace("{timing-next}", nextTime)
                        .replace("{economy-bal}", balance)
                        .replace("{economy-next}", nextBal);

            newMessage.add(message);
        }
        return newMessage;
    }
}
