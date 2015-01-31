package au.com.shinetech.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import au.com.shinetech.Application;
import au.com.shinetech.domain.Charity;
import au.com.shinetech.repository.CharityRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CharityResource REST controller.
 *
 * @see CharityResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class CharityResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    @Inject
    private CharityRepository charityRepository;

    private MockMvc restCharityMockMvc;

    private Charity charity;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CharityResource charityResource = new CharityResource();
        ReflectionTestUtils.setField(charityResource, "charityRepository", charityRepository);
        this.restCharityMockMvc = MockMvcBuilders.standaloneSetup(charityResource).build();
    }

    @Before
    public void initTest() {
        charity = new Charity();
        charity.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createCharity() throws Exception {
        // Validate the database is empty
        assertThat(charityRepository.findAll()).hasSize(0);

        // Create the Charity
        restCharityMockMvc.perform(post("/api/charitys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(charity)))
                .andExpect(status().isOk());

        // Validate the Charity in the database
        List<Charity> charitys = charityRepository.findAll();
        assertThat(charitys).hasSize(1);
        Charity testCharity = charitys.iterator().next();
        assertThat(testCharity.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void getAllCharitys() throws Exception {
        // Initialize the database
        charityRepository.saveAndFlush(charity);

        // Get all the charitys
        restCharityMockMvc.perform(get("/api/charitys"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(charity.getId().intValue()))
                .andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getCharity() throws Exception {
        // Initialize the database
        charityRepository.saveAndFlush(charity);

        // Get the charity
        restCharityMockMvc.perform(get("/api/charitys/{id}", charity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(charity.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCharity() throws Exception {
        // Get the charity
        restCharityMockMvc.perform(get("/api/charitys/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCharity() throws Exception {
        // Initialize the database
        charityRepository.saveAndFlush(charity);

        // Update the charity
        charity.setName(UPDATED_NAME);
        restCharityMockMvc.perform(post("/api/charitys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(charity)))
                .andExpect(status().isOk());

        // Validate the Charity in the database
        List<Charity> charitys = charityRepository.findAll();
        assertThat(charitys).hasSize(1);
        Charity testCharity = charitys.iterator().next();
        assertThat(testCharity.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void deleteCharity() throws Exception {
        // Initialize the database
        charityRepository.saveAndFlush(charity);

        // Get the charity
        restCharityMockMvc.perform(delete("/api/charitys/{id}", charity.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Charity> charitys = charityRepository.findAll();
        assertThat(charitys).hasSize(0);
    }
}
