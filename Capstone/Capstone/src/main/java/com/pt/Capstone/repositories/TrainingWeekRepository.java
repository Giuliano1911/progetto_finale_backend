package com.pt.Capstone.repositories;

import com.pt.Capstone.entities.Customer;
import com.pt.Capstone.entities.TrainingWeek;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingWeekRepository extends JpaRepository<TrainingWeek, Long> {

    List<TrainingWeek> findByCustomer(Customer customer);
}
