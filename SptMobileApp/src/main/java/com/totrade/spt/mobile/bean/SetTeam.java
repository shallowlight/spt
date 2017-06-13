package com.totrade.spt.mobile.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/12/1.
 */
public class SetTeam {

    private String name;
    private List<String> teams;

    public void setName(String name) {
        this.name = name;
    }

    public String getSetname()
    {
        return name;
    }

    public void setTeams(List<String> teams) {
        this.teams = teams;
    }

    public List<String> getTeams() {
        return teams;
    }
}
