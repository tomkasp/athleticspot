import {Component, OnInit} from "@angular/core";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import * as moment from 'moment';
import {ToasterService} from "angular2-toaster";
import {StravaDataservice} from "./strava.dataservice";
import {ActivitiesDataservice} from "../../shared/activites/activities.dataservice";
import {SportActivityModel} from "../../shared/activites/sport-activity.model";
import {TrackerSource} from "../../shared/activites/tracker-source";
import {MapsAPILoader} from "@agm/core";


@Component({
    selector: 'athleticspot-activities',
    templateUrl: './activities.component.html',
    styleUrls: ['./activities.component.css']
})
export class ActivitiesComponent implements OnInit {

    private activities = [];
    private addActivityForm: FormGroup;
    private showTimeline = false;
    private pageLoading = false;
    private stravaActivationUrl: String;
    private pageCount: 0;
    private currentPage = 0;
    private trackerSource: TrackerSource;

    public maxSpeed: number;
    public zoom: number;

    constructor(private activityDataservice: ActivitiesDataservice,
                private stravaDataservice: StravaDataservice,
                private toasterService: ToasterService,
                private mapsAPILoader: MapsAPILoader) {
    }

    ngOnInit(): void {
        //set google maps defaults
        this.zoom = 14;
        //load Places Autocomplete
        this.mapsAPILoader.load().then(() => {
        });

        this.trackerSource = TrackerSource.MANUAL;
        this.addActivityForm = new FormGroup({
            'title': new FormControl(null, [Validators.required]),
            'description': new FormControl(),
            'sportActivityType': new FormControl("RUN", [Validators.required]),
            'duration': new FormGroup({
                "hours": new FormControl(0),
                "minutes": new FormControl(0),
                "seconds": new FormControl(0)
            }),
            'distance': new FormControl(),
            'units': new FormControl("km"),
            'startDate': new FormControl(new Date()),
            'time': new FormControl("0", [Validators.required]),
            'maxSpeed': new FormControl(),
            'averageSpeed': new FormControl()
        });
        this.refreshActivities();
        this.stravaDataservice.fetchStravaActivationLink()
            .subscribe(value => {
                this.stravaActivationUrl = value;
            })
    }

    submitActivity() {
        if (this.addActivityForm.valid) {
            let activity = this.addActivityForm.value as SportActivityModel;
            activity.trackerSource = TrackerSource.MANUAL;
            activity.startDate = moment(this.addActivityForm.get('startDate').value)
                .startOf('day')
                .add(this.addActivityForm.get('time').value,
                    'hours')
                .format("YYYY-MM-DDTHH:mm:ss");

            activity.duration =
                this.addActivityForm.get("duration.hours").value * 36000 + +
                this.addActivityForm.get("duration.minutes").value * 60 +
                this.addActivityForm.get("duration.seconds").value;
            this.activityDataservice.createActivity(activity).subscribe(isSuccess => {
                this.refreshActivities();
                this.toasterService.pop('success', 'Activity', 'Activity created successfully');
            }, error => {
                this.toasterService.pop('error', 'Activity', 'Activity create failed');
                console.log(error);
            });
            this.refreshAddActivityForm();
        } else {
            this.addActivityForm.markAsDirty({onlySelf: true});
            this.markFormGroupTouchedAndDirty(this.addActivityForm)
        }
    }

    private refreshActivities() {
        this.showTimeline = false;
        this.currentPage = 0;
        this.activities = [];
        this.activityDataservice.fetchActivityPaged(this.currentPage).subscribe((activitiesPage: any) => {
                this.pageCount = activitiesPage.totalPages;
                activitiesPage.content.forEach(sportActivity => {
                    this.activities.push(this.assambleSportActivity(sportActivity));
                });
                this.showTimeline = true;
            }, error => {
                this.showTimeline = true;
                this.toasterService.pop('error', 'Activity', 'Error during fetching activities');
            }
        );
    }

    /**
     * Marks all controls in a form group as touched
     * @param formGroup - The group to caress..hah
     */
    private markFormGroupTouchedAndDirty(formGroup: FormGroup) {
        (<any>Object).values(formGroup.controls).forEach(control => {
            control.markAsTouched();
            control.markAsDirty();

            if (control.controls) {
                this.markFormGroupTouchedAndDirty(control);
            }
        });
    }

    private refreshAddActivityForm() {
        this.addActivityForm.reset();
        this.addActivityForm.patchValue({
            sportActivityType: "RUN",
            unit: "km",
            time: "0",
            duration: {
                hours: 0,
                minutes: 0,
                seconds: 0
            }
        })
    }

    // google api component:


    private assambleSportActivity(sportActivity: any) {
        let sportActivityModel = new SportActivityModel();
        sportActivityModel.id = sportActivity.id;
        sportActivityModel.firstAndLastName = sportActivity.firstAndLastName;
        sportActivityModel.trackerSource = sportActivity.trackerSource;
        sportActivityModel.sportActivityType = sportActivity.sportActivityType;
        sportActivityModel.title = sportActivity.title;
        sportActivityModel.description = sportActivity.description;
        sportActivityModel.distance = sportActivity.distance;
        sportActivityModel.units = sportActivity.units;
        sportActivityModel.movingTime = sportActivity.movingTime;
        sportActivityModel.elapsedTime = sportActivity.elapsedTime;
        sportActivityModel.startDate = moment(sportActivity.startDate).format("LLL");
        sportActivityModel.averageSpeed = sportActivity.averageSpeed;
        sportActivityModel.maxSpeed = sportActivity.maxSpeed;
        sportActivityModel.averageTemp = sportActivity.averageTemp;
        sportActivityModel.calories = sportActivity.calories;
        sportActivityModel.coordinates = sportActivity.coordinates;
        return sportActivityModel;

    }

    onScroll() {
        console.log("scrolled!!!!");
        if (this.currentPage < this.pageCount - 1) {
            this.currentPage++;
            console.log("Page count: " + this.pageCount);
            console.log("Current page: " + this.currentPage);
            this.pageLoading = true;
        } else {
            return;
        }
        this.activityDataservice.fetchActivityPaged(this.currentPage).subscribe((activitiesPage: any) => {
                activitiesPage.content.forEach(sportActivity => {
                    this.activities.push(this.assambleSportActivity(sportActivity));
                    this.pageLoading = false;
                });
            }, error => {
                this.toasterService.pop('error', 'Activity', 'Error during fetching activities');
                this.pageLoading = false;
            }
        );
    }
}
