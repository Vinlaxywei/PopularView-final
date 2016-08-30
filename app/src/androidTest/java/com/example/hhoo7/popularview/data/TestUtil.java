package com.example.hhoo7.popularview.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by hhoo7 on 2016/8/29.
 */
public class TestUtil extends AndroidTestCase {

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        //将原始资料用valueSet方法以一套键值的方式提取出来，然后赋值到另一个set对象上
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        //遍历set对象，检查添加到数据库的资料是否和原始资料有出入
        for (Map.Entry<String, Object> entry : valueSet) {
            //获取列名
            String columnName = entry.getKey();
            System.out.print("lieming+"+columnName);
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

}
