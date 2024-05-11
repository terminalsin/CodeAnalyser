package com.codeanalyser.shop.gpt.serializer;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class CodeReviewDTO {
    @SerializedName("id")
    private UUID id;

    @SerializedName("name")
    private String name;

    @SerializedName("review")
    private List<CodeResultDTO> review;

    @SerializedName("score")
    private int score;
}


