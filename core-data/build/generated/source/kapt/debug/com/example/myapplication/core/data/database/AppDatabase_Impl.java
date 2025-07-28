package com.example.myapplication.core.data.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.example.myapplication.core.data.database.dao.NutritionCacheDao;
import com.example.myapplication.core.data.database.dao.NutritionCacheDao_Impl;
import com.example.myapplication.core.data.database.dao.NutritionDataDao;
import com.example.myapplication.core.data.database.dao.NutritionDataDao_Impl;
import com.example.myapplication.core.data.database.dao.ReceitaDao;
import com.example.myapplication.core.data.database.dao.ReceitaDao_Impl;
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
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile ReceitaDao _receitaDao;

  private volatile NutritionCacheDao _nutritionCacheDao;

  private volatile NutritionDataDao _nutritionDataDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `receitas` (`id` TEXT NOT NULL, `nome` TEXT NOT NULL, `descricaoCurta` TEXT NOT NULL, `imagemUrl` TEXT NOT NULL, `ingredientes` TEXT NOT NULL, `modoPreparo` TEXT NOT NULL, `tempoPreparo` TEXT NOT NULL, `porcoes` INTEGER NOT NULL, `userId` TEXT NOT NULL, `userEmail` TEXT, `curtidas` TEXT NOT NULL, `favoritos` TEXT NOT NULL, `tags` TEXT NOT NULL, `isSynced` INTEGER NOT NULL, `lastModified` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `nutrition_cache` (`recipeTitle` TEXT NOT NULL, `calories` REAL NOT NULL, `protein` REAL NOT NULL, `fat` REAL NOT NULL, `carbohydrates` REAL NOT NULL, `fiber` REAL, `sugar` REAL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`recipeTitle`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `nutrition_data` (`id` TEXT NOT NULL, `receitaId` TEXT NOT NULL, `calories` REAL NOT NULL, `protein` REAL NOT NULL, `fat` REAL NOT NULL, `carbohydrates` REAL NOT NULL, `fiber` REAL, `sugar` REAL, `isSynced` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '073d03a8d07741e79c28051abe4c224c')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `receitas`");
        db.execSQL("DROP TABLE IF EXISTS `nutrition_cache`");
        db.execSQL("DROP TABLE IF EXISTS `nutrition_data`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsReceitas = new HashMap<String, TableInfo.Column>(15);
        _columnsReceitas.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceitas.put("nome", new TableInfo.Column("nome", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceitas.put("descricaoCurta", new TableInfo.Column("descricaoCurta", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceitas.put("imagemUrl", new TableInfo.Column("imagemUrl", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceitas.put("ingredientes", new TableInfo.Column("ingredientes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceitas.put("modoPreparo", new TableInfo.Column("modoPreparo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceitas.put("tempoPreparo", new TableInfo.Column("tempoPreparo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceitas.put("porcoes", new TableInfo.Column("porcoes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceitas.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceitas.put("userEmail", new TableInfo.Column("userEmail", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceitas.put("curtidas", new TableInfo.Column("curtidas", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceitas.put("favoritos", new TableInfo.Column("favoritos", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceitas.put("tags", new TableInfo.Column("tags", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceitas.put("isSynced", new TableInfo.Column("isSynced", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceitas.put("lastModified", new TableInfo.Column("lastModified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReceitas = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReceitas = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoReceitas = new TableInfo("receitas", _columnsReceitas, _foreignKeysReceitas, _indicesReceitas);
        final TableInfo _existingReceitas = TableInfo.read(db, "receitas");
        if (!_infoReceitas.equals(_existingReceitas)) {
          return new RoomOpenHelper.ValidationResult(false, "receitas(com.example.myapplication.core.data.database.entity.ReceitaEntity).\n"
                  + " Expected:\n" + _infoReceitas + "\n"
                  + " Found:\n" + _existingReceitas);
        }
        final HashMap<String, TableInfo.Column> _columnsNutritionCache = new HashMap<String, TableInfo.Column>(8);
        _columnsNutritionCache.put("recipeTitle", new TableInfo.Column("recipeTitle", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNutritionCache.put("calories", new TableInfo.Column("calories", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNutritionCache.put("protein", new TableInfo.Column("protein", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNutritionCache.put("fat", new TableInfo.Column("fat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNutritionCache.put("carbohydrates", new TableInfo.Column("carbohydrates", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNutritionCache.put("fiber", new TableInfo.Column("fiber", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNutritionCache.put("sugar", new TableInfo.Column("sugar", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNutritionCache.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNutritionCache = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNutritionCache = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNutritionCache = new TableInfo("nutrition_cache", _columnsNutritionCache, _foreignKeysNutritionCache, _indicesNutritionCache);
        final TableInfo _existingNutritionCache = TableInfo.read(db, "nutrition_cache");
        if (!_infoNutritionCache.equals(_existingNutritionCache)) {
          return new RoomOpenHelper.ValidationResult(false, "nutrition_cache(com.example.myapplication.core.data.database.entity.NutritionCacheEntity).\n"
                  + " Expected:\n" + _infoNutritionCache + "\n"
                  + " Found:\n" + _existingNutritionCache);
        }
        final HashMap<String, TableInfo.Column> _columnsNutritionData = new HashMap<String, TableInfo.Column>(10);
        _columnsNutritionData.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNutritionData.put("receitaId", new TableInfo.Column("receitaId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNutritionData.put("calories", new TableInfo.Column("calories", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNutritionData.put("protein", new TableInfo.Column("protein", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNutritionData.put("fat", new TableInfo.Column("fat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNutritionData.put("carbohydrates", new TableInfo.Column("carbohydrates", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNutritionData.put("fiber", new TableInfo.Column("fiber", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNutritionData.put("sugar", new TableInfo.Column("sugar", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNutritionData.put("isSynced", new TableInfo.Column("isSynced", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNutritionData.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNutritionData = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNutritionData = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNutritionData = new TableInfo("nutrition_data", _columnsNutritionData, _foreignKeysNutritionData, _indicesNutritionData);
        final TableInfo _existingNutritionData = TableInfo.read(db, "nutrition_data");
        if (!_infoNutritionData.equals(_existingNutritionData)) {
          return new RoomOpenHelper.ValidationResult(false, "nutrition_data(com.example.myapplication.core.data.database.entity.NutritionDataEntity).\n"
                  + " Expected:\n" + _infoNutritionData + "\n"
                  + " Found:\n" + _existingNutritionData);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "073d03a8d07741e79c28051abe4c224c", "930bd3b2aaba9e7b105c0976abba0ad9");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "receitas","nutrition_cache","nutrition_data");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `receitas`");
      _db.execSQL("DELETE FROM `nutrition_cache`");
      _db.execSQL("DELETE FROM `nutrition_data`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ReceitaDao.class, ReceitaDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(NutritionCacheDao.class, NutritionCacheDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(NutritionDataDao.class, NutritionDataDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
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
  public ReceitaDao receitaDao() {
    if (_receitaDao != null) {
      return _receitaDao;
    } else {
      synchronized(this) {
        if(_receitaDao == null) {
          _receitaDao = new ReceitaDao_Impl(this);
        }
        return _receitaDao;
      }
    }
  }

  @Override
  public NutritionCacheDao nutritionCacheDao() {
    if (_nutritionCacheDao != null) {
      return _nutritionCacheDao;
    } else {
      synchronized(this) {
        if(_nutritionCacheDao == null) {
          _nutritionCacheDao = new NutritionCacheDao_Impl(this);
        }
        return _nutritionCacheDao;
      }
    }
  }

  @Override
  public NutritionDataDao nutritionDataDao() {
    if (_nutritionDataDao != null) {
      return _nutritionDataDao;
    } else {
      synchronized(this) {
        if(_nutritionDataDao == null) {
          _nutritionDataDao = new NutritionDataDao_Impl(this);
        }
        return _nutritionDataDao;
      }
    }
  }
}
