

# AndroidAPS Autotune adaptation

## Autotune Plugin files

### AutotunePlugin

<=> oref0-autotune.sh

<=> default value for Autotune log is **disable** (autotune always generate a dedicated log file in autotune subfolder)

### AutotuneFragment

User Interface

### AutotunePrep

<=> oref0-autotune-prep.js + lib/autotune-prep

### AutotuneCore

<=> oref0-autotune-core.js = lib/autotune

### AutotuneIob

<=> lib/iob + lib/meal 

get ns-entries and ns-treatments input data for each days

exports these data as json file to run oref0-autotune algorythm with aaps data in a Virtual Machine

Make dedicated Iob calculation for autotune (specificities compared to iob calculation for loop...)

### AutotuneFS

Manage exported files for tests and  verifications of autotune plugin

=> Add settings and autotune subfolder in androidAps log folder

=> Add a zip file beside log files for each autotune run with all generated data

### data folder

PreppedGlucose: output data of AutotunePrep and input data of AutotuneCore (it contains BGDatum, CRDatum data)

TunedProfile: for profile management and results

### oref0 folder

**oref0-autotune.sh**: main file used in Virtual Machine for autotune run (see oref0 in github)

**aaps-autotune.sh**: patched oref0-autotune.sh file to run oref0-autotune with aaps exported file (pumpprofile, ns-entries.yyyy-mm-dd.json and ns-treatments.yyyy-mm-dd.json). zip previous files and query are disabled (to check consistency between **autotune.yyyy-mm-dd.json** and **aaps-autotune.yyyy-mm-dd.json**)

**aaps1-autotune.sh**: patched oref0-autotune.sh file to run oref0-autotune-core with aaps-autotune.yyyy-mm-dd.json files



## Files generated by AAPS autotune plugin (for results verifications)

### Input files (for oref0 check)

- settings.json: contains all necessary information to be able to run oref0-autotune on a virtual machine (NS website, date of run, nb of days, timezone...)
- pumpprofile.json: current profile, (input data), converted in oref json format to be able to run oref0-autotune.sh (in settings folder and autotune folder)
- aaps-entries.yyyy-mm-dd.json (<=> ns-entries.yyyy-mm-dd.json): BG data get with line below and converted in NS json format 
- aaps-treatments.yyyy-mm-dd.json (<=> ns-entries.yyyy-mm-dd.json): all treatments for autotune calculation (meals, insulin, temp basals, extended basals). 

note oref0 autotune doesn't manage profile-switch so it could have some gap on iob calculation when no TBR running (calculation done with 4 hours average pumpprofile)

- It's important to be able to export in NS Json format all data used in aaps autotune plugin to be able to run oref0-autotune with the datas generated by aaps and compare results

**=> for testing results, I just have to copy input profile in settings folder (pumpprofile.json) and all aaps-entries and aaps-treatments files in autotune folder and run an ``aaps-autotune`` command (oref0-autotune.sh file with NS queries disabled), that way I can compared results and all detailled informations (aaps logs and oref0 logs are very close) and they must be exactly the same or as close as possible...**

- 

**=> for only AutotuneCore module validation, we also copy in autotune folder of virtual machine all aaps-autotune.yyyy-mm-dd.json and newaapsprofile.yyyy-mm-dd.json files and use ``aaps1-autotune`` command**

### Ouput files

- autotune.json: output profile in aaps json format (in settings folder)
- aaps-autotune.yyyy-mm-dd.json (<=> autotune.yyyy-mm-dd.json): prepped data for autotuneCore calculation (same format than oref0 one so I can use them in virtual machine to compare oref0-autotune-core results)

- newaapsprofile.yyyy-mm-dd.json (<=> newprofile.yyyy-mm-dd.json): intermediate profiles foreach day

## Files generated by OAPS: (for results verifications)

### inputs data

- ``profil.json`` and ``pumpprofile.json``:
  - insulin informations (if not default "rapid-acting" insulin) is specified in ``pumpprofile.json`` file with an additional entry ``"curve":"ultra-rapid"``.
  - Free peak insulin are entered in pumpprofile.json file with 2 additional entries:  ``"curve":"bilinear","insulinpeaktime":75 ``.
- ``ns-entries.yyyy-mm--dd.json``: BG data received from NSClient for each day (between 4 AM local hour and 4 AM following day). All BG are in reverse order (from end to start)

- ``ns-treatments.yyy-mm-dd.json``: Treatments data received from NSClient (6 hours before first BG data to last BG data). As query is done in UTC time on field "created_at", 12 hours extra data before and after are also downloaded...

### Output data

- ``autotune.yyy-mm-dd.json``: output file of  ``oref0-autotune-prep.js`` (and input of  ``oref0-autotune-core.js``). BG are grouped according to type (CSFGlucoseData, ISFGlucoseData or basalGlucoseData) with additional informations (mealCarbs, BGI, deviation)
- 

## Note for Insight user

- To have consistent data in AAPS database and in NS Web site settings must be:

  - NS upload only (disable sync): disable
  - No upload to NS: disable
  - Always use basal absolute values: disable

- The problem is with that config you don't have absolute values in your ns-treatment database for autotune calculation

  - As we test on a Virtual Machine, we have to "patch" autotune iob library:

    - file src/oref0/lib/iob/history.js line 319 (replace ``current.absolute`` by ``current.rate``)

    ```js
            } else if (current.eventType === "Temp Basal" && (current.enteredBy === "HAPP_App" || current.enteredBy === "openaps://AndroidAPS")) {
                var temp = {};
                //Patch for insight user
                //temp.rate = current.absolute;
                temp.rate = current.rate;
                temp.duration = current.duration;
                temp.timestamp = current.created_at;
                temp.started_at = new Date(tz(temp.timestamp));
                temp.date = temp.started_at.getTime();
                tempHistory.push(temp);
    ```

    => With this patch you can run oref0-autotune (I made several test, in my aaps-treatment exports I include both values, that way I'm able to run oref0 with aaps data even if iob file is patched or not, but for full compare, it's better to patch file)