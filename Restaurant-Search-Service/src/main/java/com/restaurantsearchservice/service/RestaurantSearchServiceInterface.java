package com.restaurantsearchservice.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.restaurantsearchservice.model.FoodDetails;
import com.restaurantsearchservice.model.RestaurantModel;
import com.restaurantsearchservice.vo.AreaSearchParams;
import com.restaurantsearchservice.vo.CoOrdinateSearchParams;

public interface RestaurantSearchServiceInterface {
         
         public Page<RestaurantModel> getRestaurantByAreaAndFilterParam(AreaSearchParams areaParams);
         //public Page<RestaurantModel> getRestaurantByArea(String location,int pageNo);
         public Page<RestaurantModel> getRestaurantByLocationAndFilterParam(CoOrdinateSearchParams coOrdinateParams);
         public Page<RestaurantModel> getRestaurantByLocation(CoOrdinateSearchParams coOrdinateParamsr);
         public RestaurantModel getResaurantById(String  resId);
         //public RestaurantModel getRestaurantReviews(long resId,long pageNo);
         public FoodDetails getFoodDetailsOfARestuarant(String  resId,String foodId);
         public boolean validateDeliveryAddress(String resId, double latitude, double longitude);
         public List<FoodDetails> getAllFoodDetailsByRestaurantId(String resId);
         public RestaurantModel updateRatingBasedOnRestaurantId(String resId,float rating);
         
         
         
}