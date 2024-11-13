package com.nntk.nba.widgets.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameInfo {

    private String homeTeam;
    private String guestTeam;

    private String homeRate;
    private String guestRate;


    private TeamEntity homeTeamEntity;
    private TeamEntity guestTeamEntity;

}
