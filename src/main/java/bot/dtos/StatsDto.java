package bot.dtos;

public class StatsDto
{
    private PlayerDto player;
    private float elo;
    private int won;
    private int lost;
    private int drawn;

    public PlayerDto getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDto player) {
        this.player = player;
    }

    public float getElo() {
        return elo;
    }

    public void setElo(float elo) {
        this.elo = elo;
    }

    public int getWon() {
        return won;
    }

    public void setWon(int won) {
        this.won = won;
    }

    public int getLost() {
        return lost;
    }

    public void setLost(int lost) {
        this.lost = lost;
    }

    public int getDrawn() {
        return drawn;
    }

    public void setDrawn(int drawn) {
        this.drawn = drawn;
    }

    public float getWinRate() {
        int total = won + lost + drawn;
        if (total == 0) {
            return 0f;
        }
        return won / (float)(won + lost + drawn) * 100f;
    }
}
