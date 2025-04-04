package br.com.pointel.archius;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Config implements Serializable {

    private static final String CONFIG_NAME = "arch-config.ser";

    public static Config load(File root) throws Exception {
        var configFile = new File(root, CONFIG_NAME);
        if (configFile.exists()) {
            try (var fileIn = new FileInputStream(configFile);
                var objIn = new ObjectInputStream(fileIn)) {
                return (Config) objIn.readObject();
            }
       }
       return new Config();
    }

    public static void save(File root, Config config) throws Exception {
        var configFile = new File(root, CONFIG_NAME);
        try (var fileOut = new FileOutputStream(configFile);
            var objOut = new ObjectOutputStream(fileOut)) {
            objOut.writeObject(config);
        }
    }

    private final List<ConfigNamer> namers;

    public Config() {
        this(new ArrayList<>());
    }

    public Config(List<ConfigNamer> namers) {
        this.namers = namers;
    }

    public List<ConfigNamer> getNamers() {
        return this.namers;
    }

}
