package com.image.controller;

import java.util.Base64;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.image.comare.Comparator;

/**
 * Created by daber on 29.08.16.
 */
@Controller
public class MainController {

    @ResponseBody
    @RequestMapping(value = "compare", method = RequestMethod.POST)
    public byte[] compare(@RequestParam("f0") MultipartFile fImg, @RequestParam(("f1")) MultipartFile sImg) {
        return Base64.getEncoder().encode(new Comparator().compare(fImg, sImg));
    }
}
