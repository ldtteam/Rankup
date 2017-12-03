package com.minecolonies.rankup.modules.purchase.config;

import com.minecolonies.rankup.util.Constants;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Arrays;
import java.util.List;

/**
 * The Purchase section to the main config file.
 */
@ConfigSerializable
public class PurchaseConfig
{
    @Setting(value = "purchase-message", comment = "This is the template for the /ru buy command (before actually buying), accepted entries are same as /ru check\n"
                                                  + "Make sure you add " + Constants.ModuleInfo.PURCHASE_BUTTON + " button!")
    public List<String> purchaseMessageTemplate = Arrays.asList(
      "&6---[&2Rankup Purchase&6]---",
      "&fCurrent Rank: " + Constants.PlayerInfo.PLAYER_RANK,
      "&fNext Rank: " + Constants.PlayerInfo.PLAYER_NEXT,
      "&fMoney For Next: " + Constants.ModuleInfo.ECONOMY_NEXT,
      "&fMoney Left After Purchase: " + Constants.ModuleInfo.PURCHASE_LEFT,
      "&6---[ " + Constants.ModuleInfo.PURCHASE_BUTTON + " &6]--------");

}
