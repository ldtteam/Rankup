package io.github.asherslab.luckrankup;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.projectile.EnderPearl;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.awt.event.ItemEvent;

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
                                    .executor((src, args) -> {
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
                            .description(Text.of("Main command for rankupper."))
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
                            .child(playerInfo, "player-info","info")
                            .child(saveall,"save-all","save")
                            .child(loadall, "load-all","load")
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

        final int playerTime = plugin.cfgs.getPlayerTime(player.getUniqueId().toString());
        final String group = plugin.perms.getPlayerGroupWithMostParents(player);
        int timeToNextGroup = plugin.cfgs.checkRankup(player);
        source.sendMessage(top);
        source.sendMessage(Text.of(middle, "Current Player Time: ", TextColors.AQUA, playerTime));
        source.sendMessage(Text.of(middle, "Current Player Group: ", TextColors.DARK_RED, group));
        if (timeToNextGroup != -1)
        {
            source.sendMessage(Text.of(middle, "Time to Next Group: ", TextColors.GOLD, timeToNextGroup));
        }
        else
        {
            source.sendMessage(Text.of(middle, "You are not able to RankUp any further"));
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
        source.sendMessage(bottom);
    }

    private static void sendPlayerInfo(CommandSource source, User user)
    {
        final String player = plugin.cfgs.getPlayerKey(user);

        final String name = plugin.cfgs.getStatString(player, "PlayerName");
        final String firstJoin = plugin.cfgs.getStatString(player, "JoinDate");
        final String timePlayed = Integer.toString(plugin.cfgs.getPlayerTime(player));
        final String lastJoin = plugin.cfgs.getStatString(player, "LastVisit");


        source.sendMessage(top);
        source.sendMessage(Text.of(middle, "Last known player name: ", TextColors.DARK_PURPLE, name));
        source.sendMessage(Text.of(middle, "Date of first join: ", TextColors.DARK_BLUE, firstJoin));
        source.sendMessage(Text.of(middle, "Date of last join: ", TextColors.GRAY, lastJoin));
        source.sendMessage(Text.of(middle, "Time played: ", TextColors.DARK_AQUA, timePlayed));
        source.sendMessage(bottom);
    }
}
