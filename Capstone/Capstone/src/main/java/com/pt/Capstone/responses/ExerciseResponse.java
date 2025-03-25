package com.pt.Capstone.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseResponse {

    private Long id;

    private String name;

    private String description;

    private String muscleGroup;
}
