package com.pt.Capstone.responses;

import com.pt.Capstone.entities.Exercise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailedExerciseResponse {

    private Long id;

    private Integer reps;

    private Integer sets;

    private Integer rest;

    private Exercise exercise;
}
