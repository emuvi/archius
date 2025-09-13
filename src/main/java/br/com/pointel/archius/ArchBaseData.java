package br.com.pointel.archius;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import br.com.pointel.jarch.data.Delete;
import br.com.pointel.jarch.data.EOrmSQLite;
import br.com.pointel.jarch.data.Field;
import br.com.pointel.jarch.data.Index;
import br.com.pointel.jarch.data.Insert;
import br.com.pointel.jarch.data.Nature;
import br.com.pointel.jarch.data.Select;
import br.com.pointel.jarch.data.Table;
import br.com.pointel.jarch.data.TableHead;

public class ArchBaseData implements Closeable {

    public static final String tableFilesName = "files";
    
    public static final String fieldPlaceName = "place";
    public static final String fieldVerifierName = "verifier";
    public static final String fieldModifiedName = "modified";

    public static final Table tableFiles = new Table(new TableHead(tableFilesName), 
            List.of(
                    new Field(fieldPlaceName, Nature.Chars, true, true),
                    new Field(fieldVerifierName, Nature.Chars),
                    new Field(fieldModifiedName, Nature.Long)
            ));

    public static final String indexVerifierName = "files_verifier";

    public static final Index indexVerifier = new Index(indexVerifierName, tableFiles.tableHead, 
            List.of(tableFiles.getFieldByName(fieldVerifierName)));

    private final Select selectFilesByPlace = tableFiles.toSelect();
    private final Select selectFiles = selectFilesByPlace.uponNoFilterList();
    private final Select selectFilesByVerifier = selectFiles.uponFilterList(
            tableFiles.getFieldByName(fieldVerifierName).toFilter());
    private final Select selectFilesPlace = selectFiles.uponFieldList(
            tableFiles.getFieldByName(fieldPlaceName).toTyped());

    private final Insert insertFile = tableFiles.toInsert();
    private final Delete deleteFile = tableFiles.toDelete();

    private final Connection connection;
    private final EOrmSQLite eOrm;

    public ArchBaseData(File root) throws Exception {
        this.connection = DriverManager.getConnection("jdbc:sqlite:"
                + new File(root, "arch-base.db3").getAbsolutePath());
        this.eOrm = new EOrmSQLite(this.connection);
        this.initDatabase();
    }

    public synchronized ArchBaseUnit getByPlace(String place) throws Exception {
        return eOrm.select(selectFilesByPlace.filterWithValues(place))
                .mapResult(ArchBaseUnit.class);
    }

    public synchronized List<ArchBaseUnit> getByVerifier(String verifier) throws Exception {
        return eOrm.select(selectFilesByVerifier.filterWithValues(verifier))
                .mapResults(ArchBaseUnit.class);
    }

    public synchronized List<ArchBaseUnit> getAll() throws Exception {
        return eOrm.select(selectFiles)
                .mapResults(ArchBaseUnit.class);
    }

    public synchronized List<String> getAllPlaces() throws Exception {
        return eOrm.select(selectFilesPlace)
                .mapResults(String.class);
    }

    public synchronized void putFile(String place, String verifier, Long modified) throws Exception {
        eOrm.delete(deleteFile.filterWithValues(place));
        var inserted = eOrm.insert(insertFile.valuedWithValues(place, verifier, modified));
        if (!inserted.hadEffect()) {
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
                
}

