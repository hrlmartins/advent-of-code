package models;

import java.math.BigInteger;

public final class RegularFile extends GadgetFile {

    private BigInteger size;

    public RegularFile(String fileName, String size) {
        super(true, fileName);
        this.size = new BigInteger(size);
    }

    public BigInteger fileSize() {
        return size;
    }
}
