package com.restaurantsearchservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.restaurantsearchservice.model.FoodDetails;

public interface FoodDetailsSearchRepository extends ElasticsearchRepository<FoodDetails, String> {

	// search all food menu of a single restaurant using restaurant id
	@Query("{\"match\":{\"restaurant_id\":\"?0\"}}")
	public List<FoodDetails> getFoodDetailsByRestaurantId(String resId);
	
	// search single food detail of a single restaurant using restaurant id and food id
	@Query("{\"bool\":{\"must\":[{\"match\":{\"restaurant_id\":\"?0\"}},{\"match\":{\"foodId\":\"?1\"}}]}}")
	public FoodDetails getFoodDetailsByRestaurantIdAndFoodId(String resId, String foodId);
	
}