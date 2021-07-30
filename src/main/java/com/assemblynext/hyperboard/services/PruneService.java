package com.assemblynext.hyperboard.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.assemblynext.hyperboard.entities.Entry;
import com.assemblynext.hyperboard.repositories.EntryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PruneService {

    @Autowired
    private EntryRepository entryRepository;

    public void pruneEntries(){
        final List<Entry> prunable = entryRepository.findPrunableEntries();

        for (Entry entry : prunable){
            //delete files
            Path p = Paths.get("./public/uploads/"
            + entry.getId().toString()
            + "." + entry.getAttachmentType()
            );

            Path thumb = Paths.get("./public/thumbs/"
            + "thumbnail."+entry.getId().toString()
            + "." + entry.getAttachmentType()
            );

            try {
                Files.delete(p);
                Files.delete(thumb);
            } catch (IOException e){
                e.printStackTrace();
            }
        }//end for
        //delete all entries in batch
        entryRepository.deleteAllInBatch(prunable);
    }//end pruneEntries()
}//end class
