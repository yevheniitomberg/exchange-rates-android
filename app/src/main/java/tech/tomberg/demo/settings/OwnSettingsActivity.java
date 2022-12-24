package tech.tomberg.demo.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import tech.tomberg.demo.R;

public class OwnSettingsActivity extends AppCompatActivity {

    public static final String ENABLE_DARK_THEME = "themeChanger";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_view);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_panel, new OwnSettingsFragment())
                .commit();
        //SwitchPreferenceCompat compat = findViewById(R.id.)
    }
}
