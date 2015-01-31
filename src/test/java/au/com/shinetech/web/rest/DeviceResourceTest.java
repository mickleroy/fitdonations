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
import au.com.shinetech.domain.Device;
import au.com.shinetech.repository.DeviceRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the DeviceResource REST controller.
 *
 * @see DeviceResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class DeviceResourceTest {

    private static final String DEFAULT_ACCESS_TOKEN = "SAMPLE_TEXT";
    private static final String UPDATED_ACCESS_TOKEN = "UPDATED_TEXT";
    private static final String DEFAULT_SECRET_TOKEN = "SAMPLE_TEXT";
    private static final String UPDATED_SECRET_TOKEN = "UPDATED_TEXT";

    @Inject
    private DeviceRepository deviceRepository;

    private MockMvc restDeviceMockMvc;

    private Device device;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DeviceResource deviceResource = new DeviceResource();
        ReflectionTestUtils.setField(deviceResource, "deviceRepository", deviceRepository);
        this.restDeviceMockMvc = MockMvcBuilders.standaloneSetup(deviceResource).build();
    }

    @Before
    public void initTest() {
        device = new Device();
        device.setAccessToken(DEFAULT_ACCESS_TOKEN);
        device.setSecretToken(DEFAULT_SECRET_TOKEN);
    }

    @Test
    @Transactional
    public void createDevice() throws Exception {
        // Validate the database is empty
        assertThat(deviceRepository.findAll()).hasSize(0);

        // Create the Device
        restDeviceMockMvc.perform(post("/api/devices")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(device)))
                .andExpect(status().isOk());

        // Validate the Device in the database
        List<Device> devices = deviceRepository.findAll();
        assertThat(devices).hasSize(1);
        Device testDevice = devices.iterator().next();
        assertThat(testDevice.getAccessToken()).isEqualTo(DEFAULT_ACCESS_TOKEN);
        assertThat(testDevice.getSecretToken()).isEqualTo(DEFAULT_SECRET_TOKEN);
    }

    @Test
    @Transactional
    public void getAllDevices() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the devices
        restDeviceMockMvc.perform(get("/api/devices"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(device.getId().intValue()))
                .andExpect(jsonPath("$.[0].accessToken").value(DEFAULT_ACCESS_TOKEN.toString()))
                .andExpect(jsonPath("$.[0].secretToken").value(DEFAULT_SECRET_TOKEN.toString()));
    }

    @Test
    @Transactional
    public void getDevice() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get the device
        restDeviceMockMvc.perform(get("/api/devices/{id}", device.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(device.getId().intValue()))
            .andExpect(jsonPath("$.accessToken").value(DEFAULT_ACCESS_TOKEN.toString()))
            .andExpect(jsonPath("$.secretToken").value(DEFAULT_SECRET_TOKEN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDevice() throws Exception {
        // Get the device
        restDeviceMockMvc.perform(get("/api/devices/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDevice() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Update the device
        device.setAccessToken(UPDATED_ACCESS_TOKEN);
        device.setSecretToken(UPDATED_SECRET_TOKEN);
        restDeviceMockMvc.perform(post("/api/devices")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(device)))
                .andExpect(status().isOk());

        // Validate the Device in the database
        List<Device> devices = deviceRepository.findAll();
        assertThat(devices).hasSize(1);
        Device testDevice = devices.iterator().next();
        assertThat(testDevice.getAccessToken()).isEqualTo(UPDATED_ACCESS_TOKEN);
        assertThat(testDevice.getSecretToken()).isEqualTo(UPDATED_SECRET_TOKEN);
    }

    @Test
    @Transactional
    public void deleteDevice() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get the device
        restDeviceMockMvc.perform(delete("/api/devices/{id}", device.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Device> devices = deviceRepository.findAll();
        assertThat(devices).hasSize(0);
    }
}
