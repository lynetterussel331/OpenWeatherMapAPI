package com.openweathermap.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openweathermap.api.model.Weather;

public interface OWMRepository extends JpaRepository<Weather, Long> {

}
