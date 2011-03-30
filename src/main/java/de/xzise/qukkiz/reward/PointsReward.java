package de.xzise.qukkiz.reward;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import nl.blaatz0r.Trivia.Trivia;

import org.bukkit.ChatColor;

import de.xzise.qukkiz.hinter.Answer;

public class PointsReward extends DefaultReward<PointsRewardSettings> {

    private final Trivia trivia;

    public PointsReward(PointsRewardSettings settings, Trivia trivia) {
        super(settings);
        this.trivia = trivia;
    }

    @Override
    public void reward(Answer answer) {
        int points = Math.max(0, this.getSettings().start - (answer.hint * this.getSettings().decrease));
        int total = points;

        try {
            Connection conn = this.trivia.getDb().getConnection();
            Statement stat = conn.createStatement();
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS scores (name, score);");

            ResultSet rs = stat.executeQuery("SELECT * FROM scores WHERE name = '" + answer.player.getName() + "';");
            if (rs.next()) {
                total += rs.getInt("score");
                stat.execute("UPDATE scores SET score = " + total + " WHERE name = '" + answer.player.getName() + "';");
            } else {
                PreparedStatement prep = conn.prepareStatement("INSERT INTO scores VALUES (?, ?);");
                prep.setString(1, answer.player.getName());
                prep.setInt(2, points);
                prep.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        answer.player.sendMessage(ChatColor.WHITE + "You scored " + ChatColor.GREEN + points + ChatColor.WHITE + " points! (" + ChatColor.GREEN + total + ChatColor.WHITE + " points total).");
    }
}
