package com.nntk.nba.widgets.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentInfo {
    private TeamEntity currentMinTeam;

    private TeamEntity currentHourTeam;
}
