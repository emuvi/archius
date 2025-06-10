package br.com.pointel.archius;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

public class ArchIndexData implements Closeable {

    private final Connection connection;

    public ArchIndexData(File folder) throws Exception {
        this.connection = DriverManager.getConnection("jdbc:sqlite:"
                        + new File(folder, "arch-index.db3").getAbsolutePath());
        this.initDatabase();
    }

    public synchronized ArchIndexUnit getByName(String name) throws Exception {
        var select = this.connection.prepareStatement(
                        "SELECT name, words, likes, indexed FROM files "
                                        + "WHERE name = ?");
        select.setString(1, name);
        var returned = select.executeQuery();
        if (returned.next()) {
            return new ArchIndexUnit(
                            returned.getString("name"),
                            returned.getString("words"),
                            returned.getString("likes"),
                            returned.getLong("indexed"));
        } else {
            return null;
        }
    }

    public synchronized Long getIndexedByName(String name) throws Exception {
        var select = this.connection.prepareStatement(
                        "SELECT indexed FROM files WHERE name = ?");
        select.setString(1, name);
        var returned = select.executeQuery();
        if (returned.next()) {
            return returned.getLong("indexed");
        } else {
            return null;
        }
    }

    public synchronized String getWordsByName(String name) throws Exception {
        var select = this.connection.prepareStatement(
                        "SELECT words FROM files WHERE name = ?");
        select.setString(1, name);
        var returned = select.executeQuery();
        if (returned.next()) {
            return returned.getString("words");
        } else {
            return null;
        }
    }

    public synchronized String getLikesByName(String name) throws Exception {
        var select = this.connection.prepareStatement(
                        "SELECT likes FROM files WHERE name = ?");
        select.setString(1, name);
        var returned = select.executeQuery();
        if (returned.next()) {
            return returned.getString("likes");
        } else {
            return null;
        }
    }

    public synchronized List<ArchIndexUnit> getAll() throws Exception {
        var select = this.connection.prepareStatement(
                        "SELECT name, words, likes, indexed FROM files");
        var returned = select.executeQuery();
        var results = new ArrayList<ArchIndexUnit>();
        while (returned.next()) {
            results.add(new ArchIndexUnit(
                            returned.getString("name"),
                            returned.getString("words"),
                            returned.getString("likes"),
                            returned.getLong("indexed")));
        }
        return results;
    }

    public synchronized List<Pair<String, Long>> getAllIndexed() throws Exception {
        var select = this.connection.prepareStatement(
                        "SELECT name, indexed FROM files");
        var returned = select.executeQuery();
        var results = new ArrayList<Pair<String, Long>>();
        while (returned.next()) {
            results.add(Pair.of(
                            returned.getString("name"),
                            returned.getLong("indexed")));                       
        }
        return results;
    }

    public synchronized void putFile(String name, String words, String likes, Long indexed) throws Exception {
        var delete = this.connection.prepareStatement(
                        "DELETE FROM files WHERE name = ?");
        delete.setString(1, name);
        delete.executeUpdate();
        var insert = this.connection.prepareStatement(
                        "INSERT INTO files (name, words, likes, indexed) " +
                                        "VALUES (?, ?, ?, ?)");
        insert.setString(1, name);
        insert.setString(2, words);
        insert.setString(3, likes);
        insert.setLong(4, indexed);
        var results = insert.executeUpdate();
        if (results == 0) {
            throw new Exception("Could not put the file.");
        }
    }

    public synchronized void delFile(String name) throws Exception {
        var delete = this.connection.prepareStatement(
                        "DELETE FROM files WHERE name = ?");
        delete.setString(1, name);
        delete.executeUpdate();
    }

    private void initDatabase() throws Exception {
        this.connection.createStatement().execute(
                        "CREATE TABLE IF NOT EXISTS "
                                        + "files (name TEXT PRIMARY KEY, "
                                        + "words TEXT, likes TEXT, indexed INTEGER)");
    }

    @Override
    public synchronized void close() throws IOException {
        try {
            this.connection.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

}
