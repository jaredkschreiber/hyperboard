package com.assemblynext.hyperboard.controllers;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.assemblynext.hyperboard.entities.Ban;
import com.assemblynext.hyperboard.entities.Entry;
import com.assemblynext.hyperboard.repositories.BanRepository;
import com.assemblynext.hyperboard.services.EntryService;
import com.assemblynext.hyperboard.utilities.HttpReqRespUtils;
import com.assemblynext.hyperboard.utilities.SiteConfig;

import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/")
public class MainController {

    @Autowired
    private EntryService entryService;

    @Autowired
    private BanRepository banRepository;

    private SiteConfig siteConfig = new SiteConfig();

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public String handleMaxUploadError(){
		return "maxupload";
	}

    @GetMapping("/faq")
    public String faq(){
        return "faq";
    }

    @GetMapping("")
    public String home(Model model){
        List<Entry> entries = entryService.pagedEntries(0);
        model.addAttribute("catalogtiles", entries);
        model.addAttribute("forward", 1);
        model.addAttribute("currentpage", 0);
        return "home";
    }

    @GetMapping("{page:[0-9]+}")
    public String home(Model model,@PathVariable Integer page){
        List<Entry> entries = entryService.pagedEntries(page);
        model.addAttribute("catalogtiles", entries);
        if (page > 0 ){
            model.addAttribute("back", page-1);
        }
        model.addAttribute("forward", page+1);
        model.addAttribute("currentpage", page);
        return "home";
    }

    @GetMapping("/tag/{tagid}")
    public String tagsHome(Model model,@PathVariable("tagid") String tagid){
        List<Entry> entries = entryService.pagedTaggedEntries(0, tagid);
        model.addAttribute("catalogtiles", entries);
        model.addAttribute("forward", "tag/"+tagid+"/"+1);
        model.addAttribute("currentpage", 0);
        model.addAttribute("tagname", tagid);
        return "home";
    }

    @GetMapping("/tag/{tagid}/{page:[0-9]+}")
    public String tagsHome(Model model,@PathVariable("tagid") String tagid,@PathVariable("page")  Integer page){
        List<Entry> entries = entryService.pagedTaggedEntries(page, tagid);
        model.addAttribute("catalogtiles", entries);
        if (page > 0 ){
            model.addAttribute("back", "tag/"+tagid+"/"+(page-1));
        }
        model.addAttribute("forward", "tag/"+tagid+"/"+(page+1));
        model.addAttribute("currentpage", page);
        model.addAttribute("tagname", tagid);
        return "home";
    }

    @GetMapping("new")
    public String newEntry(Model model,HttpServletRequest request){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //default expiration date set to 3 days after now
        String defaultDate = sdf.format(Date.from(LocalDateTime.now().plusDays(3).atZone(ZoneId.systemDefault()).toInstant()));
        model.addAttribute("datedefault",defaultDate);
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("csrf", csrfToken.getToken());
            model.addAttribute("csrfp", csrfToken.getParameterName());
        }
        //add max days
        model.addAttribute("maxdays", siteConfig.getMaxEntryExpirationDateDays());
        return "newentry";
    }

    @PostMapping("new")
    public String makeNewEntry(Model model
                              ,@RequestParam("name") Optional<String> name
                              ,@RequestParam("subject") String subject
                              ,@RequestParam("tags") Optional<String> tags
                              ,@RequestParam("comment") String commentString
                              ,@RequestParam("warning") Optional<Boolean> contentWarning
                              ,@RequestParam("expdt")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pruneDate
                              ,@RequestParam("attachment") MultipartFile file){
        //prune old bans
        List<Ban> unbanList = banRepository.pruneBans();
        banRepository.deleteAllInBatch(unbanList);
        //get user IP ADDR
        String userIP = HttpReqRespUtils.getClientIpAddressIfServletRequestExist();
        List<Ban> b = banRepository.findByIp(userIP);
        if(b.isEmpty()){
            try {
                //support: png, jpg, gif,pdf,mp3,mp4,webm
                final List<String> contentTypes = Arrays.asList("image/png", "image/jpeg", "image/gif","application/pdf","audio/mpeg","video/mp4","video/webm","application/zip");
                String fileContentType = file.getContentType();                   
                if (commentString.length() > 100000 || !contentTypes.contains(fileContentType)){
                    return "warning";
                } else {
                    //create new entry
                    entryService.createNewEntry(name, subject, tags, commentString, contentWarning, pruneDate, file);                      
                    return "redirect:/";
                }
            } catch (javax.validation.ConstraintViolationException ex){
                return "warning";
            }
        } else {
            Ban topBan = b.get(0);
            model.addAttribute("ban", topBan);
            return "banned";
        }
    }

    @GetMapping("/entries/{eid:[0-9]+}")
    public String thread(Model model,@PathVariable BigInteger eid,HttpServletRequest request){
        Optional<Entry> entry = entryService.getEntry(eid);
        if (entry.isPresent()){
            model.addAttribute("entry", entry.get());
            //get csrf
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
			if (csrfToken != null) {
				model.addAttribute("csrf", csrfToken.getToken());
				model.addAttribute("csrfp", csrfToken.getParameterName());
			}
            //encrypt time token
            BasicTextEncryptor textEncryptor = new BasicTextEncryptor();

            textEncryptor.setPasswordCharArray(siteConfig.getTimeCodePass().toCharArray());
            String encryptedText = textEncryptor.encrypt(LocalDateTime.now().toString());

            //add token to model
            model.addAttribute("tsenc", encryptedText);
            return "entry";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource");
        }
    }

    @PostMapping("/entries/{eid:[0-9]+}")
    public String postThread(Model model
                            ,@PathVariable BigInteger eid
                            ,@RequestParam("name") Optional<String> name
                            ,@RequestParam("tags") Optional<String> tags
                            ,@RequestParam("comment") String commentString
                            ,@RequestParam("tsenc") String tsEncoded
                            ,@RequestParam(value="replylist[]") Optional<BigInteger[]> replyList){
        //prune old bans
        List<Ban> unbanList = banRepository.pruneBans();
        banRepository.deleteAllInBatch(unbanList);
        //get user IP ADDR
        String userIP = HttpReqRespUtils.getClientIpAddressIfServletRequestExist();
        List<Ban> b = banRepository.findByIp(userIP);
        if(b.isEmpty()){
            BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
            textEncryptor.setPasswordCharArray(siteConfig.getTimeCodePass().toCharArray());

            try {
                String tsDec = textEncryptor.decrypt(tsEncoded);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                LocalDateTime dateTime = LocalDateTime.parse(tsDec, formatter);
                //decrypted date
                Duration duration = Duration.between(dateTime, LocalDateTime.now());
                long d = duration.toSeconds();
                if (d < 3){
                    return "warningbot";
                } else {
                    try {               
                        if (commentString.length() > 12000){
                            return "warning";
                        } else {
                            //create new entry
                            entryService.createNewReply(name, tags, commentString, eid, replyList);                      
                            return "redirect:/entries/" + eid +"#bottom";
                        }
                    } catch (javax.validation.ConstraintViolationException ex){
                        ex.printStackTrace();
                        return "warning";
                    }
                }
            } catch (Exception ex){
                ex.printStackTrace();
                return "warningbot";
            }
        } else {
            Ban topBan = b.get(0);
            model.addAttribute("ban", topBan);
            return "banned";
        }
    }
}
