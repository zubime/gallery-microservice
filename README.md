# gallery-microservice
Multitenant Gallery Microservice for Jhipster.

# general
* MongoDB store.
* Based on Jhipster 3.1
* Based on microservice
* Added frontend layer

# gallery feature
* Used to store Blob resoures mostly images.
* Grid FS Store
* Utilization of Spring Data Mongo DB Grid FS template to store Blob Objects

# domain
* Gallery - used for grouping resources into galleries
* Resource - Resource represent Blob Object
* Tag - used for tagging resources

# model (jdl)

# multitenancy feature
* Domain objects are owned by some entity in current implementation User.
* All Domain objects are clustered with entity id.
* All Domain objects inherit from MultitenantAware object.
* Utilization of Spring Security SecurityContext
* Users are restricted to create update delete their own Galleries
