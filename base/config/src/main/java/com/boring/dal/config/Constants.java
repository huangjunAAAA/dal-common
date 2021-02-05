package com.boring.dal.config;

public class Constants {
    public static final String TYPE_FLOAT = "Type.Float";
    public static final String TYPE_DOUBLE = "Type.Double";
    public static final String TYPE_INTEGER = "Type.Integer";
    public static final String TYPE_STRING = "Type.String";

    public static final String UNKNOWN_TABLE = "UNKNOWN";
    public static final String ANY_FUNC = "ANY.FUNC";
    public static final String TYPE_DELIMITER = ".";
    public static final String NAME_DELIMITER = "#";
    public static final String TABLE_DELIMITER = "|";

    public static final String KEY_DELIMITER = "_";
    public static final String KEY_PLACEHOLDER = "*";

    public static final String LIST_CNT_SUFFIX = "#C";

    public static final String CACHE_MODE_MEM = "memcache";
    public static final String CACHE_MODE_REDIS = "redis";
    public static final String CACHE_MODE_NONE = "none";
    public static final String CACHE_MODE_AUTO = "auto";

    public static final String ITEM_MODE_LIST="list";
    public static final String ITEM_MODE_MAP="map";

    public static final String CACHE_UPDATING_PREFIX = "DirtyCheck:";
    public static final String MAXID_KEY = ":MaxId";
    public static final String VERSIONED = "Versioned:";

    public static final int CACHE_CLEAN = 0;
    public static final int CACHE_DIRTY = 1;
    public static final int SLAVE_DIRTY = 2;

    public static final int LISTCACHE_HIT = 0;
    public static final int LISTCACHE_MISS = -1;
    public static final int LISTCACHE_LEFTHIT = 1;
    public static final int LISTCACHE_RIGHTHIT = 2;
    public static String NullObject="null_obj";
}
