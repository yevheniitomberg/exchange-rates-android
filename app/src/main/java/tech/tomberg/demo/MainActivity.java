package tech.tomberg.demo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import org.json.JSONObject;

import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;

import tech.tomberg.demo.filter.DecimalDigitsInputFilter;
import tech.tomberg.demo.filter.InputNumberFilter;
import tech.tomberg.demo.settings.OwnSettingsActivity;
import tech.tomberg.demo.settings.OwnSettingsFragment;
import tech.tomberg.demo.tasks.LoadJSON;

public class MainActivity extends AppCompatActivity {

    public final String FRANKFURT_ROOT="https://api.frankfurter.app/";

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadCurrencies();
        loadSpinners();
        checkPreferences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings_id) {
            startActivity(new Intent(MainActivity.this, OwnSettingsActivity.class));
            return true;
        }
        return true;
    }

    public void loadCurrencies() {
        LoadJSON loadJSON = new LoadJSON();
        LinkedList<String> strings = new LinkedList<>();
        try {
            loadJSON.execute(new URL(FRANKFURT_ROOT+"currencies"));
            Iterator<String> currencies = loadJSON.get().keys();
            while (currencies.hasNext()) {
                LoadJSON loadEmoji = new LoadJSON();
                String next = currencies.next();
                loadEmoji.execute(new URL("https://api.tomberg.tech/currency/emoji/" + next));
                strings.add(loadEmoji.get().getString("emoji") + " " + next);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Spinner spinner_from = findViewById(R.id.currency_from_icons);
        Spinner spinner_to = findViewById(R.id.currency_to_icons);
        Spinner spinner_converter = findViewById(R.id.currency_converter);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, strings);

        spinner_converter.setAdapter(dataAdapter);
        spinner_converter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadBasedOnSpecificCurrency(spinner_converter.getSelectedItem().toString().split(" ")[1]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_from.setAdapter(dataAdapter);
        spinner_to.setAdapter(dataAdapter);
    }

    public void loadBasedOnSpecificCurrency(String base) {
        TableLayout tableLayout = findViewById(R.id.tableCurrencies);
        tableLayout.removeAllViews();
        LoadJSON loadJSON = new LoadJSON();
        try {
            loadJSON.execute(new URL(FRANKFURT_ROOT+"latest?base=" + base));
            JSONObject rates = loadJSON.get().getJSONObject("rates");
            Iterator<String> currencies = rates.keys();
            while (currencies.hasNext()) {
                LoadJSON loadEmoji = new LoadJSON();
                String next = currencies.next();
                loadEmoji.execute(new URL("https://api.tomberg.tech/currency/emoji/" + next));

                TableRow row = new TableRow(this);
                TextView currency = new TextView(this);
                currency.setTextSize(TypedValue.COMPLEX_UNIT_PT, 14);
                currency.setText(loadEmoji.get().getString("emoji") + " " + next);
                row.addView(currency);

                TextView currencyValue = new TextView(this);
                currencyValue.setTextSize(TypedValue.COMPLEX_UNIT_PT, 14);
                currencyValue.setText(rates.getString(next));
                row.addView(currencyValue);
                row.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                row.setGravity(Gravity.CENTER);
                tableLayout.addView(row);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void loadSpinners() {
        Spinner spinner = findViewById(R.id.currency_converter);
        EditText from = findViewById(R.id.count_from);
        TextView to = findViewById(R.id.count_to);
        from.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2), new InputNumberFilter(1, 99999)});
        to.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(10, 2)});
        loadBasedOnSpecificCurrency(spinner.getSelectedItem().toString().split(" ")[1]);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Spinner spinner_from = findViewById(R.id.currency_from_icons);
                Spinner spinner_to = findViewById(R.id.currency_to_icons);
                String from_currency = spinner_from.getSelectedItem().toString().split(" ")[1];
                String to_currency = spinner_to.getSelectedItem().toString().split(" ")[1];

                if (from_currency.equals(to_currency)) {
                    to.setText(charSequence);
                } else if (from.getText().toString().equals("")) {
                    to.setText("");
                } else {
                    LoadJSON loadJSON = new LoadJSON();
                    try {
                        loadJSON.execute(new URL(FRANKFURT_ROOT + "latest?from=" + from_currency + "&to=" + to_currency + "&amount=" + charSequence));
                        JSONObject jsonObject = loadJSON.get();
                        to.setText(jsonObject.getJSONObject("rates").getString(to_currency));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        from.addTextChangedListener(textWatcher);
    }

    public void checkPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        checkDarkTheme(prefs, OwnSettingsActivity.ENABLE_DARK_THEME);
        listener = MainActivity::checkDarkTheme;
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void checkDarkTheme(SharedPreferences prefs1, String key) {
        if (key.equals(OwnSettingsActivity.ENABLE_DARK_THEME)) {
            if (prefs1.getBoolean(OwnSettingsActivity.ENABLE_DARK_THEME, true)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }
}