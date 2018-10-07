package com.athleticspot.tracker.infrastracture.assembler;

import com.athleticspot.tracker.domain.model.SportActivity;
import com.athleticspot.tracker.domain.model.SportActivityType;
import com.athleticspot.tracker.domain.model.TrackerSource;
import com.athleticspot.tracker.domain.model.manual.ManualSportActivity;
import com.athleticspot.tracker.domain.model.manual.ManualSportActivityDetails;
import com.athleticspot.tracker.domain.model.strava.StravaSportActivity;
import javastrava.api.v3.model.StravaActivity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tomasz Kasprzycki
 */
@Component
public class StravaActivityAssembler implements SportActivityAssembler<StravaSportActivity>{

    public ManualSportActivity buildFromStrava(StravaActivity stravaActivity) {

        final ManualSportActivityDetails manualSportActivityDetails = ManualSportActivityDetails.create(
            stravaActivity.getDescription(),
            stravaActivity.getName(),
            SportActivityType.valueOf(stravaActivity.getType().getValue().toUpperCase()),
            stravaActivity.getElapsedTime().toString(),
            stravaActivity.getDistance().toString(),
            null,
            stravaActivity.getMaxSpeed().toString(),
            stravaActivity.getAverageSpeed().toString()
        );

        return ManualSportActivity.createNew(
            "STRAVA",
            manualSportActivityDetails,
            stravaActivity.getStartDateLocal()
        );
    }

    public List<ManualSportActivity> buildFromStravaActivities(List<StravaActivity> stravaActivities) {
        return stravaActivities.stream()
            .map(this::buildFromStrava)
            .collect(Collectors.toList());
    }

    @Override
    public SportActivity assembleSportActivity(StravaSportActivity trackerSportActivity) {
        return SportActivity.create(trackerSportActivity.getExternalId(),
            trackerSportActivity.getUsername(),
            trackerSportActivity.getTitle(),
            TrackerSource.STRAVA,
            trackerSportActivity.getStartDate() );
    }
}
