package com.lodenrogue.fast.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import com.lodenrogue.fast.service.ProductBacklogItemFacade;

@RestController
@RequestMapping(path = "/api/v1")
public class ProductBacklogController {

	@RequestMapping(path = "/product-backlog", method = RequestMethod.POST)
	public HttpEntity<Object> createItem(@RequestBody ProductBacklogItem item) {
		List<String> missingFields = getMissingFields(item);

		// Check missing fields
		if (missingFields.size() > 0) {
			return new ResponseEntity<Object>(new MissingFieldsError(missingFields), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Check that author exists in database
		ResponseEntity<Object> authorResponse = (ResponseEntity<Object>) new UserController().getUser(item.getAuthorId());
		if (authorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
			return new ResponseEntity<Object>(new ErrorMessage("No user with id " + item.getAuthorId() + " exists"), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Create the item
		else {
			item.setDateCreated(Calendar.getInstance());
			item.setPriority(new ProductBacklogItemFacade().findAll().size() + 1);
			item = new ProductBacklogItemFacade().create(item);
			return new ResponseEntity<Object>(item, HttpStatus.CREATED);
		}
	}

	@RequestMapping(path = "/product-backlog/{id}", method = RequestMethod.GET)
	public HttpEntity<Object> getItem(@PathVariable long id) {
		ProductBacklogItem item = new ProductBacklogItemFacade().find(id);
		if (item == null) {
			return new ResponseEntity<Object>(new ErrorMessage("No item with id " + id + " found"), HttpStatus.NOT_FOUND);
		}
		else {
			return new ResponseEntity<Object>(item, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/product-backlog", method = RequestMethod.GET)
	public HttpEntity<List<ProductBacklogItem>> getAllItems() {
		List<ProductBacklogItem> items = new ProductBacklogItemFacade().findAll();
		return new ResponseEntity<List<ProductBacklogItem>>(items, HttpStatus.OK);
	}

	@RequestMapping(path = "/product-backlog/{id}", method = RequestMethod.DELETE)
	public HttpEntity<Object> deleteItem(@PathVariable long id) {
		new ProductBacklogItemFacade().delete(id);
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	private List<String> getMissingFields(ProductBacklogItem item) {
		List<String> missingFields = new ArrayList<String>();
		if (item.getStory() == null) missingFields.add("story");
		if (item.getAuthorId() == 0) missingFields.add("authorId");
		return missingFields;
	}

}
