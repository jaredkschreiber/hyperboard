package com.assemblynext.hyperboard.utilities;

public class GetTagsFromString {
    public static String[] getTags(String tagString) {
        String[] tags = tagString.split("[#]");
        for (int i = 0; i < tags.length; i++){
            tags[i] = tags[i].trim().replaceAll("\\s.*", "");
        }
        return tags;
    }
}
