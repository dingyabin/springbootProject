package net.wecash.dingyabin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by dingyabin on 2017/11/8.
 */
@Controller
public class DispacherController {

    @RequestMapping("/")
    public String index(){
        return "forward:/addService.html";
    }



}
