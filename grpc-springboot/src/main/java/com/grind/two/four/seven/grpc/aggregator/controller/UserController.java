package com.grind.two.four.seven.grpc.aggregator.controller;

import com.grind.two.four.seven.grpc.aggregator.service.UserRestService;
import com.grind.two.four.seven.grpc.user.UserInformation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {

    private final UserRestService userRestService;

    public UserController(final UserRestService userRestService) {
        this.userRestService = userRestService;
    }

    @GetMapping(value = "{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserInformation getUserInformation(final @PathVariable int userId) {
        return userRestService.getUserInformation(userId);
    }
}
