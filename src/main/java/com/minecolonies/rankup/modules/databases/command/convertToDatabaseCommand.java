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

public class convertToDatabaseCommand extends RankupSubcommand
{
    @Override
    protected String[] getAliases()
    {
        return new String[] {"conf-to-database"};
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

        for (final UUID uuid : accConfig.playerData.keySet())
        {
            AccountConfigData.PlayerConfig playerConfig = accConfig.playerData.get(uuid);

            if (getPlugin().accUtils.doesPlayerExist(uuid))
            {
                getPlugin().getLogger().info(playerConfig.joinDate);

                getPlugin().accUtils.updatePlayerName(uuid, playerConfig.playerName);
                getPlugin().accUtils.updatePlayerJoinDate(uuid, playerConfig.joinDate);
                getPlugin().accUtils.updatePlayerLastDate(uuid, playerConfig.lastVisit);
                getPlugin().accUtils.updatePlayerTime(uuid, playerConfig.timePlayed);
            }
            else
            {
                getPlugin().getLogger().info(playerConfig.joinDate);

                getPlugin().accUtils.addPlayer(uuid);
                getPlugin().accUtils.updatePlayerName(uuid, playerConfig.playerName);
                getPlugin().accUtils.updatePlayerJoinDate(uuid, playerConfig.joinDate);
                getPlugin().accUtils.updatePlayerLastDate(uuid, playerConfig.lastVisit);
                getPlugin().accUtils.updatePlayerTime(uuid, playerConfig.timePlayed);
            }
            index++;
        }

        src.sendMessage(Text.of(index + " players added"));

        return CommandResult.success();
    }
}
