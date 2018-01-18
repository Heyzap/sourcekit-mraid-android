package org.nexage.sourcekit.mraiddemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.nexage.sourcekit.mraid.MRAIDInterstitial;
import org.nexage.sourcekit.mraid.MRAIDInterstitialListener;
import org.nexage.sourcekit.mraid.MRAIDNativeFeature;
import org.nexage.sourcekit.mraid.MRAIDNativeFeatureListener;
import org.nexage.sourcekit.mraid.MRAIDViewListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class InterstitialFragment extends ListFragment implements MRAIDInterstitialListener, MRAIDNativeFeatureListener {

    private final static String TAG = "InterstitialFragment";
    private final static String PREFIX = "interstitial";

    private MRAIDInterstitial mraidInterstitial;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                inflater.getContext(),
                android.R.layout.simple_list_item_1,
                getTestFiles());
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String name = (String)getListAdapter().getItem(position);
        Log.d(TAG, name);
        String filename = PREFIX + "." + name + ".html";
        String content = null;
        try {
            InputStream is = getResources().getAssets().open(filename);
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = bufferReader.readLine()) != null) {
                // Log.d(TAG, line);
                if (!TextUtils.isEmpty(line.trim())) {
                    sb.append(line).append(System.getProperty("line.separator"));
                }
            }
            bufferReader.close();
            content = sb.toString();

            // For demo purposes, we will support all the MRAID native features.
            // The demo's Android manifest file also needs to contain the associated
            // uses-permission elements.
            String[] supportedNativeFeatures = {
                    MRAIDNativeFeature.CALENDAR,
                    MRAIDNativeFeature.INLINE_VIDEO,
                    MRAIDNativeFeature.SMS,
                    MRAIDNativeFeature.STORE_PICTURE,
                    MRAIDNativeFeature.TEL,
            };

            final String baseUrl = "file:///android_asset/";

            mraidInterstitial = new MRAIDInterstitial(
                    getActivity(),
                    baseUrl,
                    content,
                    supportedNativeFeatures,
                    (MRAIDViewListener) this,
                    this);

            Log.d(TAG, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] getTestFiles() {
        String[] files = null;
        List<String> testFiles = new ArrayList<String>();
        try {
            files = getResources().getAssets().list("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String file = files[i];
                if (file.startsWith(PREFIX)) {
                    String displayName = file.replace(PREFIX + ".", "");
                    displayName = displayName.replace(".html",  "");
                    Log.d(TAG, files[i]);
                    testFiles.add(displayName);
                }
            }
        }
        return testFiles.toArray(new String[testFiles.size()]);
    }

    // MRAIDInterstitialListener implementation

    @Override
    public void mraidInterstitialLoaded(MRAIDInterstitial mraidInterstitial) {
        Log.d(TAG, "mraidInterstitialLoaded");
        // Show the interstitial ad as soon as it is loaded.
        // In real life, we would probably wait until a more appropriate point of time.
        this.mraidInterstitial.show(getActivity());
    }

    @Override
    public void mraidInterstitialShow(MRAIDInterstitial mraidInterstitial) {
        Log.d(TAG, "mraidInterstitialShow");
    }

    @Override
    public void mraidInterstitialHide(MRAIDInterstitial mraidInterstitial) {
        Log.d(TAG, "mraidInterstitialHide");
        // Help out the garbage collector.
        this.mraidInterstitial = null;
    }

    // MRAIDNativeFeatureListener implementation

    @Override
    public void mraidNativeFeatureCallTel(String url) {
        Log.d(TAG, "mraidNativeFeatureCallTel " + url);
    }

    @Override
    public void mraidNativeFeatureCreateCalendarEvent(String eventJSON) {
        Log.d(TAG, "mraidNativeFeatureCreateCalendarEvent " + eventJSON);
    }

    @Override
    public void mraidNativeFeaturePlayVideo(String url) {
        Log.d(TAG, "mraidNativeFeaturePlayVideo " + url);
    }

    @Override
    public void mraidNativeFeatureOpenBrowser(String url) {
        Log.d(TAG, "mraidNativeFeatureOpenBrowser " + url);

        // Demo will open the URL in an external browser
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(url)));
    }

    @Override
    public void mraidNativeFeatureStorePicture(String url) {
        Log.d(TAG, "mraidNativeFeatureStorePicture " + url);
    }

    @Override
    public void mraidNativeFeatureSendSms(String url) {
        Log.d(TAG, "mraidNativeFeatureSendSms " + url);
    }

}