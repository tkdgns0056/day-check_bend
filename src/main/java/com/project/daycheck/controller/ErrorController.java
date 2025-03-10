package com.project.daycheck.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class ErrorController {

    @GetMapping
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleError() {
        return "404 Not Found - 요청하신 페이지를 찾을 수 없습니다.";
    }
}
