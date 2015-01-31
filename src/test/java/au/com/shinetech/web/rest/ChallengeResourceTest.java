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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.util.List;

import au.com.shinetech.Application;
import au.com.shinetech.domain.Challenge;
import au.com.shinetech.repository.ChallengeRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ChallengeResource REST controller.
 *
 * @see ChallengeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ChallengeResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");


    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.ZERO;
    private static final BigDecimal UPDATED_AMOUNT = BigDecimal.ONE;

    private static final Integer DEFAULT_DISTANCE = 0;
    private static final Integer UPDATED_DISTANCE = 1;

    private static final DateTime DEFAULT_START_DATE = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_START_DATE = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_START_DATE_STR = dateTimeFormatter.print(DEFAULT_START_DATE);

    private static final DateTime DEFAULT_END_DATE = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_END_DATE = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_END_DATE_STR = dateTimeFormatter.print(DEFAULT_END_DATE);

    @Inject
    private ChallengeRepository challengeRepository;

    private MockMvc restChallengeMockMvc;

    private Challenge challenge;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ChallengeResource challengeResource = new ChallengeResource();
        ReflectionTestUtils.setField(challengeResource, "challengeRepository", challengeRepository);
        this.restChallengeMockMvc = MockMvcBuilders.standaloneSetup(challengeResource).build();
    }

    @Before
    public void initTest() {
        challenge = new Challenge();
        challenge.setAmount(DEFAULT_AMOUNT);
        challenge.setDistance(DEFAULT_DISTANCE);
        challenge.setStartDate(DEFAULT_START_DATE);
        challenge.setEndDate(DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    public void createChallenge() throws Exception {
        // Validate the database is empty
        assertThat(challengeRepository.findAll()).hasSize(0);

        // Create the Challenge
        restChallengeMockMvc.perform(post("/api/challenges")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(challenge)))
                .andExpect(status().isOk());

        // Validate the Challenge in the database
        List<Challenge> challenges = challengeRepository.findAll();
        assertThat(challenges).hasSize(1);
        Challenge testChallenge = challenges.iterator().next();
        assertThat(testChallenge.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testChallenge.getDistance()).isEqualTo(DEFAULT_DISTANCE);
        assertThat(testChallenge.getStartDate().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_START_DATE);
        assertThat(testChallenge.getEndDate().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    public void getAllChallenges() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challenges
        restChallengeMockMvc.perform(get("/api/challenges"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(challenge.getId().intValue()))
                .andExpect(jsonPath("$.[0].amount").value(DEFAULT_AMOUNT.intValue()))
                .andExpect(jsonPath("$.[0].distance").value(DEFAULT_DISTANCE))
                .andExpect(jsonPath("$.[0].startDate").value(DEFAULT_START_DATE_STR))
                .andExpect(jsonPath("$.[0].endDate").value(DEFAULT_END_DATE_STR));
    }

    @Test
    @Transactional
    public void getChallenge() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get the challenge
        restChallengeMockMvc.perform(get("/api/challenges/{id}", challenge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(challenge.getId().intValue()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()))
            .andExpect(jsonPath("$.distance").value(DEFAULT_DISTANCE))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE_STR))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE_STR));
    }

    @Test
    @Transactional
    public void getNonExistingChallenge() throws Exception {
        // Get the challenge
        restChallengeMockMvc.perform(get("/api/challenges/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateChallenge() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Update the challenge
        challenge.setAmount(UPDATED_AMOUNT);
        challenge.setDistance(UPDATED_DISTANCE);
        challenge.setStartDate(UPDATED_START_DATE);
        challenge.setEndDate(UPDATED_END_DATE);
        restChallengeMockMvc.perform(post("/api/challenges")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(challenge)))
                .andExpect(status().isOk());

        // Validate the Challenge in the database
        List<Challenge> challenges = challengeRepository.findAll();
        assertThat(challenges).hasSize(1);
        Challenge testChallenge = challenges.iterator().next();
        assertThat(testChallenge.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testChallenge.getDistance()).isEqualTo(UPDATED_DISTANCE);
        assertThat(testChallenge.getStartDate().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_START_DATE);
        assertThat(testChallenge.getEndDate().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void deleteChallenge() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get the challenge
        restChallengeMockMvc.perform(delete("/api/challenges/{id}", challenge.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Challenge> challenges = challengeRepository.findAll();
        assertThat(challenges).hasSize(0);
    }
}
