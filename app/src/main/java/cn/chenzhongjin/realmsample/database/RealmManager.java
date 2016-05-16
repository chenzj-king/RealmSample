package cn.chenzhongjin.realmsample.database;

import cn.chenzhongjin.realmsample.AppContext;
import cn.chenzhongjin.realmsample.entity.User;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmResults;
import io.realm.RealmSchema;


/**
 * @author chenzj
 * @Title: RealmManager
 * @Description: 类的描述 -
 * @date 2016/4/19 10:47
 * @email admin@chenzhongjin.cn
 */
public class RealmManager {

    private static final String TAG = "RealmManager";

    private static RealmManager singleton;
    private Realm mRealm;

    public static RealmManager getInstanse() {
        if (singleton == null) {
            synchronized (RealmManager.class) {

                if (singleton == null) {
                    singleton = new RealmManager();
                }
            }
        }
        return singleton;
    }

    public RealmManager() {

        RealmConfiguration config = new RealmConfiguration.Builder(AppContext.getInstance())
                .schemaVersion(1)
                .name("sample.realm")
                .modules(new CustomModule())
                .migration(mRealmMigration)
                .build();
        mRealm = Realm.getInstance(config);
    }

    public Realm getRealm() {
        return mRealm;
    }

    RealmMigration mRealmMigration = new RealmMigration() {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

            // DynamicRealm exposes an editable schema
            RealmSchema schema = realm.getSchema();

            // Migrate to version 1: Add a new class.
            // Example:
            // public Person extends RealmObject {
            //     private String name;
            //     private int age;
            //     // getters and setters left out for brevity
            // }
            if (oldVersion == 0) {
                /*
                schema.create("Person")
                        .addField("name", String.class)
                        .addField("age", int.class);
                        */
                oldVersion++;
            }

            // Migrate to version 2: Add a primary key + object references
            // Example:
            // public Person extends RealmObject {
            //     private String name;
            //     @PrimaryKey
            //     private int age;
            //     private Dog favoriteDog;
            //     private RealmList<Dog> dogs;
            //     // getters and setters left out for brevity
            // }
            if (oldVersion == 1) {
                /*
                schema.get("Person")
                        .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                        .addRealmObjectField("favoriteDog", schema.get("Dog"))
                        .addRealmListField("dogs", schema.get("Dog"));
                        */
                oldVersion++;
            }
        }
    };

    // TODO: 2016/4/25 使用sample
    private void addData() {
        mRealm.beginTransaction();

        User user = mRealm.createObject(User.class);

        mRealm.commitTransaction();
    }

    private void delData() {
        // obtain the results of a query
        RealmResults<User> results = mRealm.where(User.class).findAll();
        // All changes to data must happen in a transaction
        mRealm.beginTransaction();

        // remove single match
        results.deleteFromRealm(0);
        results.deleteLastFromRealm();

        // remove a single object
        User baseFileBean = results.get(5);
        baseFileBean.deleteFromRealm();

        // Delete all matches
        //results.clear();
        mRealm.commitTransaction();
    }

    private void updateData() {

        RealmResults<User> results = mRealm.where(User.class).findAll();

        mRealm.beginTransaction();

        User user = results.get(0);
        //change the field
        user.age = 1;

        mRealm.commitTransaction();
    }

    private void queryData() {

        RealmResults<User> results = mRealm.where(User.class)
                .equalTo("name", "chenzj")
                .findAll();

        for (User user : results) {

        }
    }

}
