package me.zubi.web.rest;

import me.zubi.MGalleryApp;
import me.zubi.domain.Media;
import me.zubi.repository.MediaRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the MediaResource REST controller.
 *
 * @see MediaResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MGalleryApp.class)
@WebAppConfiguration
@IntegrationTest
public class MediaResourceIntTest {

    private static final String DEFAULT_CAPTION = "AAAAA";
    private static final String UPDATED_CAPTION = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";
    private static final String DEFAULT_LOCATION = "AAAAA";
    private static final String UPDATED_LOCATION = "BBBBB";

    @Inject
    private MediaRepository mediaRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restMediaMockMvc;

    private Media media;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MediaResource mediaResource = new MediaResource();
        ReflectionTestUtils.setField(mediaResource, "mediaRepository", mediaRepository);
        this.restMediaMockMvc = MockMvcBuilders.standaloneSetup(mediaResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        mediaRepository.deleteAll();
        media = new Media();
        media.setCaption(DEFAULT_CAPTION);
        media.setDescription(DEFAULT_DESCRIPTION);
        media.setLocation(DEFAULT_LOCATION);
    }

    @Test
    public void createMedia() throws Exception {
        int databaseSizeBeforeCreate = mediaRepository.findAll().size();

        // Create the Media

        restMediaMockMvc.perform(post("/api/media")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(media)))
                .andExpect(status().isCreated());

        // Validate the Media in the database
        List<Media> media = mediaRepository.findAll();
        assertThat(media).hasSize(databaseSizeBeforeCreate + 1);
        Media testMedia = media.get(media.size() - 1);
        assertThat(testMedia.getCaption()).isEqualTo(DEFAULT_CAPTION);
        assertThat(testMedia.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testMedia.getLocation()).isEqualTo(DEFAULT_LOCATION);
    }

    @Test
    public void getAllMedia() throws Exception {
        // Initialize the database
        mediaRepository.save(media);

        // Get all the media
        restMediaMockMvc.perform(get("/api/media?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(media.getId())))
                .andExpect(jsonPath("$.[*].caption").value(hasItem(DEFAULT_CAPTION.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION.toString())));
    }

    @Test
    public void getMedia() throws Exception {
        // Initialize the database
        mediaRepository.save(media);

        // Get the media
        restMediaMockMvc.perform(get("/api/media/{id}", media.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(media.getId()))
            .andExpect(jsonPath("$.caption").value(DEFAULT_CAPTION.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION.toString()));
    }

    @Test
    public void getNonExistingMedia() throws Exception {
        // Get the media
        restMediaMockMvc.perform(get("/api/media/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateMedia() throws Exception {
        // Initialize the database
        mediaRepository.save(media);
        int databaseSizeBeforeUpdate = mediaRepository.findAll().size();

        // Update the media
        Media updatedMedia = new Media();
        updatedMedia.setId(media.getId());
        updatedMedia.setCaption(UPDATED_CAPTION);
        updatedMedia.setDescription(UPDATED_DESCRIPTION);
        updatedMedia.setLocation(UPDATED_LOCATION);

        restMediaMockMvc.perform(put("/api/media")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedMedia)))
                .andExpect(status().isOk());

        // Validate the Media in the database
        List<Media> media = mediaRepository.findAll();
        assertThat(media).hasSize(databaseSizeBeforeUpdate);
        Media testMedia = media.get(media.size() - 1);
        assertThat(testMedia.getCaption()).isEqualTo(UPDATED_CAPTION);
        assertThat(testMedia.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMedia.getLocation()).isEqualTo(UPDATED_LOCATION);
    }

    @Test
    public void deleteMedia() throws Exception {
        // Initialize the database
        mediaRepository.save(media);
        int databaseSizeBeforeDelete = mediaRepository.findAll().size();

        // Get the media
        restMediaMockMvc.perform(delete("/api/media/{id}", media.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Media> media = mediaRepository.findAll();
        assertThat(media).hasSize(databaseSizeBeforeDelete - 1);
    }
}
