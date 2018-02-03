package com.dialogapp.dialog.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.dialogapp.dialog.model.Item;

@Entity(tableName = "favorites", indices = @Index("favorites_post_id"),
        foreignKeys = @ForeignKey(entity = Item.class,
                parentColumns = "post_id", childColumns = "favorites_post_id",
                onUpdate = ForeignKey.CASCADE, deferred = true))
public class Favorite {
    @PrimaryKey
    public final long id;
    public final long favorites_post_id;

    public Favorite(long id, long favorites_post_id) {
        this.id = id;
        this.favorites_post_id = favorites_post_id;
    }
}
