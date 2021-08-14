package com.assemblynext.hyperboard.controllers;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.assemblynext.hyperboard.entities.Ban;
import com.assemblynext.hyperboard.entities.Entry;
import com.assemblynext.hyperboard.entities.Reply;
import com.assemblynext.hyperboard.entities.Report;
import com.assemblynext.hyperboard.entities.Tag;
import com.assemblynext.hyperboard.entities.User;
import com.assemblynext.hyperboard.repositories.BanRepository;
import com.assemblynext.hyperboard.repositories.EntryRepository;
import com.assemblynext.hyperboard.repositories.ReplyRepository;
import com.assemblynext.hyperboard.repositories.ReportRepository;
import com.assemblynext.hyperboard.repositories.TagRepository;
import com.assemblynext.hyperboard.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/mod")
public class ModController {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private BanRepository banRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @GetMapping("/admin")
    public String adminpage(Model model
                         ,HttpServletRequest request
                         ,@RequestParam Optional<String> staff
                         ,@RequestParam Optional<String> rank){
        //get csrf
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("csrf", csrfToken.getToken());
            model.addAttribute("csrfp", csrfToken.getParameterName());
        }

        if(staff.isEmpty() && rank.isEmpty()){
            model.addAttribute("userlist", userRepository.findAll(PageRequest.of(0,75).withSort(Sort.by(Sort.Direction.DESC,"id"))));
        }

        if (staff.isPresent()){
            model.addAttribute("stafflist", userRepository.findStaff());
            model.addAttribute("staffpage", true);
        }
        
        if (rank.isPresent()){
            model.addAttribute("rankpage", true);
        }

