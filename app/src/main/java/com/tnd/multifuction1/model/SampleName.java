package com.tnd.multifuction1.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.db.converter.ColumnConverter;
import com.lidroid.xutils.db.sqlite.ColumnDbType;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.tnd.multifuction1.db.DbHelper;

public class SampleName extends UserInputModel<SampleName> implements FiltrateModel{
    private boolean isSelect;
    public String sampleName;
    public String sampleType;
    public String sampleNumber;
    public long time;
    public List<Project> getProjects() throws DbException {
        return DbHelper.GetInstance().findAll(new Selector(Project.class).where("parent_id","=",sampleName));//一对多的查询
    }

    public List<Project> projects;
    @Override
    public String toString() {
        return "SampleName{" +
                "isSelect=" + isSelect +
                ", sampleName='" + sampleName + '\'' +
                ", sampleType='" + sampleType + '\'' +
                ", sampleNumber='" + sampleNumber + '\'' +
                ", time=" + time +
                ", projects=" + projects +
                '}';
    }
    //    @Override
//    public String toString() {
//        return "SampleName{" +
//                "isSelect=" + isSelect +
//                ", sampleName='" + sampleName + '\'' +
//                ", sampleType='" + sampleType + '\'' +
//                ", sampleNumber='" + sampleNumber + '\'' +
//                ", time=" + time +
//                ", projects=" + projects +
//                '}';
//    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getSampleType() {
        return sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    public String getSampleNumber() {
        return sampleNumber;
    }

    public void setSampleNumber(String sampleNumber) {
        this.sampleNumber = sampleNumber;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }
    @Override
    public boolean isSelect() {
        return isSelect;
    }
    public SampleName() {

    }
    public SampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public SampleName(String sampleName, String sampleType, String sampleNumber) {
        this.sampleName = sampleName;
        this.sampleType = sampleType;
        this.sampleNumber = sampleNumber;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String getName() {
        return sampleName;
    }

    public static class Project extends UserInputModel<SampleName.Project> implements Parcelable {

        public  String projectName;
        public  float jcx;
        public String parent_id;
        public Project() {
        }

        public String getParent_id() {
            return parent_id;
        }

        public void setParent_id(String parent_id) {
            this.parent_id = parent_id;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public float getJcx() {
            return jcx;
        }

        public void setJcx(float jcx) {
            this.jcx = jcx;
        }

        @Override
        public String toString() {
            return projectName;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.projectName);
            dest.writeFloat(this.jcx);
        }

        protected Project(Parcel in) {
            this.projectName = in.readString();
            this.jcx = in.readFloat();
        }

        public static final Parcelable.Creator<Project> CREATOR = new Parcelable.Creator<Project>() {
            @Override
            public Project createFromParcel(Parcel source) {
                return new Project(source);
            }

            @Override
            public Project[] newArray(int size) {
                return new Project[size];
            }
        };
    }
    public static class ProjectList<Project> extends ArrayList {

    }
    public static class ProjectConverter implements ColumnConverter<Project> {

        @Override
        public Project getFieldValue(Cursor cursor, int index) {
            if (cursor.isNull(index)){
                return null;
            }else{
                String s = cursor.getString(index);
                Gson gson =new Gson();
                Project project= gson.fromJson(s,Project.class);
                return project;
            }
        }

        @Override
        public Project getFieldValue(String fieldStringValue) {
            return null;
        }

        @Override
        public Object fieldValue2ColumnValue(Project fieldValue) {
            Gson gson = new Gson();
            String s = gson.toJson(fieldValue);
            return s;
        }

        @Override
        public ColumnDbType getColumnDbType() {
            return ColumnDbType.TEXT;
        }
    }

    public static class ProjectListConverter implements ColumnConverter<ProjectList<Project>> {


        @Override
        public ProjectList<Project> getFieldValue(Cursor cursor, int index) {
            if (cursor.isNull(index)){
                return null;
            }else{
                String s = cursor.getString(index);
                Gson gson =new Gson();
                ProjectList<Project> project= gson.fromJson(s,new TypeToken<ProjectList<Project>>(){}.getType());
                return project;
            }
        }

        @Override
        public ProjectList<Project> getFieldValue(String fieldStringValue) {
            return null;
        }

        @Override
        public String fieldValue2ColumnValue(ProjectList<Project> fieldValue) {
            Gson gson = new Gson();
            String s = gson.toJson(fieldValue);
            return s;
        }

        @Override
        public ColumnDbType getColumnDbType() {
            return ColumnDbType.TEXT;
        }
    }
}
