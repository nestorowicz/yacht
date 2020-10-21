package com.github.nestorowicz.yacht.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestVoteRequest {

    public static final String TERM = "term";
    public static final String CANDIDATE_ID = "candidateId";

    private Integer term;
    private String candidateId;

    @JsonCreator
    public RequestVoteRequest(@JsonProperty(TERM) Integer term, @JsonProperty(CANDIDATE_ID) String candidateId) {
        this.term = term;
        this.candidateId = candidateId;
    }

    public Integer getTerm() {
        return term;
    }

    public String getCandidateId() {
        return candidateId;
    }
}
