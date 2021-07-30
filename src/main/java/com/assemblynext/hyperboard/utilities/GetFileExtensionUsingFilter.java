package com.assemblynext.hyperboard.utilities;

import java.util.Optional;

public class GetFileExtensionUsingFilter {

    public static String getFileExt(String filename) {

        String extension = Optional.of(filename)
                                   .filter(f -> f.contains("."))
                                   .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                                   .orElse("");

        return extension;
    }
}
