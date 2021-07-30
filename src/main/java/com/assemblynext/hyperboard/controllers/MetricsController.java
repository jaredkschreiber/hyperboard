package com.assemblynext.hyperboard.controllers;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.assemblynext.hyperboard.entities.Tag;
import com.assemblynext.hyperboard.repositories.EntryRepository;
import com.assemblynext.hyperboard.repositories.ReplyRepository;
import com.assemblynext.hyperboard.repositories.TagRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MetricsController {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @GetMapping("/metrics")
    public String metrics(Model model){
        final LocalDateTime dateMinusDays = LocalDateTime.now().minusDays(3);
        final List<Tag> tagsdata = tagRepository.latest(dateMinusDays);
        SortedMap<String,Integer> tagStrings = new TreeMap<String,Integer>();
        for (var word : tagsdata){
            if (tagStrings.containsKey(word.getTag())){
                tagStrings.put(word.getTag(), tagStrings.get(word.getTag()) + 1);
            } else {
                tagStrings.put(word.getTag(), 1);
            }
        }

        LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();
        tagStrings.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(15).forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        
        SortedMap<String,Integer> tagStrings2 = new TreeMap<String,Integer>();
        for (var word : tagsdata){
            if (tagStrings2.containsKey(word.getTag())){
                tagStrings2.put(word.getTag(), tagStrings2.get(word.getTag()) + word.getEntry().getReplies().size());
            } else {
                tagStrings2.put(word.getTag(), word.getEntry().getReplies().size());
            }
        }
        
        LinkedHashMap<String, Integer> reverseSortedMap2 = new LinkedHashMap<>();
        tagStrings2.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(15).forEachOrdered(x -> reverseSortedMap2.put(x.getKey(), x.getValue()));
        
        model.addAttribute("tagsdata",reverseSortedMap);
        model.addAttribute("tagsdata2",reverseSortedMap2);
        model.addAttribute("uniqueips", entryRepository.uniqueEntryIps(dateMinusDays));
        model.addAttribute("uniquecommentips", replyRepository.uniqueReplyIps(dateMinusDays));
        model.addAttribute("ppd", entryRepository.uniqueEntries(dateMinusDays));
        model.addAttribute("cpd", replyRepository.uniqueReplies(dateMinusDays));

        return "metrics";
    }
}
