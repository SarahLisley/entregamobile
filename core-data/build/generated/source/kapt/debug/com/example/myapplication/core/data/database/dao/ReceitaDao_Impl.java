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
import com.example.myapplication.core.data.database.converters.Converters;
import com.example.myapplication.core.data.database.entity.ReceitaEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ReceitaDao_Impl implements ReceitaDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ReceitaEntity> __insertionAdapterOfReceitaEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<ReceitaEntity> __updateAdapterOfReceitaEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateCurtidas;

  private final SharedSQLiteStatement __preparedStmtOfUpdateFavoritos;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsSynced;

  private final SharedSQLiteStatement __preparedStmtOfDeleteReceitaById;

  public ReceitaDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReceitaEntity = new EntityInsertionAdapter<ReceitaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `receitas` (`id`,`nome`,`descricaoCurta`,`imagemUrl`,`ingredientes`,`modoPreparo`,`tempoPreparo`,`porcoes`,`userId`,`userEmail`,`curtidas`,`favoritos`,`isSynced`,`lastModified`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReceitaEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getNome() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getNome());
        }
        if (entity.getDescricaoCurta() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDescricaoCurta());
        }
        if (entity.getImagemUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getImagemUrl());
        }
        final String _tmp = __converters.fromList(entity.getIngredientes());
        if (_tmp == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp);
        }
        final String _tmp_1 = __converters.fromList(entity.getModoPreparo());
        if (_tmp_1 == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp_1);
        }
        if (entity.getTempoPreparo() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getTempoPreparo());
        }
        statement.bindLong(8, entity.getPorcoes());
        if (entity.getUserId() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getUserId());
        }
        if (entity.getUserEmail() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getUserEmail());
        }
        final String _tmp_2 = __converters.fromList(entity.getCurtidas());
        if (_tmp_2 == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, _tmp_2);
        }
        final String _tmp_3 = __converters.fromList(entity.getFavoritos());
        if (_tmp_3 == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, _tmp_3);
        }
        final int _tmp_4 = entity.isSynced() ? 1 : 0;
        statement.bindLong(13, _tmp_4);
        statement.bindLong(14, entity.getLastModified());
      }
    };
    this.__updateAdapterOfReceitaEntity = new EntityDeletionOrUpdateAdapter<ReceitaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `receitas` SET `id` = ?,`nome` = ?,`descricaoCurta` = ?,`imagemUrl` = ?,`ingredientes` = ?,`modoPreparo` = ?,`tempoPreparo` = ?,`porcoes` = ?,`userId` = ?,`userEmail` = ?,`curtidas` = ?,`favoritos` = ?,`isSynced` = ?,`lastModified` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReceitaEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getNome() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getNome());
        }
        if (entity.getDescricaoCurta() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDescricaoCurta());
        }
        if (entity.getImagemUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getImagemUrl());
        }
        final String _tmp = __converters.fromList(entity.getIngredientes());
        if (_tmp == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp);
        }
        final String _tmp_1 = __converters.fromList(entity.getModoPreparo());
        if (_tmp_1 == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp_1);
        }
        if (entity.getTempoPreparo() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getTempoPreparo());
        }
        statement.bindLong(8, entity.getPorcoes());
        if (entity.getUserId() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getUserId());
        }
        if (entity.getUserEmail() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getUserEmail());
        }
        final String _tmp_2 = __converters.fromList(entity.getCurtidas());
        if (_tmp_2 == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, _tmp_2);
        }
        final String _tmp_3 = __converters.fromList(entity.getFavoritos());
        if (_tmp_3 == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, _tmp_3);
        }
        final int _tmp_4 = entity.isSynced() ? 1 : 0;
        statement.bindLong(13, _tmp_4);
        statement.bindLong(14, entity.getLastModified());
        if (entity.getId() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getId());
        }
      }
    };
    this.__preparedStmtOfUpdateCurtidas = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE receitas SET curtidas = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateFavoritos = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE receitas SET favoritos = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsSynced = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE receitas SET isSynced = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteReceitaById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM receitas WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertReceita(final ReceitaEntity receita,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReceitaEntity.insert(receita);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertReceitas(final List<ReceitaEntity> receitas,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReceitaEntity.insert(receitas);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateReceita(final ReceitaEntity receita,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfReceitaEntity.handle(receita);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateCurtidas(final String id, final List<String> curtidas,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateCurtidas.acquire();
        int _argIndex = 1;
        final String _tmp = __converters.fromList(curtidas);
        if (_tmp == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, _tmp);
        }
        _argIndex = 2;
        if (id == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, id);
        }
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
          __preparedStmtOfUpdateCurtidas.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateFavoritos(final String id, final List<String> favoritos,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateFavoritos.acquire();
        int _argIndex = 1;
        final String _tmp = __converters.fromList(favoritos);
        if (_tmp == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, _tmp);
        }
        _argIndex = 2;
        if (id == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, id);
        }
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
          __preparedStmtOfUpdateFavoritos.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markAsSynced(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsSynced.acquire();
        int _argIndex = 1;
        if (id == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, id);
        }
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
          __preparedStmtOfMarkAsSynced.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteReceitaById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteReceitaById.acquire();
        int _argIndex = 1;
        if (id == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, id);
        }
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
          __preparedStmtOfDeleteReceitaById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ReceitaEntity>> getAllReceitas() {
    final String _sql = "SELECT * FROM receitas ORDER BY lastModified DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"receitas"}, new Callable<List<ReceitaEntity>>() {
      @Override
      @NonNull
      public List<ReceitaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNome = CursorUtil.getColumnIndexOrThrow(_cursor, "nome");
          final int _cursorIndexOfDescricaoCurta = CursorUtil.getColumnIndexOrThrow(_cursor, "descricaoCurta");
          final int _cursorIndexOfImagemUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imagemUrl");
          final int _cursorIndexOfIngredientes = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredientes");
          final int _cursorIndexOfModoPreparo = CursorUtil.getColumnIndexOrThrow(_cursor, "modoPreparo");
          final int _cursorIndexOfTempoPreparo = CursorUtil.getColumnIndexOrThrow(_cursor, "tempoPreparo");
          final int _cursorIndexOfPorcoes = CursorUtil.getColumnIndexOrThrow(_cursor, "porcoes");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfUserEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "userEmail");
          final int _cursorIndexOfCurtidas = CursorUtil.getColumnIndexOrThrow(_cursor, "curtidas");
          final int _cursorIndexOfFavoritos = CursorUtil.getColumnIndexOrThrow(_cursor, "favoritos");
          final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final List<ReceitaEntity> _result = new ArrayList<ReceitaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReceitaEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpNome;
            if (_cursor.isNull(_cursorIndexOfNome)) {
              _tmpNome = null;
            } else {
              _tmpNome = _cursor.getString(_cursorIndexOfNome);
            }
            final String _tmpDescricaoCurta;
            if (_cursor.isNull(_cursorIndexOfDescricaoCurta)) {
              _tmpDescricaoCurta = null;
            } else {
              _tmpDescricaoCurta = _cursor.getString(_cursorIndexOfDescricaoCurta);
            }
            final String _tmpImagemUrl;
            if (_cursor.isNull(_cursorIndexOfImagemUrl)) {
              _tmpImagemUrl = null;
            } else {
              _tmpImagemUrl = _cursor.getString(_cursorIndexOfImagemUrl);
            }
            final List<String> _tmpIngredientes;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfIngredientes)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfIngredientes);
            }
            _tmpIngredientes = __converters.fromString(_tmp);
            final List<String> _tmpModoPreparo;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfModoPreparo)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfModoPreparo);
            }
            _tmpModoPreparo = __converters.fromString(_tmp_1);
            final String _tmpTempoPreparo;
            if (_cursor.isNull(_cursorIndexOfTempoPreparo)) {
              _tmpTempoPreparo = null;
            } else {
              _tmpTempoPreparo = _cursor.getString(_cursorIndexOfTempoPreparo);
            }
            final int _tmpPorcoes;
            _tmpPorcoes = _cursor.getInt(_cursorIndexOfPorcoes);
            final String _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            }
            final String _tmpUserEmail;
            if (_cursor.isNull(_cursorIndexOfUserEmail)) {
              _tmpUserEmail = null;
            } else {
              _tmpUserEmail = _cursor.getString(_cursorIndexOfUserEmail);
            }
            final List<String> _tmpCurtidas;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfCurtidas)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfCurtidas);
            }
            _tmpCurtidas = __converters.fromString(_tmp_2);
            final List<String> _tmpFavoritos;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfFavoritos)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfFavoritos);
            }
            _tmpFavoritos = __converters.fromString(_tmp_3);
            final boolean _tmpIsSynced;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsSynced);
            _tmpIsSynced = _tmp_4 != 0;
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            _item = new ReceitaEntity(_tmpId,_tmpNome,_tmpDescricaoCurta,_tmpImagemUrl,_tmpIngredientes,_tmpModoPreparo,_tmpTempoPreparo,_tmpPorcoes,_tmpUserId,_tmpUserEmail,_tmpCurtidas,_tmpFavoritos,_tmpIsSynced,_tmpLastModified);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getReceitaById(final String id,
      final Continuation<? super ReceitaEntity> $completion) {
    final String _sql = "SELECT * FROM receitas WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (id == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, id);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ReceitaEntity>() {
      @Override
      @Nullable
      public ReceitaEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNome = CursorUtil.getColumnIndexOrThrow(_cursor, "nome");
          final int _cursorIndexOfDescricaoCurta = CursorUtil.getColumnIndexOrThrow(_cursor, "descricaoCurta");
          final int _cursorIndexOfImagemUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imagemUrl");
          final int _cursorIndexOfIngredientes = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredientes");
          final int _cursorIndexOfModoPreparo = CursorUtil.getColumnIndexOrThrow(_cursor, "modoPreparo");
          final int _cursorIndexOfTempoPreparo = CursorUtil.getColumnIndexOrThrow(_cursor, "tempoPreparo");
          final int _cursorIndexOfPorcoes = CursorUtil.getColumnIndexOrThrow(_cursor, "porcoes");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfUserEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "userEmail");
          final int _cursorIndexOfCurtidas = CursorUtil.getColumnIndexOrThrow(_cursor, "curtidas");
          final int _cursorIndexOfFavoritos = CursorUtil.getColumnIndexOrThrow(_cursor, "favoritos");
          final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final ReceitaEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpNome;
            if (_cursor.isNull(_cursorIndexOfNome)) {
              _tmpNome = null;
            } else {
              _tmpNome = _cursor.getString(_cursorIndexOfNome);
            }
            final String _tmpDescricaoCurta;
            if (_cursor.isNull(_cursorIndexOfDescricaoCurta)) {
              _tmpDescricaoCurta = null;
            } else {
              _tmpDescricaoCurta = _cursor.getString(_cursorIndexOfDescricaoCurta);
            }
            final String _tmpImagemUrl;
            if (_cursor.isNull(_cursorIndexOfImagemUrl)) {
              _tmpImagemUrl = null;
            } else {
              _tmpImagemUrl = _cursor.getString(_cursorIndexOfImagemUrl);
            }
            final List<String> _tmpIngredientes;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfIngredientes)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfIngredientes);
            }
            _tmpIngredientes = __converters.fromString(_tmp);
            final List<String> _tmpModoPreparo;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfModoPreparo)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfModoPreparo);
            }
            _tmpModoPreparo = __converters.fromString(_tmp_1);
            final String _tmpTempoPreparo;
            if (_cursor.isNull(_cursorIndexOfTempoPreparo)) {
              _tmpTempoPreparo = null;
            } else {
              _tmpTempoPreparo = _cursor.getString(_cursorIndexOfTempoPreparo);
            }
            final int _tmpPorcoes;
            _tmpPorcoes = _cursor.getInt(_cursorIndexOfPorcoes);
            final String _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            }
            final String _tmpUserEmail;
            if (_cursor.isNull(_cursorIndexOfUserEmail)) {
              _tmpUserEmail = null;
            } else {
              _tmpUserEmail = _cursor.getString(_cursorIndexOfUserEmail);
            }
            final List<String> _tmpCurtidas;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfCurtidas)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfCurtidas);
            }
            _tmpCurtidas = __converters.fromString(_tmp_2);
            final List<String> _tmpFavoritos;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfFavoritos)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfFavoritos);
            }
            _tmpFavoritos = __converters.fromString(_tmp_3);
            final boolean _tmpIsSynced;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsSynced);
            _tmpIsSynced = _tmp_4 != 0;
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            _result = new ReceitaEntity(_tmpId,_tmpNome,_tmpDescricaoCurta,_tmpImagemUrl,_tmpIngredientes,_tmpModoPreparo,_tmpTempoPreparo,_tmpPorcoes,_tmpUserId,_tmpUserEmail,_tmpCurtidas,_tmpFavoritos,_tmpIsSynced,_tmpLastModified);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getUnsyncedReceitas(final Continuation<? super List<ReceitaEntity>> $completion) {
    final String _sql = "SELECT * FROM receitas WHERE isSynced = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ReceitaEntity>>() {
      @Override
      @NonNull
      public List<ReceitaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNome = CursorUtil.getColumnIndexOrThrow(_cursor, "nome");
          final int _cursorIndexOfDescricaoCurta = CursorUtil.getColumnIndexOrThrow(_cursor, "descricaoCurta");
          final int _cursorIndexOfImagemUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imagemUrl");
          final int _cursorIndexOfIngredientes = CursorUtil.getColumnIndexOrThrow(_cursor, "ingredientes");
          final int _cursorIndexOfModoPreparo = CursorUtil.getColumnIndexOrThrow(_cursor, "modoPreparo");
          final int _cursorIndexOfTempoPreparo = CursorUtil.getColumnIndexOrThrow(_cursor, "tempoPreparo");
          final int _cursorIndexOfPorcoes = CursorUtil.getColumnIndexOrThrow(_cursor, "porcoes");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfUserEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "userEmail");
          final int _cursorIndexOfCurtidas = CursorUtil.getColumnIndexOrThrow(_cursor, "curtidas");
          final int _cursorIndexOfFavoritos = CursorUtil.getColumnIndexOrThrow(_cursor, "favoritos");
          final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final List<ReceitaEntity> _result = new ArrayList<ReceitaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReceitaEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpNome;
            if (_cursor.isNull(_cursorIndexOfNome)) {
              _tmpNome = null;
            } else {
              _tmpNome = _cursor.getString(_cursorIndexOfNome);
            }
            final String _tmpDescricaoCurta;
            if (_cursor.isNull(_cursorIndexOfDescricaoCurta)) {
              _tmpDescricaoCurta = null;
            } else {
              _tmpDescricaoCurta = _cursor.getString(_cursorIndexOfDescricaoCurta);
            }
            final String _tmpImagemUrl;
            if (_cursor.isNull(_cursorIndexOfImagemUrl)) {
              _tmpImagemUrl = null;
            } else {
              _tmpImagemUrl = _cursor.getString(_cursorIndexOfImagemUrl);
            }
            final List<String> _tmpIngredientes;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfIngredientes)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfIngredientes);
            }
            _tmpIngredientes = __converters.fromString(_tmp);
            final List<String> _tmpModoPreparo;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfModoPreparo)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfModoPreparo);
            }
            _tmpModoPreparo = __converters.fromString(_tmp_1);
            final String _tmpTempoPreparo;
            if (_cursor.isNull(_cursorIndexOfTempoPreparo)) {
              _tmpTempoPreparo = null;
            } else {
              _tmpTempoPreparo = _cursor.getString(_cursorIndexOfTempoPreparo);
            }
            final int _tmpPorcoes;
            _tmpPorcoes = _cursor.getInt(_cursorIndexOfPorcoes);
            final String _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            }
            final String _tmpUserEmail;
            if (_cursor.isNull(_cursorIndexOfUserEmail)) {
              _tmpUserEmail = null;
            } else {
              _tmpUserEmail = _cursor.getString(_cursorIndexOfUserEmail);
            }
            final List<String> _tmpCurtidas;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfCurtidas)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfCurtidas);
            }
            _tmpCurtidas = __converters.fromString(_tmp_2);
            final List<String> _tmpFavoritos;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfFavoritos)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfFavoritos);
            }
            _tmpFavoritos = __converters.fromString(_tmp_3);
            final boolean _tmpIsSynced;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsSynced);
            _tmpIsSynced = _tmp_4 != 0;
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            _item = new ReceitaEntity(_tmpId,_tmpNome,_tmpDescricaoCurta,_tmpImagemUrl,_tmpIngredientes,_tmpModoPreparo,_tmpTempoPreparo,_tmpPorcoes,_tmpUserId,_tmpUserEmail,_tmpCurtidas,_tmpFavoritos,_tmpIsSynced,_tmpLastModified);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
