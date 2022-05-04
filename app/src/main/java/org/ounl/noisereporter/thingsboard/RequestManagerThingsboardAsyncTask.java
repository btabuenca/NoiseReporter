package org.ounl.noisereporter.thingsboard;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.EOFException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class RequestManagerThingsboardAsyncTask extends AsyncTask<JSONObject, Void, String> {

        private String CLASSNAME = this.getClass().getName();

        @Override
        protected String doInBackground(JSONObject... params) {

            try {
                JSONObject jsonObject = params[0];
                Log.i(CLASSNAME, "Lunch command ["+jsonObject.toString()+"]");
                run(jsonObject);
                Log.i(CLASSNAME, "Command launched! ");

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return "Executed ";
        }


        private void run(JSONObject jo) {

            HttpsURLConnection connection = null;
            //HttpURLConnection connection = null;

            try {

                URL url = new URL("https://thingsboard.cloud/api/v1/nUw81FzSm8uAbzbok8xx/telemetry");
                connection = (HttpsURLConnection) url.openConnection();

                //URL url = new URL("http://iot.etsisi.upm.es:8080/api/v1/jpDpp5e5MPkqnbRXj7CL/telemetry");
                //connection = (HttpURLConnection) url.openConnection();

                Log.i(CLASSNAME, "Lunch command on url "+url.toString());
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
                osw.write(jo.toString());
                osw.flush();
                osw.close();

                connection.connect();
                connection.getResponseCode();
                Log.i(CLASSNAME, "Response code ["+connection.getResponseCode()+"]");

            } catch (EOFException e) {
                Log.e(CLASSNAME, "There seems to be some bug reading stream from Thingsboard");
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                if (connection != null) {
                    connection.disconnect();
                }
                e.printStackTrace();
            }finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println("On poste execute: " + result);
        }

        @Override
        protected void onPreExecute() {
            System.out.println("On pre execute.");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            System.out.println("On progress update.");
        }
    }