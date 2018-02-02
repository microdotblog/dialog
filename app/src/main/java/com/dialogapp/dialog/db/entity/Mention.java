package com.dialogapp.dialog.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.dialogapp.dialog.model.Item;

@Entity(tableName = "mentions", indices = @Index("mentions_post_id"),
        foreignKeys = @ForeignKey(entity = Item.class,
                parentColumns = "post_id", childColumns = "mentions_post_id",
                onUpdate = ForeignKey.CASCADE, deferred = true))
public class Mention {
    @PrimaryKey
    public final long id;
    public final long mentions_post_id;

    public Mention(long id, long mentions_post_id) {
        this.id = id;
        this.mentions_post_id = mentions_post_id;
    }
}
