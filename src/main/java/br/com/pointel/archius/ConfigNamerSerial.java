package br.com.pointel.archius;

public class ConfigNamerSerial extends ConfigNamer {

    private final Integer length;
    private final String prefix;
    private final String suffix;

    public ConfigNamerSerial(String name, Boolean required, Integer length, String prefix, String suffix) {
        super(ConfigNamerType.SERIAL, name, required);
        this.length = length;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public Integer getLength() {
        return this.length;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

}
