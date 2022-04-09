package com.pp.arithmo.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.pp.arithmo.enums.ComplexityType;
import com.pp.arithmo.enums.ProblemType;
import com.pp.arithmo.models.Collection;

import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CollectionService {
	
	private HashMap<Integer, Collection> collectionMap = new HashMap<Integer, Collection>();
	
	private final String COLLECTIONS_JSON = "/Collections.json";
	private final String COLLECTIONS = "collections";
	private final String ID = "id";
	private final String CTYPE = "cType";
	private final String PTYPE = "pType";
	private final String NAME = "name";
	private final String ACTIVE = "active";
	
	
	
	CollectionService() throws URISyntaxException{
		populateCollections();
	}
	
	private void populateCollections() throws URISyntaxException {
		try {
			InputStream is = getClass().getResourceAsStream(COLLECTIONS_JSON);
			String json = new BufferedReader(
				      		new InputStreamReader(is, StandardCharsets.UTF_8))
				        	.lines()
				        	.collect(Collectors.joining("\n"));
			System.out.println("collections;"+json);
			JsonObject problemsJson = new JsonObject(json);
			
			problemsJson.getJsonArray(COLLECTIONS).forEach(m -> {
				JsonObject p = (JsonObject) m;
				Collection c = Collection.builder().id(Integer.parseInt(p.getString(ID)))
													.cType(p.getString(CTYPE).isEmpty() ? null : ComplexityType.get(Integer.parseInt(p.getString(CTYPE))))
													.pType(p.getString(PTYPE).isEmpty() ? null : ProblemType.get(Integer.parseInt(p.getString(PTYPE))))
													.name(p.getString(NAME))
													.active(p.containsKey(ACTIVE) ? p.getBoolean(ACTIVE) : true)
													.build();
				collectionMap.put(c.getId(), c);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public JsonObject getCollections() {
		return JsonObject.mapFrom(collectionMap);
	}
}




