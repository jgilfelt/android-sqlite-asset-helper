Android SQLiteAssetHelper
=========================

An Android helper class to manage database creation and version management using an application's raw asset files.

This class provides developers with a simple way to ship their Android app with an existing SQLite database (which may be pre-populated with data) and to manage its initial creation and any upgrades required with subsequent version releases.

It is implemented as an extension to `SQLiteOpenHelper`, providing an efficient way for `ContentProvider` implementations to defer opening and upgrading the database until first use.

Rather than implementing the `onCreate()` and `onUpgrade()` methods to execute a bunch of SQL statements, developers simply include appropriately named file assets in their project's `assets` directory. These will include the initial SQLite database file for creation and optionally any SQL upgrade scripts.

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

If you are using the old build system, download the latest library [JAR][1] and put it in your project's `libs` folder.

Usage
-----

SQLiteAssetHelper is intended as a drop in alternative for the framework's [SQLiteOpenHelper](https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html). Please familiarize yourself with the behaviour and lifecycle of that class.

Extend `SQLiteAssetHelper` as you would normally do `SQLiteOpenHelper`, providing the constructor with a database name and version number:

```java
public class MyDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "northwind.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabase(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
```

SQLiteAssetHelper relies upon asset file and folder naming conventions. Your `assets` folder will either be under your project root, or under `src/main` if you are using the default gradle project structure. At minimum, you must provide the following:

* A `databases` folder inside `assets`
* A SQLite database inside the `databases` folder whose file name matches the database name you provide in code (including the file extension, if any)

For the example above, the project would contain the following:

    assets/databases/northwind.db

Earlier versions of this library required the database asset to be compressed within a ZIP archive. This is no longer a requirement, but is still supported. Applications still targeting Gingerbread (API 10) or lower should continue to provide a compressed archive to ensure large database files are not corrupted during the packaging process. The more Linux friendly GZIP format is also supported. The naming conventions using the above example are as follows:

* ZIP: `assets/databases/northwind.db.zip` (a single SQLite database file must be the only file within the archive)
* GZIP: `assets/databases/northwind.db.gz`

The database will be extracted from the assets and copied into place within your application's private data directory. If you prefer to store the database file somewhere else (such as external storage) you can use the alternate constructor to specify a storage path. You must ensure that this path is available and writable whenever your application needs to access the database.

```java
super(context, DATABASE_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);
```

The database is made available for use the first time either `getReadableDatabase()` or `getWritableDatabase()` is called.

The class will throw a `SQLiteAssetHelperException` if you do not provide the appropriately named file.

The SQLiteOpenHelper methods `onConfigure`, `onCreate` and `onDowngrade` are not supported by this implementation and have been declared `final`.

The [samples:database-v1](https://github.com/jgilfelt/android-sqlite-asset-helper/tree/master/samples/database-v1) project demonstrates a simple database creation and usage example using the classic Northwind database.

Database Upgrades
-----------------

At a certain point in your application's lifecycle you will need to alter it's database structure to support additional features. You must ensure users who have installed your app prior to this can safely upgrade their local databases without the loss of any locally held data.

To facilitate a database upgrade, increment the version number that you pass to your `SQLiteAssetHelper` constructor:

```java
private static final int DATABASE_VERSION = 2;
```

Update the initial SQLite database in the project's `assets/databases` directory with the changes and create a text file containing all required SQL commands to upgrade the database from its previous version to it's current version and place it in the same folder. The required naming convention for this upgrade file is as follows:

    assets/databases/<database_name>_upgrade_<from_version>-<to_version>.sql

For example, [northwind.db_upgrade_1-2.sql](https://github.com/jgilfelt/android-sqlite-asset-helper/blob/master/samples/database-v2-upgrade/src/main/assets/databases/northwind.db_upgrade_1-2.sql) upgrades the database named "northwind.db" from version 1 to 2. You can include multiple upgrade files to upgrade between any two given versions.

If there are no files to form an upgrade path from a previously installed version to the current one, the class will throw a `SQLiteAssetHelperException`.

The [samples:database-v2-upgrade](https://github.com/jgilfelt/android-sqlite-asset-helper/tree/master/samples/database-v2-upgrade) project demonstrates a simple upgrade to the Northwind database which adds a FullName column to the Employee table.

### Generating upgrade scripts

You can use 3rd party tools to automatically generate the SQL required to modify a database from one schema version to another. One such application is [SQLite Compare Utility](http://www.codeproject.com/KB/database/SQLiteCompareUtility.aspx) for Windows.

### Upgrades via overwrite

If you have a read-only database or do not care about user data loss, you can force users onto the latest version of the SQLite database each time the version number is incremented (overwriting the local database with the one in the assets) by calling the `setForcedUpgrade()` method in your `SQLiteAsstHelper` subclass constructor. 

You can additionally pass an argument that is the version number below which the upgrade will be forced.

Note that this will overwrite an existing local database and all data within it.

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

 [1]: https://search.maven.org/remote_content?g=com.readystatesoftware.sqliteasset&a=sqliteassethelper&v=LATEST
