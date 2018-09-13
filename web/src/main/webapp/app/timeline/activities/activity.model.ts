import {ActivityDetailsModel} from "./activity-details.model";
import moment = require("moment");

export class ActivityModel {

    activityDetails : ActivityDetailsModel;
    sportActivityIdentifier: String;
    source = "MANUAL";
    title = "title";
    description = "description";
    type = "running";
    duration = "25";
    distance = "10";
    unit = "km";
    maxSpeed = "";
    meanSpeed = "";
    dateTime : String;

    constructor(sportActivityIdentifier: String,
                source: String,
                sportActivityDetails :ActivityDetailsModel){
        sportActivityDetails.dateTime = moment(sportActivityDetails.dateTime)
            .format("YYYY-MM-DD HH:mm:ss");
        this.source = this.source
        this.sportActivityIdentifier = sportActivityIdentifier;
        this.activityDetails = sportActivityDetails;
    }

    public assignDetails(activityDetails: ActivityDetailsModel){
        this.title = activityDetails.title;
        this.description = activityDetails.description;
        this.type = activityDetails.type;
        this.duration = activityDetails.duration;
        this.distance = activityDetails.distance;
        this.unit = activityDetails.unit;
        this.maxSpeed = activityDetails.maxSpeed;
        this.meanSpeed = activityDetails.meanSpeed;
        this.dateTime = activityDetails.dateTime;
    }

}
