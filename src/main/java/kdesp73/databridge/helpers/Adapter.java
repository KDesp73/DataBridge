package kdesp73.databridge.helpers;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Adapter {

    /**
     * Maps a ResultSet to a List of objects of the specified class type.
     * @param <T> The type of the object to map to.
     * @param resultSet The ResultSet to map.
     * @param clazz The class type to map the ResultSet to.
     * @return A List of objects of the specified class type.
     * @throws SQLException If there is an issue accessing the ResultSet.
     * @throws IllegalAccessException If there is an issue accessing class fields.
     * @throws InstantiationException If the class cannot be instantiated.
     */
    public static <T> List<T> load(ResultSet resultSet, Class<T> clazz) throws SQLException, IllegalAccessException, InstantiationException {
        List<T> resultList = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();

        while (resultSet.next()) {
            T object = clazz.newInstance(); 
			
            for (Field field : fields) {
                String fieldName = field.getName();
                try {
                    Object value = resultSet.getObject(fieldName); // TODO: also work with snake to camel case
                    
                    field.setAccessible(true);
                    
                    field.set(object, value);
                } catch (SQLException e) {
					SQLogger.getLogger().log(null, e);
				}
            }

            resultList.add(object); // Add the object to the result list
        }

        return resultList;
    }
}
