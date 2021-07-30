package com.assemblynext.hyperboard.services;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.assemblynext.hyperboard.entities.Entry;
import com.assemblynext.hyperboard.entities.Reply;
import com.assemblynext.hyperboard.entities.ReplyLink;
import com.assemblynext.hyperboard.entities.Tag;
import com.assemblynext.hyperboard.repositories.EntryRepository;
import com.assemblynext.hyperboard.repositories.ReplyLinkRepository;
import com.assemblynext.hyperboard.repositories.ReplyRepository;
import com.assemblynext.hyperboard.repositories.TagRepository;
import com.assemblynext.hyperboard.utilities.GetFileExtensionUsingFilter;
import com.assemblynext.hyperboard.utilities.GetTagsFromString;
import com.assemblynext.hyperboard.utilities.HttpReqRespUtils;
import com.assemblynext.hyperboard.utilities.SiteConfig;

@Service
public class EntryService {

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private ReplyLinkRepository replyLinkRepository;

    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    private PostProcessingService postProcessingService;

    @Autowired
    private PruneService pruneService;

    private SiteConfig siteConfig = new SiteConfig();

    public List<Entry> pagedEntries(Integer page){
        return entryRepository.findAll(PageRequest.of(page, siteConfig.getCatalogSize(), Sort.by("createDate").descending())).getContent();
    }

    public List<Entry> pagedTaggedEntries(Integer page, String tagid){
        return entryRepository.findByTagStr(tagid, PageRequest.of(page, siteConfig.getCatalogSize(), Sort.by("createDate").descending())).getContent();
    }

    public Optional<Entry> getEntry(BigInteger eid){
        return entryRepository.findById(eid);
    }

    public void createNewEntry(Optional<String> name
                              ,String subject
                              ,Optional<String> tags
                              ,String commentString
                              ,Optional<Boolean> contentWarning
                              ,LocalDate pruneDate
                              ,MultipartFile file){
            //get file ext
            var fileExt = GetFileExtensionUsingFilter.getFileExt(file.getOriginalFilename());

            //get user IP ADDR
            String userIP = HttpReqRespUtils.getClientIpAddressIfServletRequestExist();

            Entry newEntry = new Entry();
            newEntry.setIp(userIP);
            newEntry.setAttachmentType(fileExt);
            newEntry.setComment(postProcessingService.processComment(commentString));
            //check if date exceeds max expiration date
            if (pruneDate.isAfter(LocalDate.now().plusDays(siteConfig.getMaxEntryExpirationDateDays()))){
                LocalDate pruneDateModified = LocalDate.now().plusDays(siteConfig.getMaxEntryExpirationDateDays());
                newEntry.setPruneDate(pruneDateModified.atTime(LocalTime.now()));
            } else {
                newEntry.setPruneDate(pruneDate.atTime(LocalTime.now()));
            }
            newEntry.setSubject(subject);

            if (name.isPresent()){
                String processedName = postProcessingService.getTrip(name.get());
                newEntry.setName(processedName);
            }

            if (contentWarning.isPresent()){
                newEntry.setContentWarning(true);
            } else {
                newEntry.setContentWarning(false);
            }
            
            //default params
            newEntry.setArchive(false);
            newEntry.setCreateDate(LocalDateTime.now());

            //persist
            entryRepository.save(newEntry);

            //set tags here
            if (tags.isPresent()){
                String[] parsedTags = GetTagsFromString.getTags(tags.get());
                Set<String> set = new HashSet<>(Arrays.asList(parsedTags));//uniqueness guarenteed
                parsedTags = set.toArray(String[]::new);
                for (String t : parsedTags){
                    if (!t.isBlank()){
                        Tag tag = new Tag();
                        tag.setEntry(newEntry);
                        tag.setTag(t.toLowerCase());
                        tagRepository.save(tag);
                    }
                }
            }
            //end tags

            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get("./public/uploads/"
                                    + newEntry.getId().toString()
                                    + "." + fileExt
                                    );
                Files.write(path, bytes);

                //create thumbnail if image
                if (fileExt.equalsIgnoreCase("JPEG") || fileExt.equalsIgnoreCase("JPG") || fileExt.equalsIgnoreCase("PNG") || fileExt.equalsIgnoreCase("GIF")){
                    thumbnailService.createThumbnail(path);
                }                 

            } catch (IOException e) {
                e.printStackTrace();
            }
        //prune old entries
        pruneService.pruneEntries();
    }

    public void createNewReply(Optional<String> name
                              ,Optional<String> tags
                              ,String commentString
                              ,BigInteger entryId
                              ,Optional<BigInteger[]> replyList){
        Optional<Entry> eid = entryRepository.findById(entryId);
        if (eid.isPresent()){
            //get user IP ADDR
            String userIP = HttpReqRespUtils.getClientIpAddressIfServletRequestExist();

            Reply newReply = new Reply();
            newReply.setEntry(eid.get());
            newReply.setIp(userIP);
            newReply.setComment(postProcessingService.processComment(commentString));

            if (name.isPresent()){
                String processedName = postProcessingService.getTrip(name.get());
                newReply.setName(processedName);
            }
            
            //default params
            newReply.setCreateDate(LocalDateTime.now());

            //persist
            replyRepository.save(newReply);

            //set tags here
            if (tags.isPresent()){
                String[] parsedTags = GetTagsFromString.getTags(tags.get());
                Set<String> set = new HashSet<>(Arrays.asList(parsedTags));//uniqueness guarenteed for reply
                //get entry's current tags
                List<Tag> currentTags = eid.get().getTags();
                //remove current tags from reply, to avoid adding duplicates
                for(var t : currentTags){
                    set.remove(t.getTag());
                }
                //convert set to array
                parsedTags = set.toArray(String[]::new);
                for (String t : parsedTags){
                    if (!t.isBlank()){
                        try {
                            Tag tag = new Tag();
                            tag.setEntry(eid.get());
                            tag.setTag(t.toLowerCase());
                            tagRepository.save(tag);
                        } catch (org.hibernate.exception.ConstraintViolationException ex){
                            //duplicate tag
                            ex.printStackTrace();
                        }
                    }
                }
            }
            //end tags
            //create replylink
            if (replyList.isPresent()){
                for (BigInteger replylink : replyList.get()){
                    ReplyLink r = new ReplyLink();
                    Optional<Reply> to = replyRepository.findById(replylink);
                    //if post being replied to exists
                    if (to.isPresent()){
                        r.setReplyFrom(newReply);
                        r.setReplyTo(to.get());
                        replyLinkRepository.save(r);
                    }
                }
            }
            //end replylink
        }//endif
    }
}
