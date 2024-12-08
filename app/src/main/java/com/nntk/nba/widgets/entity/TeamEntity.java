package com.nntk.nba.widgets.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamEntity {

    private String teamName;
    private String teamNameZh;

    private int simpleFrameSize;

    private String bgColor;

    private int movie2015FrameSize;

    private int movie2016FrameSize;


    private String scoreBoardColor;


    private String city;

}
