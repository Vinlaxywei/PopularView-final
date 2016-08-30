package com.example.hhoo7.popularview.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    private static final String LOG_TAG = "test";

    @Override
    protected void setUp() throws Exception {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    public void testCreateDb() throws Throwable {
        //定义一个HashSet列表，把当前数据库中所有表格的名称添加进去。
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.DetailEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.TrailerEntry.TABLE_NAME);

        //再一次调用清理数据库的函数
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);

        //定义并实例化对象db，可进行数据库的写入写出操作
        SQLiteDatabase db = new MovieDbHelper(this.mContext).getWritableDatabase();
        //检查数据库是否创建陈宫
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        //如果数据库创建成功，则返回true。
        assertTrue("Error: 数据库返回控空值", c.moveToFirst());

        //清理列表
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        // 确认数据库是否清理完成
        assertTrue("Error: 数据库含有contract以外的表格",
                tableNameHashSet.isEmpty());

        // 现在准备数据库中表格的列表项
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.TrailerEntry.TABLE_NAME + ")", null);


        assertTrue("Error: 查询数据库失败，表格出现问题", c.moveToFirst());

        // 将contract中的列表项字符串逐项添加到set列表中
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(MovieContract.TrailerEntry._ID);
        locationColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_MOVIEID_KEY);
        locationColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_TRAILER_TITLE);
        locationColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY);

        //逐项删除
        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // 检查列表是否为空
        assertTrue("Error: 表格列表项出现问题 ",
                locationColumnHashSet.isEmpty());
        db.close();
        c.close();
    }

    public void testTrailerTable() {
        //获取外键的ID行号
        long movieRowID = getMovieId();
        //检测数据正确性
        assertTrue("movie id error", movieRowID != -1L);

        //获取数据库实例
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //创建测试内容
        ContentValues testValue = new ContentValues();
        testValue.put(MovieContract.TrailerEntry.COLUMN_MOVIEID_KEY, movieRowID);
        testValue.put(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY, "5U29SA1");
        testValue.put(MovieContract.TrailerEntry.COLUMN_TRAILER_TITLE, "max show");

        //将测试内容插入表格，并获取行号
        long rowId = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, testValue);
        //检测数据正确性
        assertTrue("插入数据失败" + rowId, rowId != -1);

        //创建游标，对准整个表格内容
        Cursor cursor = db.query(
                MovieContract.TrailerEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        //检查数据正确性
        assertTrue("数据库的游标移动失败", cursor.moveToFirst());

        //调用公共方法检查数据内容
        TestUtil.validateCurrentRecord("数据内容错误", cursor, testValue);

        //检查数据正确性
        assertFalse(cursor.moveToNext());

        //测试完毕关闭游标和数据库
        cursor.close();
        db.close();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    //测试电影详情表格是否成功创建
    private long getMovieId() {
        //获取数据库实例
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //创建测试内容
        ContentValues testValue = new ContentValues();
        testValue.put(MovieContract.DetailEntry.COLUMN_MOVIE_TITLE, "SUPERMAN");
        testValue.put(MovieContract.DetailEntry.COLUMN_POSTER_PATH, "posterpath");
        testValue.put(MovieContract.DetailEntry.COLUMN_OVER_VIEW, "over view");
        testValue.put(MovieContract.DetailEntry.COLUMN_VOTE_AVERAGE, 7.2);
        testValue.put(MovieContract.DetailEntry.COLUMN_RELEASE_DATE, "2015-02-03");
        testValue.put(MovieContract.DetailEntry.COLUMN_MOVIE_ID, 1233123);
        testValue.put(MovieContract.DetailEntry.COLUMN_POPULARITY,30.222);
        testValue.put(MovieContract.DetailEntry.COLUMN_FAVORITE, 1);

        //插入测试内容到数据库，并获取行号
        long movieID = db.insert(MovieContract.DetailEntry.TABLE_NAME, null, testValue);

        //确认插入是否成功，插入失败则行号等于-1，
        assertTrue("插入数据不正确" + movieID, movieID != -1);

        //将游标对准整个表格
        Cursor cursor = db.query(
                MovieContract.DetailEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        //将游标移动到第一条内容，如果表格有多条内容则创建失败
        assertTrue("数据库的游标移动失败", cursor.moveToFirst());

        //调用公共方法核对数据库内容
        TestUtil.validateCurrentRecord("数据内容错误", cursor, testValue);

        //将游标移动到下一条内容，如果表格有多条内容则创建失败
        assertFalse("数据库的游标移动失败", cursor.moveToNext());

        //测试完毕，关闭数据库与游标
        db.close();
        cursor.close();

        return movieID;
    }

}