package com.kidest.ecommerce.config;

import com.kidest.ecommerce.entity.Product;
import com.kidest.ecommerce.entity.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

//we don't want to just be able to make changes our data, add, delete stuff etc
//so we're disabling those methods for these requests

@Configuration
public class MyDataRestConfig implements RepositoryRestConfigurer {
    //autowire jpa entity manager
    private EntityManager entityManager;

    @Autowired
    public MyDataRestConfig(EntityManager theEntityManager){
        entityManager = theEntityManager;
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        RepositoryRestConfigurer.super.configureRepositoryRestConfiguration(config, cors);

        HttpMethod[] theUnsupportedActions = {HttpMethod.PUT, HttpMethod.POST, HttpMethod.DELETE};

        //disable http methods for products: PUT, POST, AND DELETE
        config.getExposureConfiguration()
                .forDomainType(Product.class)
                //disable each
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions))
                //disable all as a collection
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions));

        //disable http methods for product category: PUT, POST, AND DELETE
        config.getExposureConfiguration()
                .forDomainType(ProductCategory.class)
                //disable each
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions))
                //disable all as a collection
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions));

        //call method to expose ids
        exposeIds(config);
    }
    private void exposeIds(RepositoryRestConfiguration config){
        //expose entity ids


        //get list of all entity classes from entity manager
        Set<javax.persistence.metamodel.EntityType<?>> entities = entityManager.getMetamodel().getEntities();

        //create arraylist of those entity types
        List<Class> entityClasses = new ArrayList<>();

        //getn entity type for the entities
        for (EntityType<?> tempEntityType: entities){
            entityClasses.add(tempEntityType.getJavaType());
        }

        //expose id for the array of entity types
        Class[] domainTypes = entityClasses.toArray(new Class[0]);
        config.exposeIdsFor(domainTypes);
    }
}
