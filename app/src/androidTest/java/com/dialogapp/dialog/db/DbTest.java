package com.dialogapp.dialog.db;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;

public abstract class DbTest {
    protected MicroBlogDb db;

    @Before
    public void setUp() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), MicroBlogDb.class)
                .build();
    }

    @After
    public void tearDown() throws Exception {
        db.close();
    }
}
