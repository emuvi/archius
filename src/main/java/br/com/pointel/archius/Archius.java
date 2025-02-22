package br.com.pointel.archius;

import br.com.pointel.jarch.mage.WizProps;

public class Archius {

    public static final String ARCHIUS_SPEED = "ARCHIUS_SPEED";
    public static final Integer DEFAULT_SPEED = WizProps.get(ARCHIUS_SPEED, 8);

    public static void main(String[] args) {
        Desk.start(args);
    }

}
