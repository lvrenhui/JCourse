package com.aligame.jcourse.library.realm;

import android.content.Context;

import com.aligame.jcourse.BuildConfig;
import com.aligame.jcourse.model.CourseRm;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by matou0289 on 2016/10/20.
 */

public class RealmHelper {
    public static final String DB_NAME = BuildConfig.class.getPackage().getName() + ".realm";
    private Realm mRealm;

    public RealmHelper(Context context) {

        mRealm = Realm.getDefaultInstance();
    }

//    /**
//     * add （增）
//     */
//    public void addDog(final Dog dog) {
//        mRealm.beginTransaction();
//        mRealm.copyToRealm(dog);
//        mRealm.commitTransaction();
//
//    }
//
//    /**
//     * delete （删）
//     */
//    public void deleteDog(String id) {
//        Dog dog = mRealm.where(Dog.class).equalTo("id", id).findFirst();
//        mRealm.beginTransaction();
//        dog.deleteFromRealm();
//        mRealm.commitTransaction();
//
//    }
//

    /**
     * update （改）
     */
    public void updateCourse(int id, String title, int childPosition, int seek) {
        CourseRm oldCourse = mRealm.where(CourseRm.class).equalTo("id", id).findFirst();
        mRealm.beginTransaction();
        if (oldCourse != null) {
            oldCourse.setPart(childPosition, seek);
        } else {
            CourseRm newCourse = new CourseRm();
            newCourse.id = id;
            newCourse.title = title;
            newCourse.setPart(childPosition, seek);
            mRealm.copyToRealm(newCourse);
        }
        mRealm.commitTransaction();
    }

    /**
     * query （查询所有）
     */
    public List<CourseRm> queryAll() {
        RealmResults<CourseRm> courses = mRealm.where(CourseRm.class).findAll();
        /**
         * 对查询结果，按Id进行排序，只能对查询结果进行排序
         */
        //增序排列
        courses = courses.sort("id");
//        //降序排列
//        dogs=dogs.sort("id", Sort.DESCENDING);
        return mRealm.copyFromRealm(courses);
    }

    /**
     * query （根据Id（主键）查）
     */
    public CourseRm queryById(int id) {
        CourseRm course = mRealm.where(CourseRm.class).equalTo("id", id).findFirst();

        return course;
    }
//
//
//    /**
//     * query （根据age查）
//     */
//    public List<Dog> queryDobByAge(int age) {
//        RealmResults<Dog> dogs = mRealm.where(Dog.class).equalTo("age", age).findAll();
//
//        return mRealm.copyFromRealm(dogs);
//    }
//
//    public boolean isDogExist(String id){
//        Dog dog=mRealm.where(Dog.class).equalTo("id",id).findFirst();
//        if (dog==null){
//            return false;
//        }else {
//            return  true;
//        }
//    }

    public Realm getRealm() {

        return mRealm;
    }

    public void close() {
        if (mRealm != null) {
            mRealm.close();
        }
    }
}
