package com.commons;

/**
 * creator: sunc
 * date: 2017/4/18
 * description:
 */
public class Commons {

    public static long id = Long.MAX_VALUE;

    public static final int UNLOCK = 0;
    public static final int INSERT = 1;
    public static final int RESTORE = 2;

    public static final int LIMIT_SIZE_MAX = 10000;
    public static final int LIMIT_SIZE_MIN = 1000;

    public enum COLUMN_TYPE {
        columnValue,
        intAttributeColumn,
        dateAttributeColumn,
        stringAttributeColumn,
        intMeasureColumn,
        floatMeasureColumn
    }

    public static int columnNumber(String column_type) {
        if ("".equals(column_type))
            return 0;
        for (COLUMN_TYPE type : COLUMN_TYPE.values()) {
            if (type.toString().equals(column_type)) {
                return type.ordinal();
            }
        }
        return 0;
    }

    public static String columnType(int column_number) {
        if (column_number < 0 || column_number >= COLUMN_TYPE.values().length)
            return "";
        return COLUMN_TYPE.values()[column_number].toString();
    }

}
