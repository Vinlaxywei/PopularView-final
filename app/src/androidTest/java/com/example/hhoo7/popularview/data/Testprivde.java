package com.example.hhoo7.popularview.data;

import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import static android.provider.ContactsContract.CommonDataKinds;

public class Testprivde extends AndroidTestCase {
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testcontact() {
        Cursor cursor = null;
        try {
            cursor = getContext().getContentResolver().query(Uri.parse(CommonDataKinds.Phone.DISPLAY_NAME), null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
}
