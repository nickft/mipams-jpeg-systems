package org.mipams.jpeg360.entities.validator;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class ValidatorUtils {

    public static List<Statement> getRdfBagContents(Resource rdfBagParentResource, Model jpeg360Model) {
        return jpeg360Model
            .getBag(rdfBagParentResource)
            .listProperties()
            .toList()
            .stream()
            .filter(st -> 
                !st.getObject()
                    .toString()
                    .equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag"))
                    .collect(Collectors.toList()
            );
    }

    public static Optional<Statement> getOptionalValue(Model model, Resource sub, Property predicate) {
        StmtIterator schemaDescriptorIterator = model.listStatements(sub, predicate, (RDFNode) null);

        if (!schemaDescriptorIterator.hasNext()) {
            return Optional.empty();
        }

        return Optional.of(schemaDescriptorIterator.next());
    }

    public static void validatePropertyName(String propertyName, Set<String> occuredProperties,
            Set<String> allowedPropertyNames) throws Exception {

        if (occuredProperties.contains(propertyName)) {
            throw new Exception(String.format("Property or schema %s has already been specified", propertyName));
        }

        if (!allowedPropertyNames.contains(propertyName)) {
            throw new Exception(String.format(
                    "Property or schema %s is not supported for this schema. Allowed properties are: [%s]",
                    propertyName, String.join(",", allowedPropertyNames)));
        }

        occuredProperties.add(propertyName);
    }

    public static void validatePropertyType(String propertyName, PropertyType expectedPropertyType, String propertyType)
            throws Exception {
        if (!expectedPropertyType.name().equalsIgnoreCase(propertyType)) {
            throw new Exception(
                    String.format("Property %s should be of type %s but it is defined as %s",
                            propertyName, expectedPropertyType.name(), propertyType));
        }
    }

    public static boolean isSchemaStatement(Model model, Resource resource) {
        return getSchemaStatement(model, resource).isPresent();
    }

    public static Optional<Statement> getSchemaStatement(Model model, Resource resource) {
        Property property = ResourceFactory.createProperty("http://ns.intel.com/umf/2.0schema");
        return ValidatorUtils.getOptionalValue(model, resource, property);
    }

    public static String getPropertyName(Model model, Resource resource) {
        Property property = ResourceFactory.createProperty("http://ns.intel.com/umf/2.0name");
        return ValidatorUtils
                .getOptionalValue(model, resource, property)
                .get().getObject().toString();
    }

    public static String getPropertyType(Model model, Resource resource) {
        Property property = ResourceFactory.createProperty("http://ns.intel.com/umf/2.0type");
        return ValidatorUtils
                .getOptionalValue(model, resource, property)
                .get().getObject().toString();
    }

    public static String getPropertyValue(Model model, Resource resource) {
        Property property = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#value");
        return ValidatorUtils
                .getOptionalValue(model, resource, property)
                .get().getObject().toString();
    }

}