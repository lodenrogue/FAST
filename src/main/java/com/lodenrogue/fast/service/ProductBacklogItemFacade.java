package com.lodenrogue.fast.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lodenrogue.fast.model.ProductBacklogItem;

public class ProductBacklogItemFacade extends AbstractFacade<ProductBacklogItem> {

	public ProductBacklogItemFacade() {
		super(ProductBacklogItem.class);
	}

	public List<ProductBacklogItem> findAllByUser(long userId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userId", userId);
		return findAllFromQuery("FROM ProductBacklogItem WHERE authorId = :userId", parameters);
	}

}
