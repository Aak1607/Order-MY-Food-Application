package com.restaurantsearchservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurantsearchservice.model.DeliveryInfoResponse;
import com.restaurantsearchservice.model.FoodDetails;
import com.restaurantsearchservice.model.FoodMenuResponse;
import com.restaurantsearchservice.model.RestaurantDetailResponse;
import com.restaurantsearchservice.model.RestaurantModel;
import com.restaurantsearchservice.model.RestaurantsResponse;
import com.restaurantsearchservice.service.RestaurantSearchServiceInterface;
import com.restaurantsearchservice.vo.AreaSearchParams;
import com.restaurantsearchservice.vo.CoOrdinateSearchParams;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/restaurants")
public class Controller {
	private static final Logger logger = LoggerFactory.getLogger(Controller.class);

	@Autowired
	private RestaurantSearchServiceInterface service;

	@GetMapping(value = "/search/{area}", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiOperation(value = "search restaurants by area", response = RestaurantsResponse.class)
	public RestaurantsResponse searchRestaurantByArea(@PathVariable String area,
			@RequestParam(required = false) String name, @RequestParam(defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "0") Float rating,
			@RequestParam(required = false, defaultValue = "0") Float budget,
			@RequestParam(required = false) String cuisine) {
		if (logger.isDebugEnabled()) {
			logger.debug("searching restaurant with area=" + area + " with name=" + name + ",page=" + page + ",rating="
					+ rating + ",budget=" + budget + ",cuisine=" + cuisine);
		}

		AreaSearchParams params = new AreaSearchParams();
		params.setArea(area);
		params.setBudget(budget);
		params.setCuisine(cuisine);
		params.setPage(page);
		params.setRestaurantName(name);
		params.setRating(rating);

		Page<RestaurantModel> data = service.getRestaurantByAreaAndFilterParam(params);

		if (data != null) {
			createLinksForRestaurant(data.getContent());
		}
		return createRestaurantResponse(data);
	}

	@GetMapping(path = "/search/{latitude}/{longitude}", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiOperation(value = "search restaurants by coordinates", response = RestaurantsResponse.class)
	public RestaurantsResponse searchRestaurantByCoordinates(@PathVariable Double latitude,
			@PathVariable Double longitude, @RequestParam(required = false) String name,
			@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "0") Float rating,
			@RequestParam(required = false, defaultValue = "0") Float budget,
			@RequestParam(required = false) String cuisine,
			@RequestParam(required = false, defaultValue = "1") Float distance) {
		if (logger.isDebugEnabled()) {
			logger.debug("searching restaurant with latitude,longitude=" + latitude + "," + longitude + " with name="
					+ name + ",page=" + page + ",rating=" + rating + ",budget=" + budget + ",cuisine=" + cuisine);
		}

		CoOrdinateSearchParams params = new CoOrdinateSearchParams();
		params.setLatitude(latitude);
		params.setLongitude(longitude);
		params.setBudget(budget);
		params.setCuisine(cuisine);
		params.setPage(page);
		params.setRestaurantName(name);
		params.setRating(rating);
		params.setDistance(distance);
		
		Page<RestaurantModel> data = service.getRestaurantByLocationAndFilterParam(params);
		if (data != null) {
			createLinksForRestaurant(data.getContent());
		}

		return createRestaurantResponse(data);
	}

	@GetMapping(value = "{restaurant_id}", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiOperation(value = "get restaurant details", response = RestaurantsResponse.class)
	public RestaurantDetailResponse getRestaurantDetailsById(@PathVariable("restaurant_id") String restaurantId) {
		if (logger.isDebugEnabled()) {
			logger.debug("getting restaurant details of restaurant id=" + restaurantId);
		}
		RestaurantModel data = service.getResaurantById(restaurantId);
		if (data != null) {
			Link getAllFood = ControllerLinkBuilder.linkTo(ControllerLinkBuilder
					.methodOn(Controller.class).getFoodDetailsByRestaurantId(data.getRestaurantId()))
					.withRel("FoodMenu");
			data.add(getAllFood);
		}
		return createRestaurantDetailResponse(data);
	}


	@GetMapping(value="/{restaurant_id}/validate/{latitude}/{longitude}", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiOperation(value = "validate whether restaurant serve in that area or not", response = RestaurantsResponse.class)
	public DeliveryInfoResponse validateDeliveryAddress(@PathVariable("restaurant_id") String restaurantId,
			@PathVariable Double latitude, @PathVariable Double longitude) {
		if (logger.isDebugEnabled()) {
		}
		boolean data = service.validateDeliveryAddress(restaurantId, latitude, longitude);
		DeliveryInfoResponse status = new DeliveryInfoResponse();
		if (data == false) {
			if (logger.isDebugEnabled()) {
				logger.debug("No Data Available");
			}
			status.setMessage("Delivery is not available for your area");
			status.setStatusCode(401);
			status.setStatus("SUCCESS");
			status.setResult(data);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Creating Success Response");
			}
			status.setMessage("Delivery is available for your area");
			status.setStatusCode(200);
			status.setStatus("SUCCESS");
			status.setResult(data);
		}
		return status;
	}

	@GetMapping(value="/{restaurant_id}/menu", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiOperation(value = "getting food menu of a particular restaurant", response = RestaurantsResponse.class)
	public FoodMenuResponse getFoodDetailsByRestaurantId(@PathVariable("restaurant_id") String restaurantId) {
		if (logger.isDebugEnabled()) {
			logger.debug("getting food details of restaurant id=" + restaurantId);
		}
		List<FoodDetails> data = service.getAllFoodDetailsByRestaurantId(restaurantId);

		return createResponseForFoods(data);
	}

	@PutMapping(value="/{restaurant_id}/reviews/{rating}", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ApiOperation(value = "update the rating of a restaurant", response = RestaurantsResponse.class)
	public RestaurantDetailResponse updateRatingDetailOfRestaurant(@PathVariable("restaurant_id") String restaurantId,
			@PathVariable("rating") float rating) {
		if (logger.isDebugEnabled()) {
			logger.debug("getting food details of restaurant id=" + restaurantId + " , rating=" + rating);
		}

		RestaurantModel resObj = service.updateRatingBasedOnRestaurantId(restaurantId, rating);
		return createRestaurantDetailResponse(resObj);

	}

	private List<RestaurantModel> createLinksForRestaurant(List<RestaurantModel> data) {
		for (RestaurantModel r : data) {
			String restaurantId = r.getRestaurantId();
			Link selfLink = ControllerLinkBuilder.linkTo(Controller.class).slash(restaurantId)
					.withSelfRel();
			Link getAllFood = ControllerLinkBuilder.linkTo(ControllerLinkBuilder
					.methodOn(Controller.class).getFoodDetailsByRestaurantId(restaurantId))
					.withRel("FoodMenu");
			r.add(selfLink, getAllFood);
		}
		return data;
	}

	

	private RestaurantsResponse createRestaurantResponse(Page<RestaurantModel> data) {
		RestaurantsResponse responseStatus = new RestaurantsResponse();
		if (data != null && !data.getContent().isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Creating Success Response");
			}
			responseStatus.setStatusCode(200);
			responseStatus.setStatus("SUCCESS");
			responseStatus.setData(data.getContent());
			responseStatus.setPageNo(data.getNumber());
			responseStatus.setPageSize(data.getSize());
			responseStatus.setTotalpages(data.getTotalPages());
			responseStatus.setTotalElements(data.getNumberOfElements());

		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("No Data Available");
			}
			responseStatus.setStatusCode(401);
			responseStatus.setStatus("SUCCESS");
			responseStatus.setMessage("No Data Found");
		}
		return responseStatus;
	}

	private RestaurantDetailResponse createRestaurantDetailResponse(RestaurantModel data) {
		RestaurantDetailResponse responseStatus = new RestaurantDetailResponse();
		if (data != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Creating Success Response");
			}
			responseStatus.setStatusCode(200);
			responseStatus.setStatus("SUCCESS");
			responseStatus.setData(data);

		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("No Data Available");
			}
			responseStatus.setStatusCode(401);
			responseStatus.setStatus("FAILURE");
			responseStatus.setMessage("No Data Found");
		}
		return responseStatus;
	}

	private FoodMenuResponse createResponseForFoods(List<FoodDetails> data) {
		FoodMenuResponse responseStatus = new FoodMenuResponse();
		if (!data.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Creating Success Response");
			}
			responseStatus.setData(data);
			responseStatus.setStatusCode(200);
			responseStatus.setStatus("SUCCESS");
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("No Data Available");
			}
			responseStatus.setStatusCode(401);
			responseStatus.setStatus("SUCCESS");
			responseStatus.setMessage("No Data Found");
		}
		return responseStatus;
	}

}
