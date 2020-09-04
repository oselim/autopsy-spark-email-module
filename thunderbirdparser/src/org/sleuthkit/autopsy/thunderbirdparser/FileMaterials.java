package org.sleuthkit.autopsy.thunderbirdparser;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class FileMaterials implements Serializable {
    private String name;
    private String nameExtension;
    private long size;
    private byte[] headBytes;

    public FileMaterials() {
    }

    public FileMaterials(String name, String nameExtension, long size, byte[] headBytes) {
        this.name = name;
        this.nameExtension = nameExtension;
        this.size = size;
        this.headBytes = headBytes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameExtension() {
        return nameExtension;
    }

    public void setNameExtension(String nameExtension) {
        this.nameExtension = nameExtension;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public byte[] getHeadBytes() {
        return headBytes;
    }

    public void setHeadBytes(byte[] headBytes) {
        this.headBytes = headBytes;
    }

    public boolean isValidMimeTypeMbox() {
        return (new String(getHeadBytes())).startsWith("From "); //NON-NLS
    }

    public boolean isEMLFile() {
        boolean isEMLFile = getNameExtension() != null && getNameExtension().equals("eml");
        if (isEMLFile) {
            isEMLFile = (new String(getHeadBytes())).contains(":"); //NON-NLS
        }
        return isEMLFile;
    }

    public boolean isPstFile() {
        int PST_HEADER = 0x2142444E;
        byte[] buffer;
        buffer = Arrays.copyOfRange(getHeadBytes(), 0, 4);
        if (buffer.length != 4) {
            return false;
        }
        ByteBuffer bb = ByteBuffer.wrap(buffer);

        return bb.getInt() == PST_HEADER;

    }

    public boolean isVcardFile() {
        long MIN_FILE_SIZE = 22;
        String VCARD_HEADER = "BEGIN:VCARD";
        if (getSize() > MIN_FILE_SIZE) {
            byte[] buffer;

            buffer = Arrays.copyOfRange(getHeadBytes(), 0, VCARD_HEADER.length() + 1);
            if (buffer.length > 0) {
                String header = new String(buffer);
                return header.equalsIgnoreCase(VCARD_HEADER);
            }
        }
        return false;
    }
}
