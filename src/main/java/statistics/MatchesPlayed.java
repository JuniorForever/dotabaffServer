package statistics;

import java.io.Serializable;

public class MatchesPlayed implements Serializable {
    private Long matchID;
    private Integer playerSlot;
    private Integer lobbyType;
    private Boolean radiantWin;
    private Integer duration;
    private Integer heroId;
    private Integer kills;
    private Integer deaths;
    private Integer assists;
    private static Boolean needMatches = true;

    public static Boolean getNeedMatches() {
        return needMatches;
    }

    public static void setNeedMatches(Boolean needMatches) {
        MatchesPlayed.needMatches = needMatches;
    }

    public void setPlayerSlot(Integer playerSlot) {
        this.playerSlot = playerSlot;
    }

    public Integer getPlayerSlot() {
        return playerSlot;
    }

    public Long getMatchID() {
        return matchID;
    }

    public void setMatchID(Long matchID) {
        this.matchID = matchID;
    }

    public Integer getLobbyType() {
        return lobbyType;
    }

    public void setLobbyType(Integer lobbyType) {
        this.lobbyType = lobbyType;
    }

    public Boolean getRadiantWin() {
        return radiantWin;
    }

    public void setRadiantWin(Boolean radiantWin) {
        this.radiantWin = radiantWin;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getHeroId() {
        return heroId;
    }

    public void setHeroId(Integer heroId) {
        this.heroId = heroId;
    }

    public Integer getKills() {
        return kills;
    }

    public void setKills(Integer kills) {
        this.kills = kills;
    }

    public Integer getDeaths() {
        return deaths;
    }

    public void setDeaths(Integer deaths) {
        this.deaths = deaths;
    }

    public Integer getAssists() {
        return assists;
    }

    public void setAssists(Integer assists) {
        this.assists = assists;
    }
}
