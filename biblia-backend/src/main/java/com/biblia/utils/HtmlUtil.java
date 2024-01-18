package com.biblia.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class HtmlUtil {
	private static final Logger log = LogManager.getLogger(HtmlUtil.class);
	public static String stripXSS(String value) {
        if (value != null) {
            // Avoid null characters
        	value = value.trim();
            value = value.replaceAll("", "");

            // Avoid anything between script tags
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid anything in a src='...' type of expression
            scriptPattern = Pattern.compile("src[\r\n\\s]*=[\r\n\\s]*'(.*?)'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            scriptPattern = Pattern.compile("src[\r\n\\s]*=[\r\n\\s]*\"(.*?)\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome </script> tag
            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome <script ...> tag
            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid eval(...) expressions
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid expression(...) expressions
            scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid javascript:... expressions
            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid vbscript:... expressions
            scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid onload= expressions
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            
//            value = HtmlUtils.htmlEscape(value,StandardCharsets.UTF_8.toString());
            value = convertBreakLine(value);
        }
        return value;
    }
	
	public static String convertBreakLine(String value){
		if(!StringUtils.isBlank(value)){
			value = value.replaceAll("(\r\n|\n)", "<br/>");
		}
		return value;
	}
	
	public static void validateRequest(Object obj){
		try{
			Field[] allFields = obj.getClass().getDeclaredFields();
			for (Field field : allFields) {
				field.setAccessible(true);
				if(field.getType() == String.class){
					Object value = field.get(obj);
					if(value != null){
						value = CommonUtils.removeSpace((String)value);
						value = stripXSS((String)value);
						field.set(obj, value);
					}
				}
			}
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
}
