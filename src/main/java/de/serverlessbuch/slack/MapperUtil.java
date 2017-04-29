package de.serverlessbuch.slack;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.experimental.UtilityClass;

import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@UtilityClass
public class MapperUtil {
    public TypeReference<Map<String, Object>> mapTypeReference = new TypeReference<Map<String, Object>>() {};
}
