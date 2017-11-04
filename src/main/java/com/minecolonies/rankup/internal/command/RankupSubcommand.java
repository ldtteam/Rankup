package com.minecolonies.rankup.internal.command;

import com.google.inject.Inject;
import com.minecolonies.rankup.Rankup;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

/**
 * Extend this in any module to create a new Rankup command.
 */
public abstract class RankupSubcommand implements CommandExecutor
{
    public static final Text TOP    = Text.of(TextColors.LIGHT_PURPLE, "####################");
    public static final Text MIDDLE = Text.of(TextColors.LIGHT_PURPLE, "##   ");
    public static final Text BOTTOM = Text.of(TextColors.LIGHT_PURPLE, "####################");

    private static CommandElement[] empty = new CommandElement[0];
    @Inject
    private Rankup plugin;

    protected abstract String[] getAliases();

    protected abstract Optional<Text> getDescription();

    protected abstract Optional<String> getPermission();

    protected final Rankup getPlugin()
    {
        return this.plugin;
    }

    public CommandElement[] getArguments()
    {
        return empty;
    }
}
