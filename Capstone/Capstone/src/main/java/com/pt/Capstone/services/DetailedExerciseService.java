package com.pt.Capstone.services;

import com.pt.Capstone.entities.DetailedExercise;
import com.pt.Capstone.entities.TrainingDay;
import com.pt.Capstone.repositories.DetailedExerciseRepository;
import com.pt.Capstone.repositories.ExerciseRepository;
import com.pt.Capstone.repositories.TrainingDayRepository;
import com.pt.Capstone.requests.DetailedExerciseRequest;
import com.pt.Capstone.responses.DetailedExerciseResponse;
import com.pt.Capstone.responses.EntityPostResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class DetailedExerciseService {
    private final DetailedExerciseRepository detailedExerciseRepository;
    private final TrainingDayRepository trainingDayRepository;
    private final ExerciseRepository exerciseRepository;

    public DetailedExerciseResponse DetailedExerciseResponseFromDetailedExercise(DetailedExercise detailedExercise) {
        DetailedExerciseResponse detailedExerciseResponse = new DetailedExerciseResponse();
        BeanUtils.copyProperties(detailedExercise, detailedExerciseResponse);
        if (detailedExercise.getExercise() != null)
            detailedExercise.setExercise(exerciseRepository.findById(detailedExercise.getExercise().getId()).get());
        return detailedExerciseResponse;
    }

    public DetailedExerciseResponse findById(Long id) {
        return DetailedExerciseResponseFromDetailedExercise(detailedExerciseRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("DetailedExercise with id: " + id + " not found")));
    }

    public EntityPostResponse save(Long id, @Valid DetailedExerciseRequest detailedExerciseRequest) {
        TrainingDay trainingDay = trainingDayRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("TrainingDay with id: " + id + " not found"));
        Long exerciseId = detailedExerciseRequest.getExerciseId();
        DetailedExercise detailedExercise= new DetailedExercise();
        BeanUtils.copyProperties(detailedExerciseRequest, detailedExercise);
        detailedExercise.setExercise(exerciseRepository.findById(exerciseId).orElseThrow(() -> new EntityNotFoundException("Exercise with id: " + exerciseId + " not found")));
        detailedExerciseRepository.save(detailedExercise);
        trainingDay.getDetailedExercises().add(detailedExercise);
        trainingDayRepository.save(trainingDay);
        return new EntityPostResponse(detailedExercise.getId());
    }

    public void update(Long id, @Valid DetailedExerciseRequest detailedExerciseRequest) {
        Long exerciseId = detailedExerciseRequest.getExerciseId();
        DetailedExercise detailedExercise = detailedExerciseRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("DetailedExercise with id: " + id + " not found"));
        detailedExercise.setExercise(exerciseRepository.findById(exerciseId).orElseThrow(() -> new EntityNotFoundException("Exercise with id: " + exerciseId + " not found")));
        detailedExercise.setReps(detailedExerciseRequest.getReps());
        detailedExercise.setSets(detailedExerciseRequest.getSets());
        detailedExercise.setRest(detailedExerciseRequest.getRest());
        detailedExerciseRepository.save(detailedExercise);
    }

    public void delete(Long id) {
        detailedExerciseRepository.deleteById(id);
    }
}
