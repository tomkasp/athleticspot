package com.athleticspot.training.infrastracture.persistnce;

import com.athleticspot.service.UserService;
import com.athleticspot.training.domain.Athlete;
import com.athleticspot.training.domain.AthleteRepository;
import com.athleticspot.training.domain.trainingsurvey.TrainingSurvey;
import com.athleticspot.training.domain.trainingsurvey.TrainingSurveyProvider;
import com.athleticspot.training.domain.trainingsurvey.TrainingSurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Tomasz Kasprzycki
 */
@Service
public class TrainingSurveyProviderImpl implements TrainingSurveyProvider {

    private final UserService userService;
    private final AthleteRepository athleteRepository;
    private final TrainingSurveyRepository trainingSurveyRepository;

    @Autowired
    public TrainingSurveyProviderImpl(UserService userService, AthleteRepository athleteRepository, TrainingSurveyRepository trainingSurveyRepository) {
        this.userService = userService;
        this.athleteRepository = athleteRepository;
        this.trainingSurveyRepository = trainingSurveyRepository;
    }

    @Override
    public Optional<TrainingSurvey> getAthleteSurvey() {
        final Optional<Athlete> athleteOptional =
            athleteRepository
                .findByUserId(userService.getUserWithAuthorities().getId());
        final Optional<TrainingSurvey> trainingSurveyOptional = trainingSurveyRepository
            .findByAthleteIdUuid(athleteOptional.get().athleteId().uuid());
        return trainingSurveyOptional;
    }
}
