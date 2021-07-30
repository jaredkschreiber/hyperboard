package com.assemblynext.hyperboard.utilities;

import lombok.Value;

@Value
public class SiteConfig{
    
    //time trap secure password
    final String timeCodePass = "81nczkkaah1";

    //number of seconds after loading a page that user must wait to reply (anti spam)
    final Integer replyMinSpeed = 3;

    //number of entries per catalog page
    final Integer catalogSize = 24;

    //salt for hash
    final String tripHash = "xqEDbBFYn3";

    //max expiration date, default 90 days
    final Integer maxEntryExpirationDateDays = 90;
}
