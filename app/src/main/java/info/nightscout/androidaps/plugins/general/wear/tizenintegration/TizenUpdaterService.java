package info.nightscout.androidaps.plugins.general.wear.tizenintegration;

import info.nightscout.androidaps.interfaces.PluginType;
import info.nightscout.androidaps.plugins.general.wear.WearPlugin;
import info.nightscout.androidaps.utils.SP;

public class TizenUpdaterService {
    public static final String ACTION_RESEND = TizenUpdaterService.class.getName().concat(".Resend");
    public static final String ACTION_OPEN_SETTINGS = TizenUpdaterService.class.getName().concat(".OpenSettings");
    public static final String ACTION_SEND_STATUS = TizenUpdaterService.class.getName().concat(".SendStatus");
    public static final String ACTION_SEND_BASALS = TizenUpdaterService.class.getName().concat(".SendBasals");
    public static final String ACTION_SEND_BOLUSPROGRESS = TizenUpdaterService.class.getName().concat(".BolusProgress");
    public static final String ACTION_SEND_ACTIONCONFIRMATIONREQUEST = TizenUpdaterService.class.getName().concat(".ActionConfirmationRequest");
    public static final String ACTION_SEND_CHANGECONFIRMATIONREQUEST = TizenUpdaterService.class.getName().concat(".ChangeConfirmationRequest");
    public static final String ACTION_CANCEL_NOTIFICATION = TizenUpdaterService.class.getName().concat(".CancelNotification");

    // Draft, to be updated with Tizen library
    public static final String WEARABLE_DATA_PATH = "/nightscout_watch_data";
    public static final String WEARABLE_RESEND_PATH = "/nightscout_watch_data_resend";
    private static final String WEARABLE_CANCELBOLUS_PATH = "/nightscout_watch_cancel_bolus";
    public static final String WEARABLE_CONFIRM_ACTIONSTRING_PATH = "/nightscout_watch_confirmactionstring";
    public static final String WEARABLE_INITIATE_ACTIONSTRING_PATH = "/nightscout_watch_initiateactionstring";

    private static final String OPEN_SETTINGS_PATH = "/openwearsettings";
    private static final String NEW_STATUS_PATH = "/sendstatustowear";
    private static final String NEW_PREFERENCES_PATH = "/sendpreferencestowear";
    public static final String BASAL_DATA_PATH = "/nightscout_watch_basal";
    public static final String BOLUS_PROGRESS_PATH = "/nightscout_watch_bolusprogress";
    public static final String ACTION_CONFIRMATION_REQUEST_PATH = "/nightscout_watch_actionconfirmationrequest";
    public static final String ACTION_CHANGECONFIRMATION_REQUEST_PATH = "/nightscout_watch_changeconfirmationrequest";
    public static final String ACTION_CANCELNOTIFICATION_REQUEST_PATH = "/nightscout_watch_cancelnotificationrequest";

    boolean wear_integration = false;

    public void setSettings() {
        wear_integration = WearPlugin.getPlugin().isEnabled(PluginType.GENERAL);
        // Log.d(TAG, "WR: wear_integration=" + wear_integration);
        if (wear_integration && SP.getBoolean("tizenenable", false)) {
            // googleApiConnect();
        }
    }

    public void listenForChangeInSettings() { WearPlugin.registerTizenUpdaterService(this); }


}
