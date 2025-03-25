package com.pt.Capstone.services;

import com.pt.Capstone.entities.Customer;
import com.pt.Capstone.entities.DietDay;
import com.pt.Capstone.entities.DietWeek;
import com.pt.Capstone.entities.Meal;
import com.pt.Capstone.repositories.CustomerRepository;
import com.pt.Capstone.repositories.DietDayRepository;
import com.pt.Capstone.repositories.DietWeekRepository;
import com.pt.Capstone.repositories.MealRepository;
import com.pt.Capstone.requests.DietWeekNameRequest;
import com.pt.Capstone.responses.CustomerResponse;
import com.pt.Capstone.responses.DietWeekResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class DietWeekService {
    private final DietWeekRepository dietWeekRepository;
    private final DietDayRepository dietDayRepository;
    private final MealRepository mealRepository;
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;

    private DietWeekResponse DietWeekResponseFromDietWeek(DietWeek dietWeek) {
        DietWeekResponse dietWeekResponse = new DietWeekResponse();
        BeanUtils.copyProperties(dietWeek, dietWeekResponse);
        CustomerResponse customerResponse = customerService.customerResponseFromEntity(customerRepository.findById(dietWeek.getCustomer().getId()).orElseThrow(() -> new EntityNotFoundException("Customer with id: " + dietWeek.getCustomer().getId() + " not found")));
        dietWeekResponse.setCustomerResponse(customerResponse);
        dietWeekResponse.setDietDays(dietDayRepository.findAllById(dietWeek.getDietDays().stream().map(DietDay::getId).toList()));
        return dietWeekResponse;
    }

    public DietWeekResponse findById(Long id) {
        DietWeek dietWeek = dietWeekRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("DietWeek with id: " + id + " not found"));
        return DietWeekResponseFromDietWeek(dietWeek);
    }

    public DietWeekResponse save(Long customerId, @Valid DietWeekNameRequest dietWeekNameRequest) {
        DietWeek dietWeek = new DietWeek();
        BeanUtils.copyProperties(dietWeekNameRequest, dietWeek);
        dietWeek.setCustomer(customerRepository.findById(customerId).orElseThrow(() -> new EntityNotFoundException("Customer with id: " + customerId + " not found")));

        DietDay dietDay1 = new DietDay();

        Meal meal1 = new Meal();
        meal1.setName("Breakfast");
        mealRepository.save(meal1);

        Meal meal2 = new Meal();
        meal2.setName("Lunch");
        mealRepository.save(meal2);

        Meal meal3 = new Meal();
        meal3.setName("Dinner");
        mealRepository.save(meal3);

        Meal meal4 = new Meal();
        meal4.setName("Snack");
        mealRepository.save(meal4);

        dietDay1.setMeals(List.of(meal1, meal2, meal3, meal4));
        dietDay1.setDay("Rest Day");
        dietDayRepository.save(dietDay1);

        DietDay dietDay2 = new DietDay();

        Meal meal5 = new Meal();
        meal5.setName("Breakfast");
        mealRepository.save(meal5);

        Meal meal6 = new Meal();
        meal6.setName("Lunch");
        mealRepository.save(meal6);

        Meal meal7 = new Meal();
        meal7.setName("Dinner");
        mealRepository.save(meal7);

        Meal meal8 = new Meal();
        meal8.setName("Snack");
        mealRepository.save(meal8);

        dietDay2.setMeals(List.of(meal5, meal6, meal7, meal8));
        dietDay2.setDay("Workout Day");
        dietDayRepository.save(dietDay2);

        dietWeek.getDietDays().add(dietDay1);
        dietWeek.getDietDays().add(dietDay2);
        dietWeekRepository.save(dietWeek);
        return DietWeekResponseFromDietWeek(dietWeek);
    }

    public List<DietWeekResponse> findByCustomer(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Customer with id: " + id + " not found"));
        List<DietWeek> dietWeeks = dietWeekRepository.findByCustomer(customer);
        return dietWeeks.stream().map(this::DietWeekResponseFromDietWeek).toList();
    }

    public void delete(Long id) {
        dietWeekRepository.deleteById(id);
    }
}
