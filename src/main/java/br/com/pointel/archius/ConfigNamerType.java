package br.com.pointel.archius;

public enum ConfigNamerType {

    COMBO(ConfigNamerCombo.class), 
    FIELD(ConfigNamerField.class), 
    SEPARATOR(ConfigNamerSeparator.class), 
    SERIAL(ConfigNamerSerial.class);

    private final Class<? extends ConfigNamer> clazz;

    private ConfigNamerType(Class<? extends ConfigNamer> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends ConfigNamer> getClazz() {
        return this.clazz;
    }

}
