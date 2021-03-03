package com.github.supermoonie.cef;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * @author supermoonie
 * @since 2021/2/25
 */
@DatabaseTable(tableName = "house")
public class House {

    public static final String TIME_CREATED = "timeCreated";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = TIME_CREATED, canBeNull = false, index = true, indexName = "idx_time_created")
    private Date timeCreated;
}
