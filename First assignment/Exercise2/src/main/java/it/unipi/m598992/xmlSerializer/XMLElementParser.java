package it.unipi.m598992.xmlSerializer;

import it.unipi.m598992.xmlSerializer.annotations.XMLable;
import it.unipi.m598992.xmlSerializer.annotations.XMLfield;
import it.unipi.m598992.xmlSerializer.exception.XMLSerializationException;
import it.unipi.m598992.xmlSerializer.xmlModel.XMLClassElement;
import it.unipi.m598992.xmlSerializer.xmlModel.XMLElement;
import it.unipi.m598992.xmlSerializer.xmlModel.XMLFieldElement;
import it.unipi.m598992.xmlSerializer.xmlModel.XMLNoSerializableElement;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class XMLElementParser {

    public static XMLElement parseObjToXmlElement(Object obj) {
        Class<?> objClass = obj.getClass();
        XMLable classAnnotation = objClass.getAnnotation(XMLable.class);
        return classAnnotation != null ?
                createXMLClass(obj, objClass) :
                new XMLNoSerializableElement();
    }

    private static XMLClassElement createXMLClass(Object obj, Class<?> objClass) {
        XMLClassElement xmlClass = new XMLClassElement(objClass.getSimpleName());
        List<XMLElement> allXMLField = getAllXMLField(obj);
        xmlClass.addSubElements(allXMLField);
        return xmlClass;
    }

    private static List<XMLElement> getAllXMLField(Object obj) {
        Class<?> objClass = obj.getClass();
        Field[] declaredFields = objClass.getDeclaredFields();
        return Arrays.stream(declaredFields)
                .flatMap(field -> getXMLField(field, obj).stream())
                .toList();
    }

    private static Optional<XMLElement> getXMLField(Field field, Object obj) {
        XMLfield fieldAnnotation = field.getAnnotation(XMLfield.class);
        return Optional.ofNullable(fieldAnnotation)
                .map(annotation -> createXMLField(field, obj, annotation));
    }

    private static XMLFieldElement createXMLField(Field field, Object obj, XMLfield annotation) {
        field.setAccessible(true);
        Object fieldValue = getFieldValue(field, obj);
        return new XMLFieldElement(annotation.type(), getFieldName(field, annotation), fieldValue.toString());
    }

    private static Object getFieldValue(Field field, Object obj) {
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new XMLSerializationException(e.getMessage());
        }
    }

    private static String getFieldName(Field field, XMLfield fieldAnnotation) {
        return fieldAnnotation.name().isEmpty() ? field.getName() : fieldAnnotation.name();
    }
}