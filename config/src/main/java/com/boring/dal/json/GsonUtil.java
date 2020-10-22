package com.boring.dal.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class GsonUtil {

	public static <T> T fromJson(String json, Class<T> clasz) {
		try {
			return getGson().fromJson(json, clasz);
		}catch (Exception e){
			throw e;
		}
	}

	public static <T> T fromJson(String json, Type type) {
		try {
			return (T) getGson().fromJson(json, type);
		}catch (Exception e){
			throw e;
		}
	}

	public static String toJson(Object src) {
		return getGson().toJson(src);
	}
	
	public static String toJson(Object src, Type srcType) {
		return getGson().toJson(src, srcType);
	}
	
	public static String toJsonWithoutNull(Object src, Type srcType) {
		Gson gson = new GsonBuilder()
	    .enableComplexMapKeySerialization()
	    .disableHtmlEscaping()
	    .addSerializationExclusionStrategy(new ExcludedExclusionStrategy()) //包含@Excluded声明的字段将不会被序列化成json
	    .create();
		
		return gson.toJson(src, srcType);
	}
	
	public static String toJsonEscapeHtml(Object src, Type srcType) {
		Gson gson = new GsonBuilder()
	    .enableComplexMapKeySerialization()
	    .create();
		
		return gson.toJson(src, srcType);
	}

	public static Object get(Object key, String snapshot) {
		if (null == key || StringUtils.isBlank(snapshot)) {
			return null;
		}
		Map<Object, Object> map = null;
		try {
			map = getGson().fromJson(snapshot, new TypeToken<Map<Object, Object>>() { }.getType());
		} catch (Exception ex) {

		}
		if (map==null||map.isEmpty()) {
			return null;
		}
		return map.get(key);
	}

	public static String put(String snapshot, Object key, Object value) {
		Map<Object, Object> map = getGson().fromJson(snapshot, new TypeToken<Map<Object, Object>>() {}.getType());
		map.put(key, value);
		return getGson().toJson(map);
	}
	
	/**
	 * 目前只能判断一层
	 * @param str
	 * @return
	 */
	public static boolean maybeJson(String str) {
		if (StringUtils.isEmpty(str)) return false;
		
		//先判断是否带{},减少Exception次数
		if (StringUtils.containsNone(str, "{") || StringUtils.containsNone(str, "}")) {
			return false;
		}
		
		try {
			getGson().fromJson(str, new TypeToken<Map<Object, Object>>(){}.getType());
			return true;
		} catch (Exception ex){
			return false;
		}
	}

	/**
	 * gson no good, because of converting all number to double, eg. 1 => 1.0
	 * @param json
	 * @return
	 */
	public static Map<String, Object> convertJsonToMap(String json) {
		HashMap<String, Object> map = new HashMap<String, Object>();		
		try {
			map = getGson().fromJson(json, new TypeToken<HashMap<String, Object>>() {}.getType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return map;
	}

	private static Gson getGson() {
		 Gson gson = new GsonBuilder()
		    .enableComplexMapKeySerialization()
//		    .serializeNulls()
		    .disableHtmlEscaping()
		    .addSerializationExclusionStrategy(new ExcludedExclusionStrategy()) //包含@Excluded声明的字段将不会被序列化成json
//		    .setPrettyPrinting() //不需要\n,缩进等拍版需要
		    .create();
		return gson;
	}
	
}
