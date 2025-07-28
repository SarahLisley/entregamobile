package com.example.myapplication.core.data.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.myapplication.core.data.database.entity.NutritionCacheEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class NutritionCacheDao_Impl implements NutritionCacheDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<NutritionCacheEntity> __insertionAdapterOfNutritionCacheEntity;

  private final EntityDeletionOrUpdateAdapter<NutritionCacheEntity> __deletionAdapterOfNutritionCacheEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldCache;

  public NutritionCacheDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfNutritionCacheEntity = new EntityInsertionAdapter<NutritionCacheEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `nutrition_cache` (`recipeTitle`,`calories`,`protein`,`fat`,`carbohydrates`,`fiber`,`sugar`,`timestamp`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final NutritionCacheEntity entity) {
        if (entity.getRecipeTitle() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getRecipeTitle());
        }
        statement.bindDouble(2, entity.getCalories());
        statement.bindDouble(3, entity.getProtein());
        statement.bindDouble(4, entity.getFat());
        statement.bindDouble(5, entity.getCarbohydrates());
        if (entity.getFiber() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getFiber());
        }
        if (entity.getSugar() == null) {
          statement.bindNull(7);
        } else {
          statement.bindDouble(7, entity.getSugar());
        }
        statement.bindLong(8, entity.getTimestamp());
      }
    };
    this.__deletionAdapterOfNutritionCacheEntity = new EntityDeletionOrUpdateAdapter<NutritionCacheEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `nutrition_cache` WHERE `recipeTitle` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final NutritionCacheEntity entity) {
        if (entity.getRecipeTitle() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getRecipeTitle());
        }
      }
    };
    this.__preparedStmtOfDeleteOldCache = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM nutrition_cache WHERE timestamp < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertNutrition(final NutritionCacheEntity nutrition,
      final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfNutritionCacheEntity.insert(nutrition);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object deleteNutrition(final NutritionCacheEntity nutrition,
      final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfNutritionCacheEntity.handle(nutrition);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object deleteOldCache(final long timestamp, final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldCache.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteOldCache.release(_stmt);
        }
      }
    }, arg1);
  }

  @Override
  public Object getNutritionByTitle(final String recipeTitle,
      final Continuation<? super NutritionCacheEntity> arg1) {
    final String _sql = "SELECT * FROM nutrition_cache WHERE recipeTitle = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (recipeTitle == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, recipeTitle);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<NutritionCacheEntity>() {
      @Override
      @Nullable
      public NutritionCacheEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfRecipeTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "recipeTitle");
          final int _cursorIndexOfCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "calories");
          final int _cursorIndexOfProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "protein");
          final int _cursorIndexOfFat = CursorUtil.getColumnIndexOrThrow(_cursor, "fat");
          final int _cursorIndexOfCarbohydrates = CursorUtil.getColumnIndexOrThrow(_cursor, "carbohydrates");
          final int _cursorIndexOfFiber = CursorUtil.getColumnIndexOrThrow(_cursor, "fiber");
          final int _cursorIndexOfSugar = CursorUtil.getColumnIndexOrThrow(_cursor, "sugar");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final NutritionCacheEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpRecipeTitle;
            if (_cursor.isNull(_cursorIndexOfRecipeTitle)) {
              _tmpRecipeTitle = null;
            } else {
              _tmpRecipeTitle = _cursor.getString(_cursorIndexOfRecipeTitle);
            }
            final double _tmpCalories;
            _tmpCalories = _cursor.getDouble(_cursorIndexOfCalories);
            final double _tmpProtein;
            _tmpProtein = _cursor.getDouble(_cursorIndexOfProtein);
            final double _tmpFat;
            _tmpFat = _cursor.getDouble(_cursorIndexOfFat);
            final double _tmpCarbohydrates;
            _tmpCarbohydrates = _cursor.getDouble(_cursorIndexOfCarbohydrates);
            final Double _tmpFiber;
            if (_cursor.isNull(_cursorIndexOfFiber)) {
              _tmpFiber = null;
            } else {
              _tmpFiber = _cursor.getDouble(_cursorIndexOfFiber);
            }
            final Double _tmpSugar;
            if (_cursor.isNull(_cursorIndexOfSugar)) {
              _tmpSugar = null;
            } else {
              _tmpSugar = _cursor.getDouble(_cursorIndexOfSugar);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _result = new NutritionCacheEntity(_tmpRecipeTitle,_tmpCalories,_tmpProtein,_tmpFat,_tmpCarbohydrates,_tmpFiber,_tmpSugar,_tmpTimestamp);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg1);
  }

  @Override
  public Object getCacheSize(final Continuation<? super Integer> arg0) {
    final String _sql = "SELECT COUNT(*) FROM nutrition_cache";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg0);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
