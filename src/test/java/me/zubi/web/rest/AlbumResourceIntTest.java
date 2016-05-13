package me.zubi.web.rest;

import me.zubi.MGalleryApp;
import me.zubi.domain.Album;
import me.zubi.repository.AlbumRepository;

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
 * Test class for the AlbumResource REST controller.
 *
 * @see AlbumResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MGalleryApp.class)
@WebAppConfiguration
@IntegrationTest
public class AlbumResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    @Inject
    private AlbumRepository albumRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAlbumMockMvc;

    private Album album;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AlbumResource albumResource = new AlbumResource();
        ReflectionTestUtils.setField(albumResource, "albumRepository", albumRepository);
        this.restAlbumMockMvc = MockMvcBuilders.standaloneSetup(albumResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        albumRepository.deleteAll();
        album = new Album();
        album.setName(DEFAULT_NAME);
        album.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    public void createAlbum() throws Exception {
        int databaseSizeBeforeCreate = albumRepository.findAll().size();

        // Create the Album

        restAlbumMockMvc.perform(post("/api/albums")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(album)))
                .andExpect(status().isCreated());

        // Validate the Album in the database
        List<Album> albums = albumRepository.findAll();
        assertThat(albums).hasSize(databaseSizeBeforeCreate + 1);
        Album testAlbum = albums.get(albums.size() - 1);
        assertThat(testAlbum.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAlbum.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    public void getAllAlbums() throws Exception {
        // Initialize the database
        albumRepository.save(album);

        // Get all the albums
        restAlbumMockMvc.perform(get("/api/albums?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(album.getId())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    public void getAlbum() throws Exception {
        // Initialize the database
        albumRepository.save(album);

        // Get the album
        restAlbumMockMvc.perform(get("/api/albums/{id}", album.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(album.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    public void getNonExistingAlbum() throws Exception {
        // Get the album
        restAlbumMockMvc.perform(get("/api/albums/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateAlbum() throws Exception {
        // Initialize the database
        albumRepository.save(album);
        int databaseSizeBeforeUpdate = albumRepository.findAll().size();

        // Update the album
        Album updatedAlbum = new Album();
        updatedAlbum.setId(album.getId());
        updatedAlbum.setName(UPDATED_NAME);
        updatedAlbum.setDescription(UPDATED_DESCRIPTION);

        restAlbumMockMvc.perform(put("/api/albums")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAlbum)))
                .andExpect(status().isOk());

        // Validate the Album in the database
        List<Album> albums = albumRepository.findAll();
        assertThat(albums).hasSize(databaseSizeBeforeUpdate);
        Album testAlbum = albums.get(albums.size() - 1);
        assertThat(testAlbum.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAlbum.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    public void deleteAlbum() throws Exception {
        // Initialize the database
        albumRepository.save(album);
        int databaseSizeBeforeDelete = albumRepository.findAll().size();

        // Get the album
        restAlbumMockMvc.perform(delete("/api/albums/{id}", album.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Album> albums = albumRepository.findAll();
        assertThat(albums).hasSize(databaseSizeBeforeDelete - 1);
    }
}
