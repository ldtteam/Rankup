package com.minecolonies.rankup.modules.databases.command;

import com.minecolonies.rankup.internal.command.RankupSubcommand;
import com.minecolonies.rankup.modules.core.config.AccountConfigData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.UUID;

public class ConvertToConfCommand extends RankupSubcommand
{
    private static final int QUARTER_OF_ONE_HUNDRED = 25;
    private static final long EXECUTE_TICK_DELAY = 4L;

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
        return Optional.of("rankup.convert.base");
    }

    @Override
    public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException
    {
        Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
        taskBuilder.name("conf-to-database").async().delayTicks(EXECUTE_TICK_DELAY).execute(this::convert).submit(getPlugin());

        src.sendMessage(Text.of("Conversion has started, feel free to check your Console for progress!"));

        return CommandResult.success();
    }

    private void convert()
    {
        AccountConfigData accConfig = getPlugin().getConfigUtils().getAccountConfig();

        int index = 0;

        for (final UUID uuid : getPlugin().getAccUtils().getPlayers().keySet())
        {
            if (accConfig.playerData.containsKey(uuid))
            {
                AccountConfigData.PlayerConfig playerConfig = accConfig.playerData.get(uuid);

                final String name = getPlugin().getAccUtils().getPlayerName(uuid);
                final String joinDate = getPlugin().getAccUtils().getPlayerJoinDate(uuid);
                final String lastDate = getPlugin().getAccUtils().getPlayerLastDate(uuid);
                final int timePlayed = getPlugin().getAccUtils().getPlayerTime(uuid);

                playerConfig.playerName = name;
                playerConfig.joinDate = joinDate;
                playerConfig.lastVisit = lastDate;
                playerConfig.timePlayed = timePlayed;
            }
            else
            {
                accConfig.playerData.put(uuid, new AccountConfigData.PlayerConfig());

                AccountConfigData.PlayerConfig playerConfig = accConfig.playerData.get(uuid);

                final String name = getPlugin().getAccUtils().getPlayerName(uuid);
                final String joinDate = getPlugin().getAccUtils().getPlayerJoinDate(uuid);
                final String lastDate = getPlugin().getAccUtils().getPlayerLastDate(uuid);
                final int timePlayed = getPlugin().getAccUtils().getPlayerTime(uuid);

                playerConfig.playerName = name;
                playerConfig.joinDate = joinDate;
                playerConfig.lastVisit = lastDate;
                playerConfig.timePlayed = timePlayed;
            }
            index++;

            if (index % QUARTER_OF_ONE_HUNDRED == 0)
            {
                getPlugin().getLogger().info("Players converted: " + index);
            }
        }
        accConfig.save();

        getPlugin().getLogger().info(index + " players added");
    }
}
