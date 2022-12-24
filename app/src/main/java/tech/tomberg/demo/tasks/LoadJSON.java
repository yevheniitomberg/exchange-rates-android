package tech.tomberg.demo.tasks;

import android.os.AsyncTask;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LoadJSON extends AsyncTask<URL, Void, JSONObject> {
    @Override
    protected JSONObject doInBackground(URL... urls) {
        String json = null;
        try {
            json = IOUtils.toString(urls[0], StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
