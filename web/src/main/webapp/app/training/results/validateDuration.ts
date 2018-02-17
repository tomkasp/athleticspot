import {FormGroup} from "@angular/forms";

export function validateDuration(group: FormGroup){

    let isAtLeastOne = false;
    if (group && group.controls) {
        for (const control in group.controls) {
            if (group.controls.hasOwnProperty(control) && group.controls[control].valid && group.controls[control].value) {
                isAtLeastOne = true;
                break;
            }
        }
    }

    return isAtLeastOne ? null : { 'required': true };

}
