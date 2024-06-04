package com.grupo6.votingapp.dtos.stats;

import java.util.List;

import com.grupo6.votingapp.dtos.questions.QuestionStats;
import com.grupo6.votingapp.dtos.users.UsersWithNoRelationsDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VotingStatsDTO {
    private Long numvotes;
    private List<QuestionStats> questionsstats;
    private List<UsersWithNoRelationsDTO> voters;
}
