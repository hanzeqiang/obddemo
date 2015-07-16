package com.skywin.obd.remote.activity;

import java.util.ArrayList;

import pt.lighthouselabs.obd.commands.ObdCommand;
import pt.lighthouselabs.obd.enums.ObdProtocols;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.github.pires.obd.reader.config.ObdConfig;
import com.skywin.obd.remote.R;

/**
 * Configuration com.github.pires.obd.reader.activity.
 */
public class ConfigActivity extends PreferenceActivity implements OnPreferenceChangeListener {

  public static final String UPLOAD_SERVER_KEY = "upload_server_preference";
  public static final String UPLOAD_DATA_KEY = "upload_data_preference";
  public static final String OBD_UPDATE_PERIOD_KEY = "obd_update_period_preference";
  public static final String SERVER_ID_KEY = "server_id_preference";
  public static final String ENGINE_DISPLACEMENT_KEY = "engine_displacement_preference";
  public static final String VOLUMETRIC_EFFICIENCY_KEY = "volumetric_efficiency_preference";
  public static final String IMPERIAL_UNITS_KEY = "imperial_units_preference";
  public static final String COMMANDS_SCREEN_KEY = "obd_commands_screen";
  public static final String PROTOCOLS_LIST_KEY = "obd_protocols_preference";
  public static final String ENABLE_GPS_KEY = "enable_gps_preference";
  //public static final String GPS_UPDATE_PERIOD_KEY = "gps_update_period_preference";
  //public static final String GPS_DISTANCE_PERIOD_KEY = "gps_distance_period_preference";
  public static final String ENABLE_BT_KEY = "enable_bluetooth_preference";
  public static final String MAX_FUEL_ECON_KEY = "max_fuel_econ_preference";
  public static final String CONFIG_READER_KEY = "reader_config_preference";

  /**
   * @param prefs
   * @return
   */
  public static int getObdUpdatePeriod(SharedPreferences prefs) {
    String periodString = prefs
        .getString(ConfigActivity.OBD_UPDATE_PERIOD_KEY, "4"); // 4 as in seconds
    int period = 4000; // by default 4000ms

    try {
      period = Integer.parseInt(periodString) * 1000;
    } catch (Exception e) {
    }

    if (period <= 0) {
      period = 250;
    }

    return period;
  }

  /**
   * @param prefs
   * @return
   */
  public static double getVolumetricEfficieny(SharedPreferences prefs) {
    String veString = prefs.getString(ConfigActivity.VOLUMETRIC_EFFICIENCY_KEY, ".85");
    double ve = 0.85;
    try {
      ve = Double.parseDouble(veString);
    } catch (Exception e) {
    }
    return ve;
  }

  /**
   * @param prefs
   * @return
   */
  public static double getEngineDisplacement(SharedPreferences prefs) {
    String edString = prefs.getString(ConfigActivity.ENGINE_DISPLACEMENT_KEY, "1.6");
    double ed = 1.6;
    try {
      ed = Double.parseDouble(edString);
    } catch (Exception e) {
    }
    return ed;
  }

  /**
   * @param prefs
   * @return
   */
  public static ArrayList<ObdCommand> getObdCommands(SharedPreferences prefs) {
    ArrayList<ObdCommand> cmds = ObdConfig.getCommands();
    ArrayList<ObdCommand> ucmds = new ArrayList<>();
    for (int i = 0; i < cmds.size(); i++) {
      ObdCommand cmd = cmds.get(i);
      boolean selected = prefs.getBoolean(cmd.getName(), true);
      if (selected)
        ucmds.add(cmd);
    }
    return ucmds;
  }

  /**
   * @param prefs
   * @return
   */
  public static double getMaxFuelEconomy(SharedPreferences prefs) {
    String maxStr = prefs.getString(ConfigActivity.MAX_FUEL_ECON_KEY, "70");
    double max = 70;
    try {
      max = Double.parseDouble(maxStr);
    } catch (Exception e) {
    }
    return max;
  }

