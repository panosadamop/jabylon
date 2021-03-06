/**
 * (C) Copyright 2013 Jabylon (http://www.jabylon.org) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jabylon.index.properties.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.jabylon.index.properties.QueryService;
import org.jabylon.properties.Project;
import org.jabylon.properties.ProjectLocale;
import org.jabylon.properties.ProjectVersion;
import org.jabylon.properties.PropertiesFactory;
import org.jabylon.properties.PropertiesPackage;
import org.jabylon.properties.Property;
import org.jabylon.properties.PropertyAnnotation;
import org.jabylon.properties.PropertyFile;
import org.jabylon.properties.PropertyFileDescriptor;
import org.jabylon.properties.types.PropertyAnnotations;

public class PropertyFileAnalyzer {

    public List<Document> createDocuments(PropertyFileDescriptor descriptor) {
        PropertyFile file = descriptor.loadProperties();
        List<Document> documents = new ArrayList<Document>(file.getProperties().size());

        Map<String, Property> masterProperties = Collections.emptyMap();
        if(!descriptor.isMaster()) {
        	PropertyFile masterFile = descriptor.getMaster().loadProperties();
        	 masterProperties = masterFile.asMap();
        }

        EList<Property> properties = file.getProperties();
        for (Property property : properties) {
            Document doc = new Document();
            ProjectLocale locale = descriptor.getProjectLocale();
            ProjectVersion version = locale.getParent();
            Project project = version.getParent();

            Field projectField = new Field(QueryService.FIELD_PROJECT, project.getName(), Store.YES, Index.NOT_ANALYZED);
            doc.add(projectField);
            Field versionField = new Field(QueryService.FIELD_VERSION, version.getName(), Store.YES, Index.NOT_ANALYZED);
            doc.add(versionField);
            if(locale.isMaster())
            {
                //mark the master files specifically
                Field localeField = new Field(QueryService.FIELD_LOCALE, QueryService.MASTER, Store.YES, Index.NOT_ANALYZED);
                doc.add(localeField);
            }
            else if(locale.getLocale()!=null)
            {
                Field localeField = new Field(QueryService.FIELD_LOCALE, locale.getLocale().toString(), Store.YES, Index.NOT_ANALYZED);
                doc.add(localeField);

                //only add the master to a localized document
                if(masterProperties.get(property.getKey())!=null && masterProperties.get(property.getKey()).getValue()!=null) {
                	Field masterValueField = new Field(QueryService.FIELD_MASTER_VALUE, masterProperties.get(property.getKey()).getValue(), Store.YES, Index.ANALYZED);
                	doc.add(masterValueField);
                }
                if(masterProperties.get(property.getKey())!=null && masterProperties.get(property.getKey()).getComment()!=null) {
                	Field masterCommentField = new Field(QueryService.FIELD_MASTER_COMMENT, masterProperties.get(property.getKey()).getComment(), Store.YES, Index.ANALYZED);
                	doc.add(masterCommentField);
                }
            }
            Field uriField = new Field(QueryService.FIELD_URI, descriptor.getLocation().toString(), Store.YES, Index.NOT_ANALYZED);
            doc.add(uriField);
            Field pathField = new Field(QueryService.FIELD_FULL_PATH, descriptor.fullPath().toString(), Store.YES, Index.NOT_ANALYZED);
            doc.add(pathField);
            CDOID cdoID = descriptor.cdoID();
            StringBuilder builder = new StringBuilder();
            CDOIDUtil.write(builder, cdoID);

            Field idField = new Field(QueryService.FIELD_CDO_ID, builder.toString(), Store.YES, Index.NOT_ANALYZED);
            doc.add(idField);

            Field comment = new Field(QueryService.FIELD_COMMENT, nullSafe(property.getComment()), Store.YES, Index.ANALYZED);
            doc.add(comment);
            Field key = new Field(QueryService.FIELD_KEY, nullSafe(property.getKey()), Store.YES, Index.NOT_ANALYZED);
            doc.add(key);
            Field analyzedKey = new Field(QueryService.FIELD_KEY, nullSafe(property.getKey()), Store.YES, Index.ANALYZED);
            doc.add(analyzedKey);
            Field value = new Field(QueryService.FIELD_VALUE, nullSafe(property.getValue()), Store.YES, Index.ANALYZED);
            doc.add(value);
            String templateLocation = descriptor.getMaster() == null ? "" : descriptor.getMaster().getLocation().toString();
            Field templateLoc = new Field(QueryService.FIELD_TEMPLATE_LOCATION, templateLocation, Store.YES, Index.NOT_ANALYZED);
            doc.add(templateLoc);
            documents.add(doc);
        }
        return documents;
    }
    
    /**
     * analyses TMX files
     * This works a little different since it is bilingual and there is no template.
     * So instead of creating a master and several slave documents, we have one document that contains both.
     * The normal attributes are filled from the source language and the translation goes in the TMX* fields
     * @param file
     * @param location
     * @return
     */
	public List<Document> createTMXDocuments(PropertyFile file, URI location) {
		List<Document> documents = new ArrayList<Document>(file.getProperties().size() * 2);

		EList<Property> properties = file.getProperties();
		for (Property property : properties) {
			Document doc = new Document();
//			Locale sourceLocale = extractSourceLocale(property);
			Locale targetLocale = extractTargetLocale(property);
			if (targetLocale == null)
				continue;

			Field localeField = new Field(QueryService.FIELD_TMX_LOCALE, targetLocale.toString(), Store.YES, Index.NOT_ANALYZED);
			doc.add(localeField);
			doc.add(new Field(QueryService.FIELD_LOCALE, QueryService.MASTER, Store.YES, Index.NOT_ANALYZED));
			doc.add(new Field(QueryService.FIELD_TMX, Boolean.TRUE.toString(), Store.YES, Index.NOT_ANALYZED));

			if (property.getKey() != null) {
				Field masterValueField = new Field(QueryService.FIELD_MASTER_VALUE, property.getKey(), Store.YES, Index.ANALYZED);
				doc.add(masterValueField);
			}

			if (property.getComment() != null) {
				Field masterCommentField = new Field(QueryService.FIELD_MASTER_COMMENT, property.getComment(), Store.YES, Index.ANALYZED);
				doc.add(masterCommentField);
			}

			Field uriField = new Field(QueryService.FIELD_URI, location.toString(), Store.YES, Index.NOT_ANALYZED);
			doc.add(uriField);
			Field pathField = new Field(QueryService.FIELD_FULL_PATH, location.toString(), Store.YES, Index.NOT_ANALYZED);
			doc.add(pathField);

			Field comment = new Field(QueryService.FIELD_COMMENT, nullSafe(property.getComment()), Store.YES, Index.ANALYZED);
			doc.add(comment);
			Field key = new Field(QueryService.FIELD_KEY, nullSafe(property.getKey()), Store.YES, Index.NOT_ANALYZED);
			doc.add(key);
			Field analyzedKey = new Field(QueryService.FIELD_KEY, nullSafe(property.getKey()), Store.YES, Index.ANALYZED);
			doc.add(analyzedKey);
			doc.add(new Field(QueryService.FIELD_TMX_VALUE, nullSafe(property.getValue()), Store.YES, Index.ANALYZED));
			//the key is the actual master value
			doc.add(new Field(QueryService.FIELD_VALUE, nullSafe(property.getKey()), Store.YES, Index.ANALYZED));
			String templateLocation = location.toString();
			Field templateLoc = new Field(QueryService.FIELD_TEMPLATE_LOCATION, templateLocation, Store.YES, Index.NOT_ANALYZED);
			doc.add(templateLoc);
			documents.add(doc);
		}
		return documents;
	}

    /**
     * extracts the target locale from a property annotation. 
     * @param property
     * @return
     */
    private Locale extractTargetLocale(Property property) {
    	PropertyAnnotation annotation = property.findAnnotation(PropertyAnnotations.ANNOTATION_LANGUAGE);
    	if(annotation==null)
    		return null;
    	String localeString = annotation.getValues().get(PropertyAnnotations.TARGET_LANGUAGE);
    	if(localeString==null)
    		return null;
    	return (Locale) PropertiesFactory.eINSTANCE.createFromString(PropertiesPackage.Literals.LOCALE, localeString);
	}

/*	private Locale extractSourceLocale(Property property) {
		PropertyAnnotation annotation = property.findAnnotation(PropertyAnnotations.ANNOTATION_LANGUAGE);
    	if(annotation==null)
    		return Locale.ENGLISH;
    	String localeString = annotation.getValues().get(PropertyAnnotations.SOURCE_LANGUAGE);
    	if(localeString==null)
    		return Locale.ENGLISH;
    	return (Locale) PropertiesFactory.eINSTANCE.createFromString(PropertiesPackage.Literals.LOCALE, localeString);
	}*/

	private String nullSafe(String s) {
        return s == null ? "" : s;
    }

}
