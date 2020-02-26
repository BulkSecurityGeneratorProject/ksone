package org.crossfit.app.web.rest.www;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.config.CacheConfiguration;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.TimeService;
import org.crossfit.app.service.TimeSlotService;
import org.crossfit.app.web.rest.dto.TimeSlotInstanceDTO;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * REST controller for managing TimeSlot.
 */
@RestController
@RequestMapping("/public")
public class PublicTimeSlotResource {

    private final Logger log = LoggerFactory.getLogger(PublicTimeSlotResource.class);

    @Inject
    private TimeSlotService timeSlotService;

	@Inject
	private CrossFitBoxSerivce boxService;

    @Inject
    private TimeService timeService;
	
	/**
     * GET  /event -> get all event (timeslot & closedday.
     */
    @RequestMapping(value = "/timeslots.json",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)	
    @Cacheable(CacheConfiguration.PUBLIC_TIMESLOT_CACHE_NAME)
    public ResponseEntity<AgendaWebDTO> getTimeSlotsByDayByHour(
    		@RequestParam(value = "start", required = false) String startStr,
    		@RequestParam(value = "v", required = false, defaultValue = "1") String version) {

    	CrossFitBox box = boxService.findCurrentCrossFitBox();

    	DateTime startAt = StringUtils.isNotBlank(startStr) ? timeService.parseDate("yyyy-MM-dd", startStr, box) : null;
    	startAt = startAt==null ? timeService.nowAsDateTime(box).withDayOfWeek(DateTimeConstants.MONDAY) : startAt;
    	DateTime endAt = startAt.plusDays(6);
    	
    	final DateTimeFormatter dtfJour = DateTimeFormat.forPattern("EEEE").withLocale(Locale.FRENCH);
    	final DateTimeFormatter dtfHeure = DateTimeFormat.forPattern("HH:mm");

    	List<TimeSlotInstanceDTO> events = timeSlotService.findAllTimeSlotInstance(startAt, endAt, Collections.emptyList(), Collections.emptyList(), timeService.getDateTimeZone(box))
    			.collect(Collectors.toList());
    	
    	

    	Stream<LocalDate> days = events.parallelStream().map(TimeSlotInstanceDTO::getDate).distinct();
    	Set<Interval> times = events.parallelStream().map(ts->new Interval(ts.getStart().withDate(LocalDate.now()), ts.getEnd().withDate(LocalDate.now()))).distinct().collect(Collectors.toSet());
    	Map<Interval, List<TimeSlotInstanceDTO>> eventByDateTime = events.parallelStream().collect(
    			Collectors.groupingBy(ts->new Interval(ts.getStart(), ts.getEnd())));
    	
    	
    	Map<String, Map<String, List<String>>> collect =
    			days.collect(Collectors.toMap(
    					dtfJour::print, day->times.stream().sorted(Comparator.comparing(Interval::getStart)).collect(Collectors.toMap(
    							time->dtfHeure.print(time.getStart())+"-"+dtfHeure.print(time.getEnd()), 
    							time->eventByDateTime.getOrDefault(
    									new Interval(day.toDateTime(time.getStart().toLocalTime()), day.toDateTime(time.getEnd().toLocalTime())), Collections.emptyList()
    								).stream().map(ts->ts.getTimeSlotType().getName()).collect(Collectors.toList()), (a,b)->a, LinkedHashMap::new
    							)
    						), (a,b)->a, LinkedHashMap::new
    					)
    				);
    	
    	AgendaWebDTO agenda = new AgendaWebDTO();
    	agenda.events = collect;
    	
    	Stream<TimeSlotType> sorted = events.stream().map(TimeSlotInstanceDTO::getTimeSlotType).distinct().sorted();
    	
    	if ("2".equals(version)) { //En v2 => Liste
    		agenda.definitions = sorted.map(TimeSlotTypeWebDTO::new).collect(Collectors.toList());
    	}
    	else { //Sinon v1 et en v1 => Map
        	agenda.definitions = sorted.collect(Collectors.toMap(TimeSlotType::getName, TimeSlotType::getDescription));
    	}
    	
   
     	
    	return new ResponseEntity<AgendaWebDTO>(agenda, HttpStatus.OK);
    }
    
    
    static class AgendaWebDTO{
    	Object definitions;
    	Map<String, Map<String, List<String>>> events;

		public Map<String, Map<String, List<String>>> getEvents() {
			return events;
		}
		public Object getDefinitions() {
			return definitions;
		}
    }
    
    static class TimeSlotTypeWebDTO{

        private String name;        
        private String description;        
        private String cssClass;

        
        
        public TimeSlotTypeWebDTO(TimeSlotType t) {
			super();
			this.name = t.getName();
			this.description = t.getDescription();
			this.cssClass = t.getWebCssClass();
		}
		public String getName() {
			return name;
		}
		public String getDescription() {
			return description;
		}
		public String getCssClass() {
			return cssClass;
		}
    }
}