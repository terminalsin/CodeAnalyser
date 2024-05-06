package com.codeanalyser.shop.gpt.serializer;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CodeReviewDTO {
    @SerializedName("review")
    private List<CodeResultDTO> review;

    @SerializedName("score")
    private int score;

    // Getters and setters
    public List<CodeResultDTO> getReview() {
        return review;
    }

    public void setReview(List<CodeResultDTO> review) {
        this.review = review;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}


