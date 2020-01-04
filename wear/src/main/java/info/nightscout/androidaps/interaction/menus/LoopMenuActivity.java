package info.nightscout.androidaps.interaction.menus;

import android.content.Intent;
import android.os.Bundle;

import info.nightscout.androidaps.data.ListenerService;
import info.nightscout.androidaps.interaction.actions.DisconnectActivity;
import info.nightscout.androidaps.interaction.actions.SuspendActivity;
import info.nightscout.androidaps.interaction.utils.MenuListActivity;

public class LoopMenuActivity extends MenuListActivity {

    @Override
    protected String[] getElements() {

        Bundle extras = getIntent().getExtras();
        boolean enabled = extras.getBoolean("enabled", false);
        boolean disconnected = extras.getBoolean("disconnected", false);
        boolean suspended = extras.getBoolean("suspended", false);
        int minToGo = extras.getInt("minToGo", -1);

        String[] elements;
        if (!enabled) {
            elements = new String[1];
            elements[0] = "Enable";
        }
        else if (disconnected || suspended) {
            elements = new String[1];
            elements[0] = "Resume";
        }
        else {
            elements = new String[3];
            elements[0] = "Disconnect";
            elements[1] = "Suspend";
            elements[2] = "Disable";
        }
        return elements;
    }

    @Override
    protected void doAction(String action) {

        Intent intent;
        if ("Disconnect".equals(action)) {
            intent = new Intent(this, DisconnectActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        } else if ("Suspend".equals(action)) {
            intent = new Intent(this, SuspendActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        } else if ("Disable".equals(action)) {
            ListenerService.initiateAction(this, "disable");
        } else if ("Resume".equals(action)) {
            ListenerService.initiateAction(this, "resume");
        } else if ("Enable".equals(action)) {
            ListenerService.initiateAction(this, "enable");
        }
    }
}