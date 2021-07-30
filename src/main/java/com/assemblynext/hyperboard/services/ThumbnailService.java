package com.assemblynext.hyperboard.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

@Service
public class ThumbnailService {
    public void createThumbnail(Path pathName){
        try {
            File sourceFile = pathName.toFile();
            File destinationDir = new File("./public/thumbs/");
            try {
                Thumbnails.of(sourceFile).width(150).toFiles(destinationDir, Rename.PREFIX_DOT_THUMBNAIL);
            } catch (java.lang.ArrayIndexOutOfBoundsException ex){
                //if animated gif can't be thumbnailed, use full file
                Files.copy(Paths.get("./public/uploads/"+sourceFile.getName()), Paths.get("./public/thumbs/thumbnail."+sourceFile.getName()));
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
