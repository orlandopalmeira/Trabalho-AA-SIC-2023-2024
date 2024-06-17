package com.grupo6.votingapp.dtos.stats;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VotingStatsDTO {
    private Long numvotes;
    private List<QuestionStats> questionsstats;
    private List<UserStats> voters;
    private List<UserSelectedOptions> userselectedoptions;
}
