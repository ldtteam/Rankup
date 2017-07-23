package io.github.asherslab.luckrankup;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class Commands
{
    private static Luckrankup plugin;

    public static void init(Luckrankup pl)
    {
        plugin = pl;
        Sponge.getCommandManager().register(plugin, luckRankUp(), "luckrankup", "lru");
    }

    private static final Text top    = Text.of(TextColors.LIGHT_PURPLE, "####################");
    private static final Text middle = Text.of(TextColors.LIGHT_PURPLE, "##   ");
    private static final Text bottom = Text.of(TextColors.LIGHT_PURPLE, "####################");

    private static CommandCallable luckRankUp()
    {
        CommandSpec set = CommandSpec.builder()
                            .description(Text.of("Set time played by player."))
                            .permission("luckrankup.set")
                            .arguments(
                              GenericArguments.userOrSource(Text.of("player")),
                              GenericArguments.integer(Text.of("minutes")))
                            .executor((src, args) ->
                            {
                                {
                                    final int time = args.<Integer>getOne("minutes").orElse(0);
                                    if (args.getOne("player").orElse(null) instanceof User)
                                    {
                                        User user = args.<User>getOne("player").orElse(null);
                                        if (user == null)
                                        {
                                            src.sendMessage(Text.of(TextColors.DARK_RED, "User must be Online"));
                                            return CommandResult.success();
                                        }
                                        plugin.cfgs.setPlayerTime(plugin.cfgs.getPlayerKey(user), time);
                                        plugin.cfgs.savePlayerStats();
                                        sendCheck(src, user);
                                    }
                                    return CommandResult.success();
                                }
                            })
                            .build();

        CommandSpec add = CommandSpec.builder()
                            .description(Text.of("Add time played by player."))
                            .permission("luckrankup.add")
                            .arguments(
                              GenericArguments.userOrSource(Text.of("player")),
                              GenericArguments.integer(Text.of("minutes")))
                            .executor((src, args) ->
                            {
                                {
                                    final int time = args.<Integer>getOne("minutes").orElse(0);
                                    if (args.getOne("player").orElse(null) instanceof User)
                                    {
                                        User user = args.<User>getOne("player").orElse(null);
                                        if (user == null)
                                        {
                                            src.sendMessage(Text.of(TextColors.DARK_RED, "User must be Online"));
                                            return CommandResult.success();
                                        }
                                        plugin.cfgs.addPlayerTime(plugin.cfgs.getPlayerKey(user), time);
                                        plugin.cfgs.savePlayerStats();
                                        sendCheck(src, user);
                                    }
                                    return CommandResult.success();
                                }
                            })
                            .build();

        CommandSpec check = CommandSpec.builder()
                              .description(Text.of("Check if requirements is done to rankup."))
                              .permission("luckrankup.check")
                              .arguments(
                                GenericArguments.userOrSource(Text.of("player")))
                              .executor((src, args) ->
                              {
                                  if (args.getOne("player").orElse(null) instanceof User)
                                  {
                                      sendCheck(src, args.<User>getOne("player").orElse(null));
                                      return CommandResult.success();
                                  }
                                  else
                                  {
                                      src.sendMessage(Text.of(TextColors.DARK_RED, "Not Enough Arguments"));
                                      return CommandResult.success();
                                  }
                              })
                              .build();

        CommandSpec topList = CommandSpec.builder()
                            .description(Text.of("Display list of top 10 most played players."))
                            .permission("luckrankup.top")
                            .executor((src, args) ->
                            {
                                {
                                    sendTopList(src);
                                    return CommandResult.success();
                                }
                            })
                            .build();

        CommandSpec reload = CommandSpec.builder()
                               .description(Text.of("Reload rankupper."))
                               .permission("luckrankup.reload")
                               .executor((src, args) ->
                               {
                                   {
                                       plugin.reload();
                                       src.sendMessage(Text.of("Luckrankup reloaded!"));
                                       return CommandResult.success();
                                   }
                               })
                               .build();

        CommandSpec help = CommandSpec.builder()
                             .description(Text.of("Help command for rankupper."))
                             .executor((src, args) ->
                             {
                                 {
                                     sendHelp(src);
                                     return CommandResult.success();
                                 }
                             })
                             .build();

        CommandSpec playerInfo = CommandSpec.builder()
                                   .description(Text.of("Returns info about a player"))
                                   .permission("luckrankup.playerinfo")
                                   .arguments(GenericArguments.userOrSource(Text.of("user")))
                                   .executor((src, args) ->
                                   {
                                       {
                                           sendPlayerInfo(src, args.<User>getOne("user").orElse(null));
                                           return CommandResult.success();
                                       }
                                   })
                                   .build();

        CommandSpec saveall = CommandSpec.builder()
                                .description(Text.of("Saves all player stats to file"))
                                .permission("luckrankup.saveall")
                                .executor((src, args) ->
                                {
                                    {
                                        plugin.cfgs.savePlayerStats();
                                        src.sendMessage(Text.of("Saved all stats"));
                                        return CommandResult.success();
                                    }
                                })
                                .build();

        CommandSpec loadall = CommandSpec.builder()
                                .description(Text.of("Loads all player stats from file"))
                                .permission("luckrankup.loadall")
                                .executor((src, args) ->
                                {
                                    {
                                        plugin.loadall();
                                        src.sendMessage(Text.of("Loaded all stats"));
                                        return CommandResult.success();
                                    }
                                })
                                .build();

        CommandSpec lru = CommandSpec.builder()
                            .description(Text.of("Main command for luck-rankupper."))
                            .executor((src, args) ->
                            {
                                {
                                    //no args
                                    src.sendMessage(top);
                                    src.sendMessage(Text.of(middle, plugin.get().getName()));
                                    src.sendMessage(Text.of(middle, "Developed by " + plugin.get().getAuthors().get(0) + "."));
                                    src.sendMessage(Text.of(middle, "For more information about the commands, type /lru help"));
                                    src.sendMessage(bottom);
                                    return CommandResult.success();
                                }
                            })
                            .child(help, "?", "help")
                            .child(reload, "reload", "rl")
                            .child(add, "add")
                            .child(set, "set")
                            .child(check, "check")
                            .child(playerInfo, "player-info", "info")
                            .child(saveall, "save-all", "save")
                            .child(loadall, "load-all", "load")
                            .child(topList, "top")
                            .build();

        return lru;
    }

    private static void sendCheck(CommandSource source, User user)
    {
        if (user == null)
        {
            source.sendMessage(Text.of(TextColors.DARK_RED, "User must be Online"));
            return;
        }

        final Player player = user.getPlayer().orElse(null);

        if (player == null)
        {
            source.sendMessage(Text.of(TextColors.DARK_RED, "Player must be Online"));
            return;
        }

        int playerTime = plugin.cfgs.getPlayerTime(player.getUniqueId().toString());
        final String group = plugin.perms.getPlayerGroupWithMostParents(player);
        int timeToNextGroup = plugin.cfgs.checkRankup(player);

        final long days = TimeUnit.MINUTES.toDays(playerTime);
        playerTime -= TimeUnit.DAYS.toMinutes(days);

        final long daysGroup = TimeUnit.MINUTES.toDays(playerTime);
        timeToNextGroup -= TimeUnit.DAYS.toMinutes(daysGroup);

        final long hours = TimeUnit.MINUTES.toHours(playerTime);
        playerTime -= TimeUnit.HOURS.toMinutes(hours);

        final long hoursGroup = TimeUnit.MINUTES.toHours(playerTime);
        timeToNextGroup -= TimeUnit.HOURS.toMinutes(hoursGroup);

        source.sendMessage(top);
        source.sendMessage(Text.of(middle, "Current Player Time: ", TextColors.AQUA,  days + " days " + hours + " hours and " + playerTime + " minutes"));
        source.sendMessage(Text.of(middle, "Current Player Group: ", TextColors.DARK_RED, group));
        if (timeToNextGroup != -1)
        {
            source.sendMessage(Text.of(middle, "Time to Next Group: ", TextColors.GOLD, daysGroup + " days " + hoursGroup + " hours and " + timeToNextGroup + " minutes"));
        }
        else
        {
            source.sendMessage(Text.of(middle, "You are not able to RankUp any further"));
        }
        source.sendMessage(bottom);
    }

    private static void sendTopList(CommandSource source)
    {
        HashMap<String, Integer> stats = new HashMap<>();

        for (Object uuid : plugin.cfgs.stats().getChildrenMap().keySet())
        {
            if (plugin.cfgs.getPlayerTime(uuid.toString()) > 0){
                stats.put(uuid.toString(), plugin.cfgs.getPlayerTime(uuid.toString()));
            }
        }

        List<String> sorted = stats.entrySet().stream()
                                .sorted(reverseOrder(comparing(Map.Entry::getValue)))
                                .map(Map.Entry::getKey)
                                .collect(toList());


        int index = 0;
        source.sendMessage(top);
        for (String uuid : sorted)
        {
            index++;
            int time = plugin.cfgs.getPlayerTime(uuid);
            final String player = plugin.cfgs.getStatString(uuid,"PlayerName");

            final long days = TimeUnit.MINUTES.toDays(time);
            time -= TimeUnit.DAYS.toMinutes(days);

            final long hours = TimeUnit.MINUTES.toHours(time);
            time -= TimeUnit.HOURS.toMinutes(hours);

            source.sendMessage(Text.of(middle, index + ". Player name: ", TextColors.BLUE, player,TextColors.WHITE ," With time played at: ", TextColors.GOLD, days + " days " + hours + " hours and " + time + " minutes"));
            if (index == 10)
            {
                break;
            }
        }
        source.sendMessage(bottom);
    }

    private static void sendHelp(CommandSource source)
    {
        source.sendMessage(top);
        source.sendMessage(Text.of(middle, TextColors.DARK_GREEN, "/lru help", " - Shows this help page"));
        source.sendMessage(Text.of(middle, TextColors.DARK_GREEN, "/lru reload", " - Reloads all configs and saves player stats"));
        source.sendMessage(Text.of(middle, TextColors.DARK_GREEN, "/lru add <player> <time>", " - adds <time> to <player>s stats"));
        source.sendMessage(Text.of(middle, TextColors.DARK_GREEN, "/lru set <player> <time>", " - sets <player>s time to <time>"));
        source.sendMessage(Text.of(middle, TextColors.DARK_GREEN, "/lru check <player>", " - checks for available rankup, MUST BE ONLINE"));
        source.sendMessage(Text.of(middle, TextColors.DARK_GREEN, "/lru info <player>", " - shows a player's stats"));
        source.sendMessage(Text.of(middle, TextColors.DARK_GREEN, "/lru save-all", " - saves player-stats file"));
        source.sendMessage(Text.of(middle, TextColors.DARK_GREEN, "/lru load-all", " - loads player-stats file"));
        source.sendMessage(Text.of(middle, TextColors.DARK_GREEN, "/lru top", " - show's top 10 players"));
        source.sendMessage(bottom);
    }

    private static void sendPlayerInfo(CommandSource source, User user)
    {
        final String player = plugin.cfgs.getPlayerKey(user);

        final String name = plugin.cfgs.getStatString(player, "PlayerName");
        final String firstJoin = plugin.cfgs.getStatString(player, "JoinDate");
        long timePlayed = plugin.cfgs.getPlayerTime(player);
        final String lastJoin = plugin.cfgs.getStatString(player, "LastVisit");

        final long days = TimeUnit.MINUTES.toDays(timePlayed);
        timePlayed -= TimeUnit.DAYS.toMinutes(days);

        final long hours = TimeUnit.MINUTES.toHours(timePlayed);
        timePlayed -= TimeUnit.HOURS.toMinutes(hours);

        source.sendMessage(top);
        source.sendMessage(Text.of(middle, "Last known player name: ", TextColors.DARK_PURPLE, name));
        source.sendMessage(Text.of(middle, "Date of first join: ", TextColors.DARK_BLUE, firstJoin));
        source.sendMessage(Text.of(middle, "Date of last join: ", TextColors.GRAY, lastJoin));
        source.sendMessage(Text.of(middle, "Time played: ", TextColors.DARK_AQUA, days + " days " + hours + " hours and " + timePlayed + " sminutes"));
        source.sendMessage(bottom);
    }
}
