package com.onlineradiofm.trancemusicradio.db;

import androidx.annotation.NonNull;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenDelegate;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.SQLite;
import androidx.sqlite.SQLiteConnection;
import com.onlineradiofm.trancemusicradio.db.dao.RMRadioDAO;
import com.onlineradiofm.trancemusicradio.db.dao.RMRadioDAO_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile RMRadioDAO _rMRadioDAO;

  @Override
  @NonNull
  protected RoomOpenDelegate createOpenDelegate() {
    final RoomOpenDelegate _openDelegate = new RoomOpenDelegate(1, "54cefde3f49254c591765911d87a89cc", "c8a40b7933d36a3554eb02d9cc188741") {
      @Override
      public void createAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `radios` (`link` TEXT, `is_mp3` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        SQLite.execSQL(connection, "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '54cefde3f49254c591765911d87a89cc')");
      }

      @Override
      public void dropAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `radios`");
      }

      @Override
      public void onCreate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      public void onOpen(@NonNull final SQLiteConnection connection) {
        internalInitInvalidationTracker(connection);
      }

      @Override
      public void onPreMigrate(@NonNull final SQLiteConnection connection) {
        DBUtil.dropFtsSyncTriggers(connection);
      }

      @Override
      public void onPostMigrate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      @NonNull
      public RoomOpenDelegate.ValidationResult onValidateSchema(
          @NonNull final SQLiteConnection connection) {
        final Map<String, TableInfo.Column> _columnsRadios = new HashMap<String, TableInfo.Column>(4);
        _columnsRadios.put("link", new TableInfo.Column("link", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRadios.put("is_mp3", new TableInfo.Column("is_mp3", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRadios.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRadios.put("name", new TableInfo.Column("name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysRadios = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesRadios = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRadios = new TableInfo("radios", _columnsRadios, _foreignKeysRadios, _indicesRadios);
        final TableInfo _existingRadios = TableInfo.read(connection, "radios");
        if (!_infoRadios.equals(_existingRadios)) {
          return new RoomOpenDelegate.ValidationResult(false, "radios(com.onlineradiofm.trancemusicradio.db.entity.RMRadioEntity).\n"
                  + " Expected:\n" + _infoRadios + "\n"
                  + " Found:\n" + _existingRadios);
        }
        return new RoomOpenDelegate.ValidationResult(true, null);
      }
    };
    return _openDelegate;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final Map<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final Map<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "radios");
  }

  @Override
  public void clearAllTables() {
    super.performClear(false, "radios");
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final Map<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(RMRadioDAO.class, RMRadioDAO_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final Set<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public RMRadioDAO radioDAO() {
    if (_rMRadioDAO != null) {
      return _rMRadioDAO;
    } else {
      synchronized(this) {
        if(_rMRadioDAO == null) {
          _rMRadioDAO = new RMRadioDAO_Impl(this);
        }
        return _rMRadioDAO;
      }
    }
  }
}
