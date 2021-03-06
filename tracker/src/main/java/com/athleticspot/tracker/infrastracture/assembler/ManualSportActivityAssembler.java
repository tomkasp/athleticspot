package com.athleticspot.tracker.infrastracture.assembler;

import com.athleticspot.tracker.domain.model.SportActivity;
import com.athleticspot.tracker.domain.model.TrackerSource;
import com.athleticspot.tracker.domain.model.UserRepository;
import com.athleticspot.tracker.domain.model.manual.ManualSportActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author Tomasz Kasprzycki
 */
@Component
public class ManualSportActivityAssembler implements SportActivityAssembler<ManualSportActivity> {

    @Autowired
    public ManualSportActivityAssembler(UserRepository userRepository) {
    }

    @Override
    public SportActivity assembleSportActivity(ManualSportActivity manualSportActivity) {
        final SportActivity sportActivity = new SportActivity()
            .setId(manualSportActivity.identifier())
            .setUsername(manualSportActivity.getUsername())
            .setTrackerSource(TrackerSource.MANUAL)
            .setSportActivityType(manualSportActivity.details().type())
            .setTitle(manualSportActivity.details().title())
            .setMovingTime(manualSportActivity.details().duration())
            .setStartDate(manualSportActivity.getStartDate());
        final String distance = manualSportActivity.details().distance();
        if(Objects.nonNull(distance)){
            sportActivity.setDistance(Float.parseFloat(distance));
            sportActivity.setUnits(manualSportActivity.details().units());
        }
        return sportActivity;
    }

}
