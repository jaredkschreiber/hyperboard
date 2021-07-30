package com.assemblynext.hyperboard.controllers;

import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;

import com.assemblynext.hyperboard.entities.Reply;
import com.assemblynext.hyperboard.entities.Report;
import com.assemblynext.hyperboard.repositories.EntryRepository;
import com.assemblynext.hyperboard.repositories.ReplyRepository;
import com.assemblynext.hyperboard.repositories.ReportRepository;
import com.assemblynext.hyperboard.utilities.HttpReqRespUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private ReplyRepository replyRepository;
    
    @GetMapping("/{postType}/{id:[0-9]+}")
    public String reportpage(Model model,@PathVariable String postType,@PathVariable BigInteger id,HttpServletRequest request){
        //get csrf
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("csrf", csrfToken.getToken());
            model.addAttribute("csrfp", csrfToken.getParameterName());
        }
        model.addAttribute("posttype", postType);
        model.addAttribute("postid", id);
        return "report";
    }

    @PostMapping("/new")
    public String reportNew(@RequestParam("reason") String reason
                           ,@RequestParam("post_type") String postType
                           ,@RequestParam("post_id") BigInteger postId){

        //get user IP ADDR
        String userIP = HttpReqRespUtils.getClientIpAddressIfServletRequestExist();
        Report r = new Report();
        if(postType.equals("entries")){
            r.setEntry(entryRepository.getById(postId));
        } else if (postType.equals("reply")){
            Reply rp = replyRepository.getById(postId);
            r.setReply(rp);
            r.setEntry(rp.getEntry());
        }
        r.setReason(reason);
        r.setIp(userIP);
        reportRepository.save(r);
        return "redirect:/";
    }
}
