package org.mipams.jpeg360.entities.validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.util.RDFContainers;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import static org.eclipse.rdf4j.model.util.Values.iri;

public class ValidatorUtils {

    public static ArrayList<Value> getRdfBagContents(Resource rdfBagParentResource, Model jlinkModel) {
        return RDFContainers.toValues(RDF.BAG, jlinkModel, rdfBagParentResource, new ArrayList<>());
    }

    public static Optional<Statement> getOptionalValue(Model model, Resource sub, IRI predicate) {
        Iterator<Statement> schemaDescriptors = model
                .getStatements(sub, predicate, null).iterator();

        if (!schemaDescriptors.hasNext()) {
            return Optional.empty();
        }

        return Optional.of(schemaDescriptors.next());
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
        return ValidatorUtils.getOptionalValue(model, resource, iri("http://ns.intel.com/umf/2.0schema"));
    }

    public static String getPropertyName(Model model, Resource resource) {
        return ValidatorUtils
                .getOptionalValue(model, resource, iri("http://ns.intel.com/umf/2.0name"))
                .get().getObject().stringValue();
    }

    public static String getPropertyType(Model model, Resource resource) {
        return ValidatorUtils
                .getOptionalValue(model, resource, iri("http://ns.intel.com/umf/2.0type"))
                .get().getObject().stringValue();
    }

    public static String getPropertyValue(Model model, Resource resource) {
        return ValidatorUtils
                .getOptionalValue(model, resource, iri("http://www.w3.org/1999/02/22-rdf-syntax-ns#value"))
                .get().getObject().stringValue();
    }

}