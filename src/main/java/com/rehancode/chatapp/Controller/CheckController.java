package com.rehancode.chatapp.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/check")
public class CheckController {

    @GetMapping("/test")
    public String check(){
        return "Hello from spring boot";
    }


}
