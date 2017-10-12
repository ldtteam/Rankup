package com.minecolonies.rankup.internal.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is Rankup's main command
 */
public final class RankupCommand implements CommandCallable
{
    public static final Text TOP    = Text.of(TextColors.LIGHT_PURPLE, "####################");
    public static final Text MIDDLE = Text.of(TextColors.LIGHT_PURPLE, "##   ");
    public static final Text BOTTOM = Text.of(TextColors.LIGHT_PURPLE, "####################");

    private InputTokenizer tokenizer = InputTokenizer.quotedStrings(false);

    /**
     * The subcommands that this command will handle.
     */
    private final Map<String, CommandSpec> subCommands = Maps.newHashMap();

    /**
     * Registers a {@link RankupSubcommand}
     *
     * @param subcommandToRegister The subcommand to register.
     * @return <code>true</code> if successful.
     */
    public boolean registerSubCommand(RankupSubcommand subcommandToRegister)
    {
        // Work out how the sub command system will work - probably annotation based, but not sure yet.
        // By definition, all subcommands will be lower case

        // Register the command.
        Collection<String> sc = Arrays.asList(subcommandToRegister.getAliases());
        if (subCommands.keySet().stream().map(String::toLowerCase).noneMatch(sc::contains))
        {
            // We can register the aliases. Create the CommandSpec
            // We might want to add descriptions in.
            CommandSpec.Builder specbuilder = CommandSpec.builder();
            subcommandToRegister.getPermission().ifPresent(specbuilder::permission);
            subcommandToRegister.getDescription().ifPresent(specbuilder::description);
            CommandSpec spec = specbuilder
                                 .arguments(subcommandToRegister.getArguments())
                                 .executor(subcommandToRegister)
                                 .build();

            sc.forEach(x -> subCommands.put(x.toLowerCase(), spec));
            return true;
        }

        return false;
    }

    /**
     * Removes the specified sub command type from the mapping.
     * <p>
     * <p>
     * This is a class, so we don't have to hold a reference to the actual command.
     * </p>
     *
     * @param subcommand The class of the subcommand to remove.
     * @return Whether a subcommand was removed.
     */
    public boolean removeSubCommand(Class<? extends RankupSubcommand> subcommand)
    {
        Collection<String> keysToRemove = subCommands.entrySet().stream()
                                            .filter(x -> x.getValue().getExecutor() instanceof RankupSubcommand)
                                            .map(Map.Entry::getKey).collect(Collectors.toList());

        if (keysToRemove.isEmpty())
        {
            return false;
        }

        keysToRemove.forEach(subCommands::remove);
        return true;
    }

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException
    {
        // Get the first argument, is it a child?
        final CommandArgs args = new CommandArgs(arguments, tokenizer.tokenize(arguments, false));

        Optional<CommandSpec> optionalSpec = getSpec(args);
        if (optionalSpec.isPresent())
        {
            CommandSpec spec = optionalSpec.get();
            CommandContext context = new CommandContext();
            spec.checkPermission(source);
            spec.populateContext(source, args, context);
            return spec.getExecutor().execute(source, context);
        }


        // Else, what do we want to do here?
        source.sendMessage(Text.of("Rankup from Minecolonies team (Namely Asherslab!) \n"));
        return CommandResult.success();
    }

    private String getFirst(CommandArgs a)
    {
        try
        {
            return a.peek().toLowerCase();
        }
        catch (Exception e)
        {
            return "";
        }
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition)
      throws CommandException
    {
        final CommandArgs args = new CommandArgs(arguments, tokenizer.tokenize(arguments, false));
        final String firstArg;

        try
        {
            firstArg = args.peek().toLowerCase();
        }
        catch (Exception e)
        {
            return Lists.newArrayList(subCommands.keySet());
        }

        try
        {
            Optional<CommandSpec> optionalSpec = getSpec(args);
            if (optionalSpec.isPresent())
            {
                CommandSpec spec = optionalSpec.get();
                CommandContext context = new CommandContext();
                spec.checkPermission(source);
                return spec.complete(source, args, context);
            }
        }
        catch (Exception e)
        {
            // ignored - most likely not a sub command.
        }

        // Only if this is the first arg.
        if (args.getAll().size() == 1)
        {
            return subCommands.entrySet().stream().filter(x -> x.getKey().startsWith(firstArg))
                     .filter(x -> x.getValue().testPermission(source))
                     .map(Map.Entry::getKey)
                     .collect(Collectors.toList());
        }

        return Lists.newArrayList();
    }

    @Override
    public boolean testPermission(CommandSource source)
    {
        // This is for the root command only. Should probably be changed.
        return true;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source)
    {
        return Optional.empty();
    }

    @Override
    public Optional<Text> getHelp(CommandSource source)
    {
        sendHelp(source);

        return Optional.empty();
    }

    @Override
    public Text getUsage(CommandSource source)
    {
        return Text.EMPTY;
    }

    private Optional<CommandSpec> getSpec(CommandArgs args) throws CommandException
    {
        if (args.hasNext())
        {
            String child = args.next().toLowerCase();
            if (subCommands.containsKey(child))
            {
                // Try to execute it.
                final CommandContext context = new CommandContext();
                return Optional.of(subCommands.get(child));
            }

            // Just temporary.
            throw new CommandException(Text.of(TextColors.RED, child, " is not a valid subcommand!"), true);
        }

        return Optional.empty();
    }

    private static void sendHelp(CommandSource source)
    {
        source.sendMessage(TOP);
        source.sendMessage(Text.of(MIDDLE, TextColors.DARK_GREEN, "/ru add [time] <player>", " - adds <time> to <player>s stats"));
        source.sendMessage(Text.of(MIDDLE, TextColors.DARK_GREEN, "/ru set [time] <player>", " - sets <player>s time to <time>"));
        source.sendMessage(Text.of(MIDDLE, TextColors.DARK_GREEN, "/ru check <player>", " - shows players stats"));
        source.sendMessage(Text.of(MIDDLE, TextColors.DARK_GREEN, "/ru top", " - show's top 10 players"));
        source.sendMessage(BOTTOM);
    }
}
