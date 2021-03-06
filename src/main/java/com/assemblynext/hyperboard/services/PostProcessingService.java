package com.assemblynext.hyperboard.services;

import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

import com.assemblynext.hyperboard.utilities.GetTripHashFromCode;
import com.assemblynext.hyperboard.utilities.SiteConfig;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

@Service
public class PostProcessingService {

    private SiteConfig siteConfig = new SiteConfig();

    public String getTrip(String name){
        String[] stringparts = name.split("[#]");
        if (stringparts.length == 2){
            return stringparts[0] + "!" + GetTripHashFromCode.get(stringparts[1]);
        } else {
            return name;
        }
    }

    public String processComment(String comment){

        String result = comment.trim();
        //hyperlink replacement
        result = result.replaceAll("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", "[link]$0[/blink]$0[/link]");
        //greentext replacement
        result = result.replaceAll("(^|\\s)(>)([^\\n\\r$]*)", "[greentext]$1$2$3[/greentext]");
        //red replacement
        result = result.replaceAll("==(.*?)==", "[redtext]$1[/redtext]");
        //spoiler replacement
        result = result.replaceAll("\\*\\*(.*?)\\*\\*", "[spoiler]$1[/spoiler]");
        //line break
        result = result.replaceAll("(\r\n|\n)", "[br]");
        //strip html tags
        result = Jsoup.clean(result, Safelist.none());

        //replacement stage

        //final replacement hyperlinks
        result = result.replaceAll("\\[link\\]", "<a target=\"_blank\" href=\"");
        result = result.replaceAll("\\[/blink\\]", "\">");
        result = result.replaceAll("\\[/link\\]", "</a>");

        //final replacement break
        result = result.replaceAll("\\[br\\]", "<br>");

        //final replacement spoiler
        result = result.replaceAll("\\[spoiler\\](.*?)\\[/spoiler\\]", "<span class='spoiler'>$1</span>");

        //final replacement greentext
        result = result.replaceAll("\\[greentext\\](.*?)\\[/greentext\\]", "<span class='greentext'>$1</span>");

        //final replacement blockquote
        result = result.replaceAll("\\[blockquote\\](.*?)\\[/blockquote\\]", "<blockquote>$1</blockquote>");

        //final replacement redtxt
        result = result.replaceAll("\\[redtext\\](.*?)\\[/redtext\\]", "<span class='redtext'><strong>$1</strong></span>");

        return result;
    }
}
