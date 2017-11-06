package com.minecolonies.rankup.modules.databases.command;

import com.minecolonies.rankup.internal.command.RankupSubcommand;
import com.minecolonies.rankup.modules.core.config.AccountConfigData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.scheduler.Task.Builder;
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
        Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
        taskBuilder.name("conf-to-database").async().delayTicks(4L).execute(this::convert).submit(getPlugin());

        src.sendMessage(Text.of("Conversion has started, feel free to check your Console for progress!"));

        return CommandResult.success();
    }

    private void convert()
    {
        AccountConfigData accConfig = getPlugin().configUtils.getAccountConfig();
        int index = 0;

        for (final UUID uuid : accConfig.playerData.keySet())
        {
            AccountConfigData.PlayerConfig playerConfig = accConfig.playerData.get(uuid);

            String joinDate = playerConfig.joinDate;
            String lastDate = playerConfig.lastVisit;

            if (getPlugin().accUtils.doesPlayerExist(uuid))
            {
                getPlugin().accUtils.updatePlayerName(uuid, playerConfig.playerName);
                getPlugin().accUtils.updatePlayerJoinDate(uuid, joinDate);
                getPlugin().accUtils.updatePlayerLastDate(uuid, lastDate);
                getPlugin().accUtils.updatePlayerTime(uuid, playerConfig.timePlayed);
            }
            else
            {
                getPlugin().accUtils.addPlayer(uuid);

                getPlugin().accUtils.updatePlayerName(uuid, playerConfig.playerName);
                getPlugin().accUtils.updatePlayerJoinDate(uuid, joinDate);
                getPlugin().accUtils.updatePlayerLastDate(uuid, lastDate);
                getPlugin().accUtils.updatePlayerTime(uuid, playerConfig.timePlayed);
            }
            index++;

            if (index % 25 == 0)
            {
                getPlugin().getLogger().info("Players converted: " + index);
            }
        }

        getPlugin().getLogger().info(index + " players added \n This may not reflect total, as any players that do not exist in sponge's user storage will not be added");
    }
}
