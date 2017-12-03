package com.minecolonies.rankup.util;

/**
 * Constants for our plugin.
 */
public final class Constants
{

    private Constants()
    {
        //Empty private constructor.
    }

    public static final class PlayerInfo
    {
        public static final String PLAYER_NAME   = "{player}";
        public static final String PLAYER_GROUP  = "{group}";
        public static final String PLAYER_RANK   = "{rank}";
        public static final String PLAYER_NEXT   = "{rank-next}";
        public static final String PLAYER_PREFIX = "{prefix}";
        public static final String PLAYER_JOIN   = "{joindate}";
        public static final String PLAYER_LAST   = "{lastjoin}";
        public static final String PLAYER_TRACK  = "{track}";

        private PlayerInfo()
        {
            //Empty private constructor.
        }
    }

    public static final class ModuleInfo
    {
        public static final String TIMING_TIME     = "{timing-time}";
        public static final String TIMING_NEXT     = "{timing-next}";
        public static final String ECONOMY_BAL     = "{economy-bal}";
        public static final String ECONOMY_NEXT    = "{economy-next}";
        public static final String MAGIBRIDGE_CURR = "{magibridge-current}";
        public static final String MAGIBRIDGE_NEXT = "{magibridge-next}";
        public static final String PURCHASE_BUTTON = "{purchase-button}";
        public static final String PURCHASE_LEFT   = "{purchase-left}";

        private ModuleInfo()
        {
            //Empty private constructor.
        }
    }

    public static final class SQL
    {
        public static final String WHERE  = "WHERE";
        public static final String FROM   = "FROM";
        public static final String SELECT = "SELECT";
        public static final String SET    = "SET";
        public static final String UPDATE = "UPDATE";

        private SQL()
        {
            //Empty private constructor.
        }
    }
}
