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
import br.com.pointel.jarch.data.Filter;
import br.com.pointel.jarch.data.Index;
import br.com.pointel.jarch.data.Nature;
import br.com.pointel.jarch.data.Select;
import br.com.pointel.jarch.data.Table;
import br.com.pointel.jarch.data.TableHead;
import br.com.pointel.jarch.data.Typed;

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
        return eOrm.select(selectFiles.withFilter(filterByPlace.withValue(place)))
                .mapResult(ArchBaseUnit.class);
    }

    public synchronized List<ArchBaseUnit> getByVerifier(String verifier) throws Exception {
        return eOrm.select(selectFiles.withFilter(filterByVerifier.withValue(verifier)))
                .mapResults(ArchBaseUnit.class);
    }

    public synchronized List<ArchBaseUnit> getAll() throws Exception {
        return eOrm.select(selectFiles.withNoFilter())
                .mapResults(ArchBaseUnit.class);
    }

    public synchronized List<String> getAllPlaces() throws Exception {
        return eOrm.select(selectFilesPlace.withNoFilter())
                .mapResults(String.class);
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

    private synchronized void initDatabase() throws Exception {
        eOrm.createIfNotExists(tableFiles);
        eOrm.createIfNotExists(indexVerifier);
    }

    @Override
    public synchronized void close() throws IOException {
        try {
            this.connection.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
    
    private static Field fieldPlace = new Field("place", Nature.Chars, true, true);

    private static Typed typedPlace = fieldPlace.toTyped();

    private static Field fieldVerifier = new Field("verifier", Nature.Chars);

    private static Typed typedVerifier = fieldVerifier.toTyped();

    private static Field fieldModified = new Field("modified", Nature.Long);

    private static Typed typedModified = fieldModified.toTyped();

    private static Table tableFiles = new Table(new TableHead("files"), 
            List.of(fieldPlace, fieldVerifier, fieldModified));

    private static Index indexVerifier = new Index("files_verifier", tableFiles.tableHead, 
            List.of(fieldVerifier));

    private static Filter filterByPlace = new Filter().withField(fieldPlace);

    private static Filter filterByVerifier = new Filter().withField(fieldVerifier);

    private static Select selectFiles = new Select(tableFiles.tableHead, 
            List.of(typedPlace, typedVerifier, typedModified));

    private static Select selectFilesPlace = new Select(tableFiles.tableHead, 
            List.of(typedPlace));
            
}

