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
import br.com.pointel.jarch.data.FilterLikes;
import br.com.pointel.jarch.data.Index;
import br.com.pointel.jarch.data.Insert;
import br.com.pointel.jarch.data.Nature;
import br.com.pointel.jarch.data.Select;
import br.com.pointel.jarch.data.Table;
import br.com.pointel.jarch.data.TableHead;
import br.com.pointel.jarch.data.Update;

public class ArchBaseData implements Closeable {

    public static final String TABLE_FILES_NAME = "files";
    
    public static final String FIELD_PLACE_NAME = "place";
    public static final String FIELD_VERIFIER_NAME = "verifier";
    public static final String FIELD_MODIFIED_NAME = "modified";

    public static final String FILES_VERIFIER_INDEX = "files_verifier";

    public static final Field fieldPlace = new Field(FIELD_PLACE_NAME, Nature.Chars, true, true);
    public static final Field fieldVerifier = new Field(FIELD_VERIFIER_NAME, Nature.Chars);
    public static final Field fieldModified = new Field(FIELD_MODIFIED_NAME, Nature.Long);

    public static final Table tableFiles = new Table(new TableHead(TABLE_FILES_NAME), 
            List.of(fieldPlace, fieldVerifier, fieldModified));

    public static final Index indexVerifier = new Index(FILES_VERIFIER_INDEX, tableFiles.tableHead,
            List.of(fieldVerifier));


    private final Select selectFilesByPlace = tableFiles.toSelect();
    private final Select selectFiles = selectFilesByPlace.uponNoFilterList();
    private final Select selectFilesByVerifier = selectFiles
            .uponFilterList(fieldVerifier.toFilter());
    private final Select selectFilesPlace = selectFiles
            .uponFieldList(fieldPlace.toTyped());
    private final Select selectFilesPlaceByStartsWith = selectFilesPlace
            .uponFilterList(fieldPlace.toFilter().withLikes(FilterLikes.StartsWith));

    private final Insert insertFile = tableFiles.toInsert();
    private final Delete deleteFile = tableFiles.toDelete();
    private final Delete deleteFilesPlaceStartsWith = deleteFile
            .uponFilterList(fieldPlace.toFilter().withLikes(FilterLikes.StartsWith));
    private final Update updateFilePlace = tableFiles.toUpdate()
            .withValuedList(fieldPlace.toValued());

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
        var inserted = eOrm.insert(insertFile
                .valuedWithValues(place, verifier, modified));
        if (!inserted.hadEffect()) {
            throw new Exception("Could not put the file.");
        }
    }

    public synchronized void delFolder(String place) throws Exception {
        eOrm.delete(deleteFilesPlaceStartsWith
                .filterWithValues(place));
    }

    public synchronized void delFile(String place) throws Exception {
        eOrm.delete(deleteFile
                .filterWithValues(place));
    }

    public synchronized void moveFolder(String fromPlace, String toPlace) throws Exception {       
        var oldPlaces = eOrm.select(selectFilesPlaceByStartsWith
                .filterWithValues(fromPlace))
                        .mapResults(String.class);
        for (var oldPlace : oldPlaces) {
            var newPlace = toPlace + oldPlace.substring(fromPlace.length());
            moveFile(oldPlace, newPlace);
        }
    }

    public synchronized void moveFile(String fromPlace, String toPlace) throws Exception {
        eOrm.update(updateFilePlace
                .valuedWithValues(toPlace)
                .filterWithValues(fromPlace));
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
