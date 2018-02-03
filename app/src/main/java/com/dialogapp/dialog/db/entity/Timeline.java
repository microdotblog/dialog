package com.dialogapp.dialog.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.dialogapp.dialog.model.Item;

@Entity(tableName = "timeline", indices = @Index("timeline_post_id"),
        foreignKeys = @ForeignKey(entity = Item.class,
                parentColumns = "post_id", childColumns = "timeline_post_id",
                onUpdate = ForeignKey.CASCADE, deferred = true))
public class Timeline {
    @PrimaryKey
    public final long id;
    public final long timeline_post_id;

    public Timeline(long id, long timeline_post_id) {
        this.id = id;
        this.timeline_post_id = timeline_post_id;
    }
}
