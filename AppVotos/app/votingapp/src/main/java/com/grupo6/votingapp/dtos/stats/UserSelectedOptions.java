package com.grupo6.votingapp.dtos.stats;

import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
public class UserSelectedOptions {
    private Long userid;
    private String username;
    private String email;
    private Long questionid;
    private List<Long> optionsids;
}