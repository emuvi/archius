package br.com.pointel.archius;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

public class ArchBaseData implements Closeable {

    private final ArchBase archBase;
    private final Connection connection;

    public ArchBaseData(ArchBase archBase) throws Exception {
        this.archBase = archBase;
        this.connection = DriverManager.getConnection("jdbc:sqlite:"
                        + new File(this.archBase.getRoot(), "arch-base.sdb").getAbsolutePath());
        this.initDatabase();
    }

    public ArchBaseUnit getByPlace(String place) throws Exception {
        var select = this.connection.prepareStatement(
                        "SELECT place, verifier, modified, indexed FROM files "
                                        + "WHERE place = ?");
        select.setString(1, place);
        var returned = select.executeQuery();
        if (returned.next()) {
            return new ArchBaseUnit(
                            returned.getString("place"),
                            returned.getString("verifier"),
                            returned.getLong("modified"),
                            returned.getLong("indexed"));
        } else {
            return null;
        }
    }

    public List<ArchBaseUnit> getByVerifier(String verifier) throws Exception {
        var select = this.connection.prepareStatement(
                        "SELECT place, verifier, modified, indexed FROM files "
                                        + "WHERE verifier = ?");
        select.setString(1, verifier);
        var returned = select.executeQuery();
        var results = new ArrayList<ArchBaseUnit>();
        while (returned.next()) {
            results.add(new ArchBaseUnit(
                            returned.getString("place"),
                            returned.getString("verifier"),
                            returned.getLong("modified"),
                            returned.getLong("indexed")));
        }
        return results;
    }

    public List<ArchBaseUnit> getAll() throws Exception {
        var select = this.connection.prepareStatement(
                        "SELECT place, verifier, modified, indexed FROM files");
        var returned = select.executeQuery();
        var results = new ArrayList<ArchBaseUnit>();
        while (returned.next()) {
            results.add(new ArchBaseUnit(
                            returned.getString("place"),
                            returned.getString("verifier"),
                            returned.getLong("modified"),
                            returned.getLong("indexed")));
        }
        return results;
    }

    public List<String> getAllPlaces() throws Exception {
        var select = this.connection.prepareStatement(
                        "SELECT place FROM files");
        var returned = select.executeQuery();
        var results = new ArrayList<String>();
        while (returned.next()) {
            results.add(returned.getString("place"));
        }
        return results;
    }

    public void putFile(String place, String verifier, Long modified) throws Exception {
        var delete = this.connection.prepareStatement(
                        "DELETE FROM files WHERE place = ?");
        delete.setString(1, place);
        delete.executeUpdate();
        var insert = this.connection.prepareStatement(
                        "INSERT INTO files (place, verifier, modified) " +
                                        "VALUES (?, ?, ?)");
        insert.setString(1, place);
        insert.setString(2, verifier);
        insert.setLong(3, modified);
        var results = insert.executeUpdate();
        if (results == 0) {
            throw new Exception("Could not put the file.");
        }
    }

    public void putIndexed(String place, Long indexed) throws Exception {
        var update = this.connection.prepareStatement(
                        "UPDATE files SET indexed = ? WHERE place = ?");
        update.setLong(1, indexed);
        update.setString(2, place);
        update.executeUpdate();
        var results = update.executeUpdate();
        if (results == 0) {
            throw new Exception("Could not put the indexed.");
        }
    }

    public void delFolder(String place) throws Exception {
        var delete = this.connection.prepareStatement(
                        "DELETE FROM files WHERE place LIKE ?");
        delete.setString(1, place + "%");
        delete.executeUpdate();
    }

    public void delFile(String place) throws Exception {
        var delete = this.connection.prepareStatement(
                        "DELETE FROM files WHERE place = ?");
        delete.setString(1, place);
        delete.executeUpdate();
    }

    public void moveFolder(String fromPlace, String toPlace) throws Exception {
        var select = this.connection.prepareStatement(
                        "SELECT place FROM files WHERE place LIKE ?");
        select.setString(1, fromPlace + "%");
        var returned = select.executeQuery();
        while (returned.next()) {
            var oldPlace = returned.getString("place");
            var newPlace = toPlace + oldPlace.substring(fromPlace.length());
            var update = this.connection.prepareStatement(
                            "UPDATE files SET place = ? WHERE place = ?");
            update.setString(1, newPlace);
            update.setString(2, oldPlace);
            update.executeUpdate();
        }
    }

    public void moveFile(String fromPlace, String toPlace) throws Exception {
        var update = this.connection.prepareStatement(
                        "UPDATE files SET place = ? WHERE place = ?");
        update.setString(1, toPlace);
        update.setString(2, fromPlace);
        update.executeUpdate();
    }

    private void initDatabase() throws Exception {
        this.connection.createStatement().execute(
                        "CREATE TABLE IF NOT EXISTS "
                                        + "files (place TEXT PRIMARY KEY, "
                                        + "verifier TEXT, modified INTEGER, indexed INTEGER)");
        this.connection.createStatement().execute(
                        "CREATE INDEX IF NOT EXISTS "
                                        + "files_verifier ON files (verifier)");
    }

    @Override
    public void close() throws IOException {
        try {
            this.connection.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

}
