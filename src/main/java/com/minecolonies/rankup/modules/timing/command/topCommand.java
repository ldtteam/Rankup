package com.minecolonies.rankup.modules.timing.command;

import com.minecolonies.rankup.internal.command.RankupSubcommand;
import com.minecolonies.rankup.modules.timing.config.TimingConfig;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.*;

import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@NonnullByDefault
public class topCommand extends RankupSubcommand
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
        return Optional.of(Text.of("Allows a player to check the Time leader board. \n Usage: /ru top"));
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
        TimingConfig timeConfig = getPlugin().getConfigUtils().getTimingConfig();

        final Map<UUID, Integer> stats = getPlugin().getAccUtils().getPlayers();

        List<UUID> sorted = stats.entrySet().stream()
                              .sorted(reverseOrder(comparing(Map.Entry::getValue)))
                              .map(Map.Entry::getKey)
                              .collect(toList());

        int index = 0;
        source.sendMessage(convertToText(timeConfig.topMessageHead));
        for (UUID uuid : sorted)
        {
            index++;

            User user;
            // For some reason the player has to be queried twice??? it's so weird.
            Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(uuid).isPresent();
            if (Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(uuid).isPresent())
            {
                user = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(uuid).get();
            }
            else
            {
                source.sendMessage(convertToText(timeConfig.topMessageFoot));
                return;
            }

            final List<String> message = getModuleData(user, getPlayerData(user, timeConfig.topMessageTemplate));

            for (final Text msg : convertToText(message))
            {
                source.sendMessage(msg);
            }

            if (index == 10)
            {
                break;
            }
        }
        source.sendMessage(convertToText(timeConfig.topMessageFoot));
    }
}
