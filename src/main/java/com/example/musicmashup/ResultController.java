package com.example.musicmashup;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResultController {
    @GetMapping("/greeting")
    public OurQueryResult greeting(@RequestParam(value = "mbid") String name) {
        return new OurQueryResult("Title", "mbid");
    }
}