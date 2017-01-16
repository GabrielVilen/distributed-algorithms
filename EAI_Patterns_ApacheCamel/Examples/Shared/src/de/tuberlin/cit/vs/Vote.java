package de.tuberlin.cit.vs;

import java.io.Serializable;

public class Vote implements Serializable {
    private String voterId;
    private Boolean vote;

    public Vote(String voterId, Boolean vote) {
        this.voterId = voterId;
        this.vote = vote;
    }

    public String getVoterId() {
        return voterId;
    }

    public Boolean getVote() {
        return vote;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "voterId='" + voterId + '\'' +
                ", vote=" + vote +
                '}';
    }
}
