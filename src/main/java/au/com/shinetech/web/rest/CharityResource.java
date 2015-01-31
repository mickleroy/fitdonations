package au.com.shinetech.web.rest;

import com.codahale.metrics.annotation.Timed;
import au.com.shinetech.domain.Charity;
import au.com.shinetech.repository.CharityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Charity.
 */
@RestController
@RequestMapping("/api")
public class CharityResource {

    private final Logger log = LoggerFactory.getLogger(CharityResource.class);

    @Inject
    private CharityRepository charityRepository;

    /**
     * POST  /charitys -> Create a new charity.
     */
    @RequestMapping(value = "/charitys",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody Charity charity) {
        log.debug("REST request to save Charity : {}", charity);
        charityRepository.save(charity);
    }

    /**
     * GET  /charitys -> get all the charitys.
     */
    @RequestMapping(value = "/charitys",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Charity> getAll() {
        log.debug("REST request to get all Charitys");
        return charityRepository.findAll();
    }

    /**
     * GET  /charitys/:id -> get the "id" charity.
     */
    @RequestMapping(value = "/charitys/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Charity> get(@PathVariable Long id) {
        log.debug("REST request to get Charity : {}", id);
        return Optional.ofNullable(charityRepository.findOne(id))
            .map(charity -> new ResponseEntity<>(
                charity,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /charitys/:id -> delete the "id" charity.
     */
    @RequestMapping(value = "/charitys/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Charity : {}", id);
        charityRepository.delete(id);
    }
}
