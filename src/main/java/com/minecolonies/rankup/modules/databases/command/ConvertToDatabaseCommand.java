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

public class ConvertToDatabaseCommand extends RankupSubcommand
{
    private static final int  QUARTER_OF_ONE_HUNDRED = 25;
    private static final long EXECUTE_TICK_DELAY     = 4L;

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
        return Optional.of("rankup.convert.base");
    }

    @Override
    public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException
    {
        Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
        taskBuilder.name("conf-to-database").async().delayTicks(EXECUTE_TICK_DELAY).execute(this::convert).submit(getPlugin());

        src.sendMessage(Text.of("Conversion has started, feel free to check your Console for progress!"));

        return CommandResult.success();
    }

    private void convert()
    {
        AccountConfigData accConfig = getPlugin().getConfigUtils().getAccountConfig();
        int index = 0;

        for (final UUID uuid : accConfig.playerData.keySet())
        {
            AccountConfigData.PlayerConfig playerConfig = accConfig.playerData.get(uuid);

            String joinDate = playerConfig.joinDate;
            String lastDate = playerConfig.lastVisit;

            if (getPlugin().getAccUtils().doesPlayerExist(uuid))
            {
                getPlugin().getAccUtils().updatePlayerName(uuid, playerConfig.playerName);
                getPlugin().getAccUtils().updatePlayerJoinDate(uuid, joinDate);
                getPlugin().getAccUtils().updatePlayerLastDate(uuid, lastDate);
                getPlugin().getAccUtils().updatePlayerTime(uuid, playerConfig.timePlayed);
            }
            else
            {
                getPlugin().getAccUtils().addPlayer(uuid);

                getPlugin().getAccUtils().updatePlayerName(uuid, playerConfig.playerName);
                getPlugin().getAccUtils().updatePlayerJoinDate(uuid, joinDate);
                getPlugin().getAccUtils().updatePlayerLastDate(uuid, lastDate);
                getPlugin().getAccUtils().updatePlayerTime(uuid, playerConfig.timePlayed);
            }
            index++;

            if (index % QUARTER_OF_ONE_HUNDRED == 0)
            {
                getPlugin().getLogger().info("Players converted: " + index);
            }
        }

        getPlugin().getLogger().info(index + " players added \n This may not reflect total, as any players that do not exist in sponge's user storage will not be added");
    }
}