        return "admin";
    }

    @PostMapping("/admin")
    public String accountdelete(@RequestParam(value="accountlist[]") Optional<BigInteger[]> accountlist){
        if (accountlist.isPresent()){
            for (var account : accountlist.get()){
                userRepository.deleteById(account);
            }
        }
        return "redirect:/mod/admin";
    }

    @PostMapping("/admin/demod")
    public String accountdemod(@RequestParam(value="accountmod") Optional<String> accountMod){
        if (accountMod.isPresent()){
            Optional<User> candidate = userRepository.findUserByUsername(accountMod.get().trim());
            if (candidate.isPresent()){
                User moduser = candidate.get();
                moduser.setMod(!moduser.getMod());
                userRepository.save(moduser);
            }
        }
        return "redirect:/mod/admin?rank";
    }

    @PostMapping("/admin/deadmin")
    public String accountdeadmin(@RequestParam(value="accountmod") Optional<String> accountMod){
        if (accountMod.isPresent()){
            Optional<User> candidate = userRepository.findUserByUsername(accountMod.get().trim());
            if (candidate.isPresent()){
                User moduser = candidate.get();
                moduser.setAdmin(!moduser.getAdmin());
                userRepository.save(moduser);
            }
        }
        return "redirect:/mod/admin?rank";
    }

    @GetMapping("/home")
    public String modpage(Model model
                         ,HttpServletRequest request
                         ,@RequestParam Optional<String> bans
                         ,@RequestParam Optional<String> archive
                         ,@RequestParam Optional<String> tags){
        //get csrf
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("csrf", csrfToken.getToken());
            model.addAttribute("csrfp", csrfToken.getParameterName());
        }

        if(bans.isEmpty() && archive.isEmpty()){
            var reports = reportRepository.findAll(PageRequest.of(0,50));
            model.addAttribute("reports", reports);
    
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String defaultDate = sdf.format(Date.from(LocalDateTime.now().plusDays(3).atZone(ZoneId.systemDefault()).toInstant()));
            model.addAttribute("datedefault",defaultDate);
        }

        if (bans.isPresent()){
            model.addAttribute("banspage", true);
            model.addAttribute("banslist", banRepository.findAll(PageRequest.of(0,50).withSort(Sort.by(Sort.Direction.DESC,"id"))));
        }

        if(archive.isPresent()){
            model.addAttribute("archivepage", true);
        }

        if(tags.isPresent()){
            model.addAttribute("tagspage", true);
        }

        return "mod";
    }

    @PostMapping("/home")
    public String modaction(@RequestParam("action") String action
                           ,@RequestParam("actionreason") String reason
                           ,@RequestParam(value="reportlist[]") Optional<String[]> reportList
                           ,@RequestParam("expdt")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expirationDate){
        if (reportList.isPresent()){
            String[] reports = reportList.get();
            for (var report : reports){
                String reportType = report.substring(0,1);
                BigInteger reportId = new BigInteger(report.substring(1));
                Optional<Report> r = reportRepository.findById(reportId);
                if (r.isPresent()){
                    if(reason.isBlank()){
                        reason = r.get().getReason();
                    }
                    //depending on action do something different
                    if (action.equals("dismiss")){
                        reportRepository.delete(r.get());
                    }//end dismiss
                    //spoiler/unspoiler
                    if(action.equals("spoiler") && reportType.equals("e")){
                        var ent = r.get().getEntry();
                        ent.setContentWarning(!ent.getContentWarning());
                        entryRepository.save(ent);
                        reportRepository.delete(r.get());
                    }//end spoiler
                    //delete only
                    if(action.equals("delete")){
                        if (reportType.equals("r")){
                            Reply rep = r.get().getReply();
                            replyRepository.delete(rep);
                        } else if (reportType.equals("e")){
                            Entry ent = r.get().getEntry();
                            //delete files
                                Path p = Paths.get("./public/uploads/"
                                + ent.getId().toString()
                                + "." + ent.getAttachmentType()
                                );
                    
                                Path thumb = Paths.get("./public/thumbs/"
                                + "thumbnail."+ent.getId().toString()
                                + "." + ent.getAttachmentType()
                                );
                    
                                try {
                                    Files.delete(p);
                                    Files.delete(thumb);
                                } catch (IOException e){
                                    e.printStackTrace();
                                }
                            //delete entry
                            entryRepository.delete(ent);
                        }
                        
                        //delete report
                        reportRepository.delete(r.get());
                    }//end delete only

                    if(action.equals("ban")){
                        if (reportType.equals("e")){
                            Entry ent = r.get().getEntry();
                            String ip = ent.getIp();
                            Ban b = new Ban();
                            b.setExpDate(expirationDate.atTime(LocalTime.now()));
                            b.setIp(ip);
                            b.setReason(reason);
                            banRepository.save(b);

                            //save bantext
                            ent.setComment(ent.getComment()+
                            "<br><span class='redtext'><strong>Blogger banned for this entry @"
                            +LocalDate.now() + " Until: " + expirationDate
                            +" for reason: "
                            +reason
                            +"</strong></span>");
                            entryRepository.save(ent);
                        } else if(reportType.equals("r")){
                            Reply rep = r.get().getReply();
                            String ip = rep.getIp();
                            Ban b = new Ban();
                            b.setExpDate(expirationDate.atTime(LocalTime.now()));
                            b.setIp(ip);
                            b.setReason(reason);
                            banRepository.save(b);

                            //save bantext
                            rep.setComment(rep.getComment()+
                            "<br><span class='redtext'><strong>Commenter banned for this entry @"
                            +LocalDate.now() + " Until: " + expirationDate
                            +" for reason: "
                            +reason
                            +"</strong></span>");
                            replyRepository.save(rep);
                        }
                        
                        //delete report
                        reportRepository.delete(r.get());
                    }//end ban only
                    if(action.equals("bananddelete")){
                        if (reportType.equals("e")){
                            Entry ent = r.get().getEntry();
                            String ip = ent.getIp();
                            Ban b = new Ban();
                            b.setExpDate(expirationDate.atTime(LocalTime.now()));
                            b.setIp(ip);
                            b.setReason(reason);
                            banRepository.save(b);

                            //delete files
                                Path p = Paths.get("./public/uploads/"
                                + ent.getId().toString()
                                + "." + ent.getAttachmentType()
                                );
                                            
                                Path thumb = Paths.get("./public/thumbs/"
                                + "thumbnail."+ent.getId().toString()
                                + "." + ent.getAttachmentType()
                                );
                                            
                                try {
                                    Files.delete(p);
                                    Files.delete(thumb);
                                } catch (IOException e){
                                    e.printStackTrace();
                                }
                            //delete entry
                            entryRepository.delete(ent);
                        } else if(reportType.equals("r")){
                            Reply rep = r.get().getReply();
                            String ip = rep.getIp();
                            Ban b = new Ban();
                            b.setExpDate(expirationDate.atTime(LocalTime.now()));
                            b.setIp(ip);
                            b.setReason(reason);
                            banRepository.save(b);
                            replyRepository.delete(rep);
                        }
                        
                        //delete report
                        reportRepository.delete(r.get());
                    }//end ban & delete
                }
            }
        }
        return "redirect:/mod/home";
    }//end /home POST

    @PostMapping("/unban")
    public String unban(Model model
                       ,HttpServletRequest request
                       ,@RequestParam(value="banlist[]") Optional<BigInteger[]> banList){
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("csrf", csrfToken.getToken());
            model.addAttribute("csrfp", csrfToken.getParameterName());
        }
        if (banList.isPresent()){
            for (BigInteger banid : banList.get()){
                banRepository.deleteById(banid);
            }   
        }
        return "redirect:/mod/home?bans";
    }

    @PostMapping("/archive")
    public String archive(@RequestParam(value="archiveid") BigInteger eid){
        Optional<Entry> entry = entryRepository.findById(eid);
        if (entry.isPresent()){
            Entry e = entry.get();
            e.setArchive(!e.getArchive());
            entryRepository.save(e);
        }
        return "redirect:/mod/home?archive";
    }

    @PostMapping("/tag")
    public String tag(@RequestParam(value="tagname") String tag){
        List<Tag> tags = tagRepository.findByTag(tag);
        tagRepository.deleteAllInBatch(tags);
        return "redirect:/mod/home?tags";
    }
}
