package com.lidroid.xutils.db.converter;

import android.database.Cursor;

import com.lidroid.xutils.db.model.Sample;
import com.lidroid.xutils.db.sqlite.ColumnDbType;

public class SampleColumnConverter implements ColumnConverter<Sample> {

    @Override
    public Sample getFieldValue(Cursor cursor, int index) {
        if(cursor.isNull(index)) return null;
        else {
            String valueStr = cursor.getString(index);
            String[] st = valueStr.split(",");
            if (st.length != 2) {
                return null;
            } else {
                return new Sample(st[0], st[1]);
            }
        }
    }

    @Override
    public Sample getFieldValue(String fieldStringValue) {
        if (fieldStringValue == null) return null;
        return null;
    }

    @Override
    public Object fieldValue2ColumnValue(Sample fieldValue) {
        return fieldValue.sampleName + "," + fieldValue.sampleNo;
    }

    @Override
    public ColumnDbType getColumnDbType() {
        return ColumnDbType.TEXT;
    }
}
