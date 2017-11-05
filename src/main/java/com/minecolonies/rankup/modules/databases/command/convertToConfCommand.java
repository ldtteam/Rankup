package com.minecolonies.rankup.modules.databases.command;

import com.minecolonies.rankup.internal.command.RankupSubcommand;
import com.minecolonies.rankup.modules.core.config.AccountConfigData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.UUID;

public class convertToConfCommand extends RankupSubcommand
{
    @Override
    protected String[] getAliases()
    {
        return new String[] {"database-to-conf"};
    }

    @Override
    protected Optional<Text> getDescription()
    {
        return Optional.of(Text.of("None"));
    }

    @Override
    protected Optional<String> getPermission()
    {
        return Optional.empty();
    }

    @Override
    public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException
    {
        AccountConfigData accConfig = getPlugin().configUtils.getAccountConfig();

        int index = 0;

        for (final UUID uuid : getPlugin().accUtils.getPlayers().keySet())
        {
            if (accConfig.playerData.containsKey(uuid))
            {
                AccountConfigData.PlayerConfig playerConfig = accConfig.playerData.get(uuid);

                final String name = getPlugin().accUtils.getPlayerName(uuid);
                final String joinDate = getPlugin().accUtils.getPlayerJoinDate(uuid);
                final String lastDate = getPlugin().accUtils.getPlayerLastDate(uuid);
                final int timePlayed = getPlugin().accUtils.getPlayerTime(uuid);

                playerConfig.playerName = name;
                playerConfig.joinDate = joinDate;
                playerConfig.lastVisit = lastDate;
                playerConfig.timePlayed = timePlayed;
            }
            else
            {
                accConfig.playerData.put(uuid, new AccountConfigData.PlayerConfig());

                AccountConfigData.PlayerConfig playerConfig = accConfig.playerData.get(uuid);

                final String name = getPlugin().accUtils.getPlayerName(uuid);
                final String joinDate = getPlugin().accUtils.getPlayerJoinDate(uuid);
                final String lastDate = getPlugin().accUtils.getPlayerLastDate(uuid);
                final int timePlayed = getPlugin().accUtils.getPlayerTime(uuid);

                playerConfig.playerName = name;
                playerConfig.joinDate = joinDate;
                playerConfig.lastVisit = lastDate;
                playerConfig.timePlayed = timePlayed;
            }
            index++;
        }
        accConfig.save();

        src.sendMessage(Text.of(index + " players added"));

        return CommandResult.success();
    }
}
