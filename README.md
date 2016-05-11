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
* Album - used for grouping resources into galleries
* Media - Resource represent Blob Object
* Tag - used for tagging resources

# model (jdl)

entity Media {

}
entity Album {

}
entity Tag {

}

# multitenancy feature
* Domain objects are owned by some entity in current implementation User.
* All Domain objects are clustered with entity id.
* All Domain objects inherit from MultitenantAware object.
* Utilization of Spring Security SecurityContext
* Users are restricted to create update delete their own Galleries
* 

# Api
* User

GET          | /users/{id}/media/search | search user media based on a query
GET          | /users/{id}/media/recent | get user recent media
GET          | /users/{id}/albums       | get user albums
GET,PUT,POST | /users/self/media        | 
GET          | /users/self/media/search | search self media based on a query
GET          | /users/self/media/recent | get self recent media
GET,PUT,POST | /users/self/albums       | get self albums

* Albums
GET | /albums/{id}   | get album information and list of media inside it

* Media
GET | /media/{id}    | 
GET | /media/search  | geospatial query

