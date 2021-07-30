package com.assemblynext.hyperboard.utilities;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class GetTripHashFromCode {

    public static String get(String tripcode) {
        var siteConfig = new SiteConfig();
        byte[] bytes = (siteConfig.getTripHash()+tripcode).getBytes();
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        long result = crc32.getValue();
        return Long.toHexString(result);
    }
}
