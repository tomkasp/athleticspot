package com.athleticspot.tracker.application;

import com.athleticspot.common.SecurityUtils;
import com.athleticspot.tracker.application.impl.StravaApplicationServiceImpl;
import com.athleticspot.tracker.application.impl.TimelineServiceImpl;
import com.athleticspot.tracker.domain.model.Timeline;
import com.athleticspot.tracker.domain.model.TimelineRepository;
import com.athleticspot.tracker.domain.model.manual.ManualSportActivity;
import com.athleticspot.tracker.domain.model.manual.ManualSportActivityRepository;
import com.athleticspot.tracker.shared.TestTimelineFactory;
import com.athleticspot.tracker.shared.TimelineEventFactory;
import com.google.maps.model.EncodedPolyline;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Tomasz Kasprzycki
 */
public class TimelineServiceTest {

    private TimelineService timelineService;
    private TrackerUserService trackerUserService;
    private StravaApplicationServiceImpl stravaApplicationService;
    private TimelineRepository timelineRepository;
    private ManualSportActivityRepository manualSportActivityRepository;
    private ApplicationEvents applicationEvents;
    private Timeline timeline;
    final String expectedTimelineId = UUID.randomUUID().toString();


    @Before
    public void setUp() {
        timelineRepository = Mockito.mock(TimelineRepository.class);
        trackerUserService = Mockito.mock(TrackerUserService.class);
        stravaApplicationService = Mockito.mock(StravaApplicationServiceImpl.class);
        applicationEvents = Mockito.mock(ApplicationEvents.class);
        manualSportActivityRepository = Mockito.mock(ManualSportActivityRepository.class);
        timelineService = new TimelineServiceImpl(timelineRepository, trackerUserService, manualSportActivityRepository, applicationEvents, stravaApplicationService);

        timeline = TestTimelineFactory.testTimeline(expectedTimelineId);
    }

    @Test
    public void polilineTest(){
        final EncodedPolyline encodedPolyline = new EncodedPolyline("qsnpH__{xBsBj@k@b\\~@`N|Hh[fOd]yNk\\wIq_@i@sSbAuS");
        encodedPolyline.decodePath();

    }

    @Test
    public void whenTimelineIsCreatedThenItIsStored() {
        //given
        Mockito.when(timelineRepository.nextTimelineId()).thenReturn(expectedTimelineId);

        //when
        final String timelineId = timelineService.createTimeline();

        //then
        Assertions.assertThat(timelineId).isEqualTo(expectedTimelineId);

        Mockito.verify(timelineRepository, Mockito.times(1)).nextTimelineId();
        Mockito.verify(timelineRepository, Mockito.times(1)).store(Matchers.isA(Timeline.class));
        Mockito.verify(applicationEvents, Mockito.times(1)).timelineWasCreated(Matchers.isA(Timeline.class));
    }

    @Test
    public void whenActivityIsAddedToTimelineThenItIsStored() {
        //given
        final String expectedSportActivityIdentifier = UUID.randomUUID().toString();
        Mockito.when(trackerUserService.getTimelineIdentifier()).thenReturn(expectedTimelineId);
        Mockito.when(manualSportActivityRepository.nextSportActivityId()).thenReturn(expectedSportActivityIdentifier);
        Mockito.when(timelineRepository.find(expectedTimelineId)).thenReturn(Optional.of(timeline));
        ManualSportActivity manualSportActivity = ManualSportActivity.create(
            expectedSportActivityIdentifier,
            "Manual",
            TimelineEventFactory.testSportActivity(),
            LocalDateTime.now()
        );

        //when
        timelineService.addActivity(TimelineEventFactory.testSportActivity(), "Manual", LocalDateTime.now());

        //then
        Mockito.verify(timelineRepository, Mockito.times(1)).find(expectedTimelineId);
        Mockito.verify(manualSportActivityRepository, Mockito.times(1)).store(manualSportActivity);
        Mockito.verify(applicationEvents, Mockito.times(1)).manualSportActivityAdded(manualSportActivity, SecurityUtils.getCurrentUserLogin());
    }

    @Test
    public void whenSystemAddActivityAndTimelineDoesntExisitNewTimelineIsCreated() {
        //given
        final String expectedSportActivityIdentifier = UUID.randomUUID().toString();
        Mockito.when(trackerUserService.getTimelineIdentifier()).thenReturn(null);
        Mockito.when(timelineRepository.nextTimelineId()).thenReturn(expectedTimelineId);
        Mockito.when(timelineRepository.find(null)).thenReturn(Optional.empty());
        Mockito.when(manualSportActivityRepository.nextSportActivityId()).thenReturn(expectedSportActivityIdentifier);
        ManualSportActivity manualSportActivity = ManualSportActivity.create(
            expectedSportActivityIdentifier,
            "Manual",
            TimelineEventFactory.testSportActivity(),
            LocalDateTime.now()
        );

        //when
        timelineService.addActivity(TimelineEventFactory.testSportActivity(), "Manual", LocalDateTime.now());

        //then
        Mockito.verify(trackerUserService, Mockito.times(1)).getTimelineIdentifier();
        Mockito.verify(timelineRepository, Mockito.times(1)).store(timeline);
        Mockito.verify(manualSportActivityRepository, Mockito.times(1)).store(manualSportActivity);
        Mockito.verify(applicationEvents, Mockito.times(1)).manualSportActivityAdded(manualSportActivity, SecurityUtils.getCurrentUserLogin());
    }

    @Test
    public void whenActivityIsRemovedFromTimelineThenTimelineDoestHaveIt() {
        //given
        Mockito.when(trackerUserService.getTimelineIdentifier()).thenReturn(expectedTimelineId);
        Mockito.when(timelineRepository.find(expectedTimelineId)).thenReturn(Optional.of(timeline));
        final String sportActivityIdentifier = UUID.randomUUID().toString();
        ManualSportActivity manualSportActivity = ManualSportActivity.create(
            sportActivityIdentifier,
            "Manual",
            TimelineEventFactory.testSportActivity(),
            LocalDateTime.now()
        );
        timeline.addTimelineEvent(manualSportActivity);

        //when
        timelineService.removeActivity(manualSportActivity);

        //then
        Assertions.assertThat(timeline.timelineEvents()).hasSize(0);

        Mockito.verify(trackerUserService, Mockito.times(1)).getTimelineIdentifier();
        Mockito.verify(manualSportActivityRepository, Mockito.times(1)).delete(sportActivityIdentifier);
        Mockito.verify(timelineRepository, Mockito.times(1)).store(timeline);
    }

}
