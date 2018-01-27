package com.softkey.mazno2.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


import com.softkey.mazno2.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by afarag on 7/3/2015.
 */
public class Utils {

    private static Pattern pattern;
    private static Matcher matcher;
    private static String spinnerValue;
    //Email Pattern
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static boolean isNotEmpty(List obj) {
        return obj != null && obj.size() != 0;
    }

    public static boolean isNotEmpty(String obj) {
        return obj != null && obj.trim().length() != 0;
    }

    public static boolean isNotEmpty(Object obj) {
        return obj != null;
    }

    public static Boolean isEmptyString(String str) {
        if (str == null || str.equals(""))
            return true;
        return false;
    }

    public static boolean isNotEmpty(Long obj) {
        return obj != null && obj.toString().length() != 0;
    }

    public static boolean isNotEmpty(Object[] obj) {
        return obj != null && obj.length != 0;
    }

    public static boolean isEmpty(Object obj) {
        return obj == null;
    }

    public static boolean isEmpty(List obj) {
        return obj == null || obj.size() == 0;
    }

    public static Boolean isCharacter(Character ch) {
        return true ? (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') : false;
    }

    public static void makeShortToast(String msg, Context cxt) {
        Toast.makeText(cxt, msg, Toast.LENGTH_SHORT).show();
    }

    public static void makeLongToast(String msg, Context cxt) {
        Toast.makeText(cxt, msg, Toast.LENGTH_LONG).show();
    }

    public static boolean validate(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String getCurrentDate() {
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date cal = new Date(System.currentTimeMillis());
        return sdf.format(cal);
    }

    public static void navigateTo(Context from, Class to) {
        Intent intent = new Intent(from, to);
        from.startActivity(intent);
    }

    public static void navigateWithData(Context from, Class to, String key, Object value) {
        Intent intent = new Intent(from, to);
        intent.putExtra(key, value + "");
        from.startActivity(intent);
    }

    public static void navigateWithMultiableData(Context from, Class to, Map<String, Object> param) {
        Intent intent = new Intent(from, to);
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue() + "");
        }
        from.startActivity(intent);
    }

    public static boolean checkMobileInternetConn(Context context, int type) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = null;
        if (connectivity != null) {
            info = connectivity.getNetworkInfo(type);

        }
        return info.isConnected();
    }

    public static void showAlertSystemDialog(final Context context, String message, final String intintType) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(intintType));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public static void turnGPSOn(Context ctx) {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        ctx.sendBroadcast(intent);

        String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            ctx.sendBroadcast(poke);
        }
    }

    public static void turnGPSOff(Context ctx) {
        String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (provider.contains("gps")) { //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            ctx.sendBroadcast(poke);
        }
    }

   /* public static String buildSpinner(Context ctx, Customer model, List<Customer> modelList, Spinner spinner, ArrayAdapter<Customer> adapter){
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Customer emp = (Customer) parent.getSelectedItem();
                spinnerValue = emp.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return spinnerValue;
    }*/

    public static Dialog showLoadingDialog(Context ctx) {
        final Dialog dialog = new Dialog(ctx, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading_dialog);
        return dialog;
    }

}
