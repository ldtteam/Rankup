package com.minecolonies.luckrankup2.modules.timing.command;

import com.minecolonies.luckrankup.utils.CommonUtils;
import com.minecolonies.luckrankup2.internal.command.LuckrankupSubcommand;
import com.minecolonies.luckrankup2.modules.core.config.AccountConfigData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.*;

import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@NonnullByDefault
public class topCommand extends LuckrankupSubcommand
{
    @Override
    protected String[] getAliases()
    {
        return new String[] {"top"};
    }

    @Override
    protected Optional<String> getPermission()
    {
        return Optional.of("rankup.top.base");
    }

    @Override
    public Optional<Text> getDescription()
    {
        return Optional.of(Text.of("Allows a player to check the Time leader board. \n Usage: /lru top"));
    }

    @Override
    public CommandElement[] getArguments()
    {
        return new CommandElement[0];
    }

    @Override
    public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException
    {
        sendTopList(src);

        return CommandResult.success();
    }

    private void sendTopList(CommandSource source)
    {
        AccountConfigData playerData = (AccountConfigData) getPlugin().getAllConfigs().get(AccountConfigData.class);

        HashMap<UUID, Integer> stats = new HashMap<>();

        for (UUID uuid : playerData.playerData.keySet())
        {
            if (playerData.playerData.get(uuid).timePlayed > 0)
            {
                stats.put(uuid, playerData.playerData.get(uuid).timePlayed);
            }
        }

        List<UUID> sorted = stats.entrySet().stream()
                              .sorted(reverseOrder(comparing(Map.Entry::getValue)))
                              .map(Map.Entry::getKey)
                              .collect(toList());


        int index = 0;
        source.sendMessage(TOP);
        for (UUID uuid : sorted)
        {
            index++;
            int time = playerData.playerData.get(uuid).timePlayed;
            final String player = playerData.playerData.get(uuid).playerName;

            source.sendMessage(Text.of(MIDDLE,
              index + ". Player name: ",
              TextColors.BLUE,
              player,
              TextColors.WHITE,
              " With time played at: ",
              TextColors.GOLD,
              CommonUtils.toText(CommonUtils.timeDescript(time))));
            if (index == 10)
            {
                break;
            }
        }
        source.sendMessage(BOTTOM);
    }
}
