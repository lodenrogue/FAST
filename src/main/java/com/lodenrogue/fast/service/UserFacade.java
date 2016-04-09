package com.lodenrogue.fast.service;

import java.util.HashMap;
import java.util.Map;

import com.lodenrogue.fast.model.User;

public class UserFacade extends AbstractFacade<User> {

	public UserFacade() {
		super(User.class);
	}

	public User findByName(String name) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", name);
		return findUnique("FROM User WHERE name = :name", parameters);
	}

}
