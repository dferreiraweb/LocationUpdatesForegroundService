/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.location.sample.locationupdatesforegroundservice;


import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

class Utils {

    static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates";
    private static Retrofit retrofit;


    /**
     * Retorna true se solicitar atualizações de local, caso contrário, retorna false.
     *
     * @param context The {@link Context}.
     */
    static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    /**
     * Armazena o estado das atualizações de localização em SharedPreferences.
     * @param requestingLocationUpdates O local atualiza o estado.
     */
    static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply();
    }

    /**
     * Returns the {@code location} object as a human readable string.
     * @param location  The {@link Location}.
     */
    static String getLocationText(Location location) {
        sendToServer(location);
        return location == null ? "Sinal de GPS perdido" :
                "( Lat: " + location.getLatitude() + ", Long: " + location.getLongitude() + ")";
    }

    static String getLocationTitle(Context context) {
        return context.getString(R.string.location_updated,
                DateFormat.getDateTimeInstance().format(new Date()));
    }


    /**
     *
     * @param location retorna uma url para ser enviada ao link de teste
     */

    static void sendToServer(Location location) {

        String baseUrl = "https://quasar-test-two.free.beeceptor.com";

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

       intialize(location.getLatitude()+"", location.getLongitude()+"");
       Log.d("LOCATION:", retrofit.baseUrl().toString());
    }

    /**
     * Metodo responsável por fazer a requisição no servidor
     * @param latitude retorna a latitude do gps
     * @param longitude retorna a longitude do gps
     */
    static void intialize(String latitude, String longitude) {

        //Treço para criar e imprimir a data em que o usuario fizer uma requisição do metodo
        Calendar currentTime = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone("America/Sao_Paulo");
        currentTime.setTimeZone(tz);
        Date date = currentTime.getTime();
        String time = android.text.format.DateFormat.format("dd-MM-yyyy---hh-mm-ss", date).toString();
        Log.d("LOCATION", "DATE: " + android.text.format.DateFormat.format("dd-MM-yyyy-hh:mm:ss", date));

        Call<JSONObject> call = new Utils().getRetrofitService().carregarDadosLocation(latitude, longitude, time);

        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                Log.d("LOCATION RESPONSE: ", response.code()+" --- Message:" + response.message());
                if (response.isSuccessful()) {
                    Log.d("LOCATION ERROR", "Sucesso na requisição: " + response.body());
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        Log.d("LOCATION:", jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
            }
        });

    }

    public RetrofitService getRetrofitService() {
        return retrofit.create(RetrofitService.class);
    }




}
