package com.minecolonies.rankup.modules.purchase.command;

import com.minecolonies.rankup.internal.command.RankupSubcommand;
import com.minecolonies.rankup.util.RankingUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Command used to buy into a group.
 */
@NonnullByDefault
public class BuyCommand extends RankupSubcommand
{
    @Override
    protected String[] getAliases()
    {
        return new String[] {"buy"};
    }

    @Override
    protected Optional<Text> getDescription()
    {
        return Optional.of(Text.of("Allows the player to buy the next rank up in the chain."));
    }

    @Override
    protected Optional<String> getPermission()
    {
        return Optional.of("rankup.purchase.base");
    }

    @Override
    public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException
    {

        if (src instanceof Player)
        {
            final Player player = (Player) src;

            if ("".equals(getPlugin().getPerms().getNextGroup(player)))
            {
                src.sendMessage(Text.of(Color.RED, "You are in the highest group possible already."));
            }

            UniqueAccount acc = getPlugin().getEcon().getOrCreateAccount(player.getUniqueId()).orElse(null);

            if (acc == null)
            {
                return CommandResult.success();
            }

            getPlugin().getLogger().info("BUY: ", (getPlugin() == null));
            getPlugin().getLogger().info("BUY: ", (getPlugin().getConfigUtils() == null));
            getPlugin().getLogger().info("BUY: ", (getPlugin().getConfigUtils().getGroupsConfig(player) == null));
            getPlugin().getLogger().info("BUY: ", (getPlugin().getConfigUtils().getGroupsConfig(player).groups == null));
            getPlugin().getLogger().info("BUY: ", (getPlugin().getPerms().getNextGroup(player) == null));
            getPlugin().getLogger().info("BUY: ", getPlugin().getConfigUtils().getGroupsConfig(player).groups.get(getPlugin().getPerms().getNextGroup(player)).moneyNeeded);

            final int playerMoney = acc.getBalance(getPlugin().getEcon().getDefaultCurrency()).intValue();
            final int moneyNeeded = getPlugin().getConfigUtils().getGroupsConfig(player).groups.get(getPlugin().getPerms().getNextGroup(player)).moneyNeeded;

            if (playerMoney >= moneyNeeded)
            {
                acc.withdraw(getPlugin().getEcon().getDefaultCurrency(), BigDecimal.valueOf(moneyNeeded), Cause.of(EventContext.empty(), "Rankup purchase"));
                src.sendMessage(Text.of("Ranking up! New Balance: " + acc.getBalance(getPlugin().getEcon().getDefaultCurrency()).intValue()));
                RankingUtils.rankUp(player, getPlugin());
            }
            else
            {
                src.sendMessage(Text.of(Color.RED, "You do not have enough money, please us /ru check to see requirements."));
            }
        }

        return CommandResult.success();
    }
}