  /**
   * @param prefs
   * @return
   */
  public static String[] getReaderConfigCommands(SharedPreferences prefs) {
    String cmdsStr = prefs.getString(CONFIG_READER_KEY, "atsp0\natz");
    String[] cmds = cmdsStr.split("\n");
    return cmds;
  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    /*
     * Read preferences resources available at res/xml/preferences.xml
     */
    addPreferencesFromResource(R.xml.preferences);

//    checkGps();

//    ArrayList<CharSequence> pairedDeviceStrings = new ArrayList<>();
    ArrayList<CharSequence> vals = new ArrayList<>();
//    ListPreference listBtDevices = (ListPreference) getPreferenceScreen()
//        .findPreference(NETWORK_LIST_KEY);
    
    ArrayList<CharSequence> protocolStrings = new ArrayList<>();
    ListPreference listProtocols = (ListPreference) getPreferenceScreen()
              .findPreference(PROTOCOLS_LIST_KEY);
    String[] prefKeys = new String[]{ENGINE_DISPLACEMENT_KEY,
        VOLUMETRIC_EFFICIENCY_KEY, OBD_UPDATE_PERIOD_KEY, MAX_FUEL_ECON_KEY};
    for (String prefKey : prefKeys) {
      EditTextPreference txtPref = (EditTextPreference) getPreferenceScreen()
          .findPreference(prefKey);
      txtPref.setOnPreferenceChangeListener(this);
    }

    /*
     * Available OBD commands
     *
     * TODO This should be read from preferences database
     */
    ArrayList<ObdCommand> cmds = ObdConfig.getCommands();
    PreferenceScreen cmdScr = (PreferenceScreen) getPreferenceScreen()
        .findPreference(COMMANDS_SCREEN_KEY);
    for (ObdCommand cmd : cmds) {
      CheckBoxPreference cpref = new CheckBoxPreference(this);
      cpref.setTitle(cmd.getName());
      cpref.setKey(cmd.getName());
      cpref.setChecked(true);
      cmdScr.addPreference(cpref);
    }

    /*
     * Available OBD protocols
     *
     */
    for (ObdProtocols protocol: ObdProtocols.values()){
          protocolStrings.add(protocol.name());
    }
    listProtocols.setEntries(protocolStrings.toArray(new CharSequence[0]));
    listProtocols.setEntryValues(protocolStrings.toArray(new CharSequence[0]));

    /*
     * Let's use this device Bluetooth adapter to select which paired OBD-II
     * compliant device we'll use.
     */
//    final BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
//    if (mBtAdapter == null) {
//      listBtDevices
//          .setEntries(pairedDeviceStrings.toArray(new CharSequence[0]));
//      listBtDevices.setEntryValues(vals.toArray(new CharSequence[0]));
//
//      // we shouldn't get here, still warn user
//      Toast.makeText(this, "This device does not support Bluetooth.",
//          Toast.LENGTH_LONG).show();
//
//      return;
//    }

    /*
     * Listen for preferences click.
     *
     * TODO there are so many repeated validations :-/
     */
//    final Activity thisActivity = this;
//    listBtDevices.setEntries(new CharSequence[1]);
//    listBtDevices.setEntryValues(new CharSequence[1]);
//    listBtDevices.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//      public boolean onPreferenceClick(Preference preference) {
//    	  System.out.println("test");
////    	  preference.get
////    	  Editor ed = preference.getEditor();
////    	  ed
//        return true;
//      }
//    });

    /*
     * Get paired devices and populate preference list.
     */
    //Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
//    savedInstanceState.get
//    if (pairedDevices.size() > 0) {
//      for (BluetoothDevice device : pairedDevices) {
//        pairedDeviceStrings.add(device.getName() + "\n" + device.getAddress());
//        vals.add(device.getAddress());
//      }
//    }
//    listBtDevices.setEntries(pairedDeviceStrings.toArray(new CharSequence[0]));
//    listBtDevices.setEntryValues(vals.toArray(new CharSequence[0]));
  }

  public static double getOnlineListString(SharedPreferences prefs) {
    String maxStr = prefs.getString(ConfigActivity.MAX_FUEL_ECON_KEY, "70");
    double max = 70;
    try {
      max = Double.parseDouble(maxStr);
    } catch (Exception e) {
    }
    return max;
  }

  /**
   * OnPreferenceChangeListener method that will validate a preferencen new
   * value when it's changed.
   *
   * @param preference the changed preference
   * @param newValue   the value to be validated and set if valid
   */
  public boolean onPreferenceChange(Preference preference, Object newValue) {
    if (OBD_UPDATE_PERIOD_KEY.equals(preference.getKey())
        || VOLUMETRIC_EFFICIENCY_KEY.equals(preference.getKey())
        || ENGINE_DISPLACEMENT_KEY.equals(preference.getKey())
        || OBD_UPDATE_PERIOD_KEY.equals(preference.getKey())
        || MAX_FUEL_ECON_KEY.equals(preference.getKey())) {
      try {
        Double.parseDouble(newValue.toString());
        return true;
      } catch (Exception e) {
        Toast.makeText(this,
            "Couldn't parse '" + newValue.toString() + "' as a number.",
            Toast.LENGTH_LONG).show();
      }
    }
    return false;
  }

//  private void checkGps() {
//    LocationManager mLocService = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//    if(mLocService != null ) {
//      LocationProvider mLocProvider = mLocService.getProvider(LocationManager.GPS_PROVIDER);
//      if (mLocProvider == null) {
//        hideGPSCategory();
//      }
//    }
//  }
//
//  private void hideGPSCategory() {
//    PreferenceScreen preferenceScreen = getPreferenceScreen();
//    PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference(getResources().getString(R.string.pref_gps_category));
//    if(preferenceCategory != null) {
//      preferenceCategory.removeAll();
//      preferenceScreen.removePreference((Preference) preferenceCategory);
//    }
//  }
}