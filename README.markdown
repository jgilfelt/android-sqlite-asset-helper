Android SQLiteAssetHelper
=========================

An Android helper class to manage database creation and version management using an application's raw asset files.

This class provides developers with a simple way to ship their Android app with an existing SQLite database (which may be pre-populated with data) and to manage its initial creation and any upgrades required with subsequent version releases.

It is implemented as an extension to `SQLiteOpenHelper`, providing an efficient way for `ContentProvider` implementations to defer opening and upgrading the database until first use.

Rather than implementing `onCreate()` and `onUpgrade()` methods to execute a bunch of SQL statements, developers simply include appropriately named file assets in their project's `assets` directory. These will include the initial SQLite database file for creation and optionally any SQL upgrade scripts.

Setup
-----

#### Gradle

If you are using the Gradle build system, simply add the following dependency in your `build.gradle` file:

```groovy
dependencies {
    compile 'com.readystatesoftware.sqliteasset:sqliteassethelper:+'
}
```

#### Ant/Eclipse

If you are using the old build system, download and place the latest library [JAR][1] inside your project's `libs` folder.


Usage
-----

Copy [android-sqlite-asset-helper.jar](https://github.com/jgilfelt/android-sqlite-asset-helper/blob/master/lib/android-sqlite-asset-helper.jar?raw=true) into your Android project's `libs` directory and add it to the build path.

Extend `SQLiteAssetHelper` as you would normally do `SQLiteOpenHelper`, providing the constructor with a database name and version number:

```java
public class MyDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "northwind";
    private static final int DATABASE_VERSION = 1;

    public MyDatabase(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
```

The name of the database must match a zip compressed file placed in your project's `assets/databases` directory. This zip file must contain a single SQLite database file. For example:

    assets/databases/northwind.zip

The SQLite database file must be the only file within the zip archive. The database file itself can be named anything you like. ZIP compression is used to minimize APK file size while ensuring that aapt (part of the Android build process) does not corrupt large database files during its own compression process.

The database will be extracted from the assets and copied into place within your application's private data directory. If you prefer to store the database file somewhere else (such as external storage) you can use the alternate constructor to specify a storage path. You must ensure that this path is available and writable whenever your application needs to access the database.

    super(context, DATABASE_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);

The database is made available for use the first time either `getReadableDatabase()` or `getWritableDatabase()` is called.

The class will throw a `SQLiteAssetHelperException` if you do not provide the appropriately named file.

The [samples:database-v1](https://github.com/jgilfelt/android-sqlite-asset-helper/tree/v2/samples/database-v1) project demonstrates a simple database creation and usage example using the classic Northwind database.

Database Upgrades
-----------------

At a certain point in your application's lifecycle you will need to alter it's database structure to support additional features. You must ensure users who have installed your app prior to this can safely upgrade their local databases without the loss of any locally held data.

To facilitate a database upgrade, increment the version number that you pass to your `SQLiteAssetHelper` constructor:

     private static final int DATABASE_VERSION = 2;

Update the initial SQLite database in the project's `assets/databases` directory with the changes and create a text file containing all required SQL commands to upgrade the database from its previous version to it's current version and place it in the same folder. The required naming convention for this upgrade file is as follows:

    assets/databases/<database_name>_upgrade_<from_version>-<to_version>.sql

For example, [assets/databases/northwind.db_upgrade_1-2.sql](https://github.com/jgilfelt/android-sqlite-asset-helper/blob/v2/samples/database-v2-upgrade/assets/databases/northwind.db_upgrade_1-2.sql) upgrades the database named "northwind" from version 1 to 2. You can include multiple upgrade files to upgrade between any two given versions.

If there are no files to form an upgrade path from a previously installed version to the current one, the class will throw a `SQLiteAssetHelperException`.

The [samples:database-v2-upgrade](https://github.com/jgilfelt/android-sqlite-asset-helper/tree/v2/samples/database-v2-upgrade) project demonstrates a simple upgrade to the Northwind database which adds a FullName column to the Employee table.

### Generating upgrade scripts

You can use 3rd party tools to automatically generate the SQL required to modify a database from one schema version to another. One such application is [SQLite Compare Utility](http://www.codeproject.com/KB/database/SQLiteCompareUtility.aspx) for Windows.

### Forcing upgrades

You can force users onto the latest version of the SQLite database (overwriting the local database with the one in the assets) by calling the `setForcedUpgradeVersion(int version)` method in your constructor. The argument passed is the version number below which the upgrade will be forced. Note that this will forcibly overwriting any existing local database and all data within it.

Credits
-------

####Author:

  * [Jeff Gilfelt](https://github.com/jgilfelt)

#### Contributors:

  * [Alexandros Schillings](https://github.com/alt236)
  * [Cyril Mottier](https://github.com/cyrilmottier)
  * [Jon Adams](https://github.com/jon-adams)
  * [Kevin](https://github.com/kevinchai)

License
-------

    Copyright (C) 2011 readyState Software Ltd
    Copyright (C) 2007 The Android Open Source Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

 [1]: http://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.readystatesoftware.sqliteasset&a=sqliteassethelper&v=LATEST&c=jar
