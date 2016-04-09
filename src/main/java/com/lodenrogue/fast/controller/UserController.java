package com.lodenrogue.fast.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lodenrogue.fast.error.ErrorMessage;
import com.lodenrogue.fast.error.MissingFieldsError;
import com.lodenrogue.fast.model.ProductBacklogItem;
import com.lodenrogue.fast.model.User;
import com.lodenrogue.fast.service.ProductBacklogItemFacade;
import com.lodenrogue.fast.service.UserFacade;

@RestController
@RequestMapping(path = "/api/v1")
public class UserController {

	@RequestMapping(path = "/users", method = RequestMethod.POST)
	public HttpEntity<Object> createUser(@RequestBody User user) {
		List<String> missingFields = getMissingFields(user);

		// Check missing fields
		if (missingFields.size() > 0) {
			return new ResponseEntity<Object>(new MissingFieldsError(missingFields), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Check that user doesn't already exist in database
		if (new UserFacade().findByName(user.getName()) != null) {
			return new ResponseEntity<Object>(new ErrorMessage("User with that name already exists"), HttpStatus.CONFLICT);
		}

		// Create user
		else {
			user = new UserFacade().create(user);
			user.add(createLinks(user));
			return new ResponseEntity<Object>(user, HttpStatus.CREATED);
		}
	}

	@RequestMapping(path = "/users/{id}", method = RequestMethod.GET)
	public HttpEntity<Object> getUser(@PathVariable long id) {
		User user = new UserFacade().find(id);
		if (user == null) {
			return new ResponseEntity<Object>(new ErrorMessage("No user with id " + id + " found"), HttpStatus.NOT_FOUND);
		}
		else {
			user.add(createLinks(user));
			return new ResponseEntity<Object>(user, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/users/{id}/product-backlog", method = RequestMethod.GET)
	public HttpEntity<Object> getProductBacklogItems(@PathVariable long id) {
		if (!userExists(id)) {
			return new ResponseEntity<Object>(new ErrorMessage("No user with id " + id + " found"), HttpStatus.NOT_FOUND);
		}
		else {
			List<ProductBacklogItem> items = new ProductBacklogItemFacade().findAllByUser(id);
			for (ProductBacklogItem i : items) {
				i.add(new ProductBacklogController().createLinks(i));
			}
			return new ResponseEntity<Object>(items, HttpStatus.OK);
		}
	}

	public List<Link> createLinks(User user) {
		List<Link> links = new ArrayList<Link>();
		links.add(linkTo(methodOn(UserController.class).getUser(user.getEntityId())).withSelfRel());

		// Add product backlog items
		if (new ProductBacklogItemFacade().findAllByUser(user.getEntityId()).size() > 0) {
			links.add(linkTo(methodOn(UserController.class).getProductBacklogItems(user.getEntityId())).withRel("product-backlog"));
		}
		return links;
	}

	private boolean userExists(long id) {
		User user = new UserFacade().find(id);
		if (user == null) return false;
		return true;
	}

	private List<String> getMissingFields(User user) {
		List<String> missingFields = new ArrayList<String>();
		if (user.getName() == null) missingFields.add("name");
		return missingFields;
	}

}
