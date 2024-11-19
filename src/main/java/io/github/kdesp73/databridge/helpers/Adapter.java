package io.github.kdesp73.databridge.helpers;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.github.kdesp73.databridge.helpers.SQLogger.LogLevel;
import io.github.kdesp73.databridge.helpers.SQLogger.LogType;

/**
 * The {@code Adapter} class provides a utility method for mapping a {@link ResultSet}
 * to a list of objects of a specified class type.
 * <p>
 * This class leverages reflection to map columns in the {@code ResultSet} to the fields
 * of the provided class. It supports field names in both camelCase and snake_case.
 * </p>
 *
 * <p>
 * The {@code load} method reads rows from a {@code ResultSet} and maps each row to
 * an instance of the provided class. If any issues are encountered during mapping, they are
 * logged using the {@link SQLogger}.
 * </p>
 *
 * <p>
 * The {@code toSnakeCase} method is used to convert camelCase field names to snake_case
 * column names, following typical database conventions.
 * </p>
 *
 * @author KDesp73
 */
public class Adapter {

	/**
	 * Private constructor to avoid external instantiation
	 */
	private Adapter() {}

    /**
     * Maps a {@link ResultSet} to a {@link List} of objects of the specified class type.
     * <p>
     * This method iterates over the rows of the {@code ResultSet}, creating an instance
     * of the provided class for each row. It uses reflection to match the column names
     * from the {@code ResultSet} to the fields of the class.
     * </p>
     *
     * @param <T> The type of the object to map to.
     * @param resultSet The {@code ResultSet} to map.
     * @param clazz The class type to map the {@code ResultSet} to.
     * @return A {@code List} of objects of the specified class type, populated with data
     *         from the {@code ResultSet}.
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
                        try {
                            value = resultSet.getObject(fieldName);
                        } catch (SQLException ex) {
                            continue;
                        }
                    }

                    field.setAccessible(true);
                    field.set(object, value);
                }

                resultList.add(object);
            }
        } catch (SQLException | InstantiationException | IllegalAccessException ex) {
            SQLogger.getLogger(LogLevel.ERRO, LogType.ALL).log(Config.getInstance().getLogLevel(), "Error while loading ResultSet into List<" + clazz.getName() + ">", ex);
        }

        return resultList;
    }

    /**
     * Converts a camelCase string to snake_case.
     * <p>
     * This method replaces uppercase letters with an underscore and the lowercase equivalent
     * (following the typical format for database column names).
     * </p>
     *
     * @param camelCase The camelCase string to convert.
     * @return A snake_case version of the input string.
     */
    private static String toSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
}
