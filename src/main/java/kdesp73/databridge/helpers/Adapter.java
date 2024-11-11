package kdesp73.databridge.helpers;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import kdesp73.databridge.helpers.SQLogger.LogLevel;
import kdesp73.databridge.helpers.SQLogger.LogType;

public class Adapter {

	/**
	 * Maps a ResultSet to a List of objects of the specified class type.
	 *
	 * @param <T> The type of the object to map to.
	 * @param resultSet The ResultSet to map.
	 * @param clazz The class type to map the ResultSet to.
	 * @return A List of objects of the specified class type.
	 */
	public static <T> List<T> load(ResultSet resultSet, Class<T> clazz) {
		List<T> resultList = new ArrayList<>();

		Field[] fields = clazz.getDeclaredFields();

		try {
			while (resultSet.next()) {
				T object = clazz.newInstance();
				
				for (Field field : fields) {
					String fieldName = field.getName();
					String columnName = toSnakeCase(fieldName);
					
					Object value;
					
					try {
						value = resultSet.getObject(columnName);
					} catch (SQLException e) {
						value = resultSet.getObject(fieldName);
					}
					
					field.setAccessible(true);
					field.set(object, value);
					
				}
				
				resultList.add(object);
			}
		} catch (SQLException | InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
			SQLogger.getLogger(LogLevel.ERRO, LogType.ALL).log(Config.getInstance().getLogLevel(), "Error while loading ResultSet into List<" + clazz.getName() + ">", ex);
		}

		return resultList;
	}

	private static String toSnakeCase(String camelCase) {
		return camelCase.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
	}
}
