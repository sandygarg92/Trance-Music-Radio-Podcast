package com.onlineradiofm.trancemusicradio.db.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.onlineradiofm.trancemusicradio.db.entity.RMRadioEntity;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class RMRadioDAO_Impl implements RMRadioDAO {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<RMRadioEntity> __insertAdapterOfRMRadioEntity;

  private final EntityDeleteOrUpdateAdapter<RMRadioEntity> __updateAdapterOfRMRadioEntity;

  public RMRadioDAO_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfRMRadioEntity = new EntityInsertAdapter<RMRadioEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `radios` (`link`,`is_mp3`,`id`,`name`) VALUES (?,?,nullif(?, 0),?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final RMRadioEntity entity) {
        if (entity.linkTrack == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.linkTrack);
        }
        statement.bindLong(2, entity.isMp3);
        statement.bindLong(3, entity.id);
        if (entity.name == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.name);
        }
      }
    };
    this.__updateAdapterOfRMRadioEntity = new EntityDeleteOrUpdateAdapter<RMRadioEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `radios` SET `link` = ?,`is_mp3` = ?,`id` = ?,`name` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final RMRadioEntity entity) {
        if (entity.linkTrack == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.linkTrack);
        }
        statement.bindLong(2, entity.isMp3);
        statement.bindLong(3, entity.id);
        if (entity.name == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.name);
        }
        statement.bindLong(5, entity.id);
      }
    };
  }

  @Override
  public long insert(final RMRadioEntity radio) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfRMRadioEntity.insertAndReturnId(_connection, radio);
    });
  }

  @Override
  public int update(final RMRadioEntity radio) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      int _result = 0;
      _result += __updateAdapterOfRMRadioEntity.handle(_connection, radio);
      return _result;
    });
  }

  @Override
  public List<RMRadioEntity> getAll() {
    final String _sql = "SELECT * from radios order by id DESC";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfLinkTrack = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "link");
        final int _columnIndexOfIsMp3 = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "is_mp3");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final List<RMRadioEntity> _result = new ArrayList<RMRadioEntity>();
        while (_stmt.step()) {
          final RMRadioEntity _item;
          final String _tmpLinkTrack;
          if (_stmt.isNull(_columnIndexOfLinkTrack)) {
            _tmpLinkTrack = null;
          } else {
            _tmpLinkTrack = _stmt.getText(_columnIndexOfLinkTrack);
          }
          final int _tmpIsMp3;
          _tmpIsMp3 = (int) (_stmt.getLong(_columnIndexOfIsMp3));
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          _item = new RMRadioEntity(_tmpName,_tmpLinkTrack,_tmpIsMp3);
          _item.id = _stmt.getLong(_columnIndexOfId);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<RMRadioEntity> getRadio(final long id) {
    final String _sql = "SELECT * from radios where id= ? limit 1";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfLinkTrack = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "link");
        final int _columnIndexOfIsMp3 = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "is_mp3");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final List<RMRadioEntity> _result = new ArrayList<RMRadioEntity>();
        while (_stmt.step()) {
          final RMRadioEntity _item;
          final String _tmpLinkTrack;
          if (_stmt.isNull(_columnIndexOfLinkTrack)) {
            _tmpLinkTrack = null;
          } else {
            _tmpLinkTrack = _stmt.getText(_columnIndexOfLinkTrack);
          }
          final int _tmpIsMp3;
          _tmpIsMp3 = (int) (_stmt.getLong(_columnIndexOfIsMp3));
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          _item = new RMRadioEntity(_tmpName,_tmpLinkTrack,_tmpIsMp3);
          _item.id = _stmt.getLong(_columnIndexOfId);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void delete(final long radioId) {
    final String _sql = "DELETE from radios where id= ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, radioId);
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
