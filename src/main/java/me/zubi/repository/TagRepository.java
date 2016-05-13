package me.zubi.repository;

import me.zubi.domain.Tag;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Tag entity.
 */
@SuppressWarnings("unused")
public interface TagRepository extends MongoRepository<Tag,String> {

}
