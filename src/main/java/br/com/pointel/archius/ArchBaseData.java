package br.com.pointel.archius;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import br.com.pointel.jarch.data.EOrmSQLite;
import br.com.pointel.jarch.data.Field;
import br.com.pointel.jarch.data.Nature;
import br.com.pointel.jarch.data.Table;
import br.com.pointel.jarch.data.TableHead;

public class ArchBaseData implements Closeable {

    private final Connection connection;
    private final EOrmSQLite eOrm;

    public ArchBaseData(File root) throws Exception {
        this.connection = DriverManager.getConnection("jdbc:sqlite:"
                        + new File(root, "arch-base.db3").getAbsolutePath());
        this.eOrm = new EOrmSQLite(this.connection);
        this.initDatabase();
    }

    public synchronized ArchBaseUnit getByPlace(String place) throws Exception {
        var select = this.connection.prepareStatement(
                        "SELECT place, verifier, modified FROM files "
                                        + "WHERE place = ?");
        select.setString(1, place);
        var returned = select.executeQuery();
        if (returned.next()) {
            return new ArchBaseUnit(
                            returned.getString("place"),
                            returned.getString("verifier"),
                            returned.getLong("modified"));
        } else {
            return null;
        }
    }

    public synchronized List<ArchBaseUnit> getByVerifier(String verifier) throws Exception {
        var select = this.connection.prepareStatement(
                        "SELECT place, verifier, modified FROM files "
                                        + "WHERE verifier = ?");
        select.setString(1, verifier);
        var returned = select.executeQuery();
        var results = new ArrayList<ArchBaseUnit>();
        while (returned.next()) {
            results.add(new ArchBaseUnit(
                            returned.getString("place"),
                            returned.getString("verifier"),
                            returned.getLong("modified")));
        }
        return results;
    }

    public synchronized List<ArchBaseUnit> getAll() throws Exception {
        var select = this.connection.prepareStatement(
                        "SELECT place, verifier, modified FROM files");
        var returned = select.executeQuery();
        var results = new ArrayList<ArchBaseUnit>();
        while (returned.next()) {
            results.add(new ArchBaseUnit(
                            returned.getString("place"),
                            returned.getString("verifier"),
                            returned.getLong("modified")));
        }
        return results;
    }

    public synchronized List<String> getAllPlaces() throws Exception {
        var select = this.connection.prepareStatement(
                        "SELECT place FROM files");
        var returned = select.executeQuery();
        var results = new ArrayList<String>();
        while (returned.next()) {
            results.add(returned.getString("place"));
        }
        return results;
    }

    public synchronized void putFile(String place, String verifier, Long modified) throws Exception {
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

    public synchronized void delFolder(String place) throws Exception {
        var delete = this.connection.prepareStatement(
                        "DELETE FROM files WHERE place LIKE ?");
        delete.setString(1, place + "%");
        delete.executeUpdate();
    }

    public synchronized void delFile(String place) throws Exception {
        var delete = this.connection.prepareStatement(
                        "DELETE FROM files WHERE place = ?");
        delete.setString(1, place);
        delete.executeUpdate();
    }

    public synchronized void moveFolder(String fromPlace, String toPlace) throws Exception {
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

    public synchronized void moveFile(String fromPlace, String toPlace) throws Exception {
        var update = this.connection.prepareStatement(
                        "UPDATE files SET place = ? WHERE place = ?");
        update.setString(1, toPlace);
        update.setString(2, fromPlace);
        update.executeUpdate();
    }

    private void initDatabase() throws Exception {
        eOrm.create(FilesTable, true);
        this.connection.createStatement().execute(
                        "CREATE INDEX IF NOT EXISTS "
                                        + "files_verifier ON files (verifier)");
    }

    @Override
    public synchronized void close() throws IOException {
        try {
            this.connection.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
    
    private static Field FilesPlaceField = new Field("place", Nature.CHARS, true, true);

    private static Field FilesVerifierField = new Field("verifier", Nature.CHARS);

    private static Field FilesModifiedField = new Field("modified", Nature.LONG);

    private static Table FilesTable = new Table(
        new TableHead("files"),
        List.of(FilesPlaceField, FilesVerifierField, FilesModifiedField)
    );

}

