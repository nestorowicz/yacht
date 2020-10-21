package com.github.nestorowicz.yacht.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestVoteResponse {

    public static final String TERM = "term";
    public static final String VOTE_GRANTED = "voteGranted";

    private Integer term;
    private Boolean voteGranted;

    @JsonCreator
     public RequestVoteResponse(@JsonProperty(TERM) Integer term, @JsonProperty(VOTE_GRANTED) Boolean voteGranted) {
        this.term = term;
        this.voteGranted = voteGranted;
    }

    public Integer getTerm() {
        return term;
    }

    public Boolean getVoteGranted() {
        return voteGranted;
    }
}
