package com.foodprotect.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.foodprotect.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Checkout extends Activity
{
    Place shipplace;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    TextView textView,textView2
            ,textView3;
    Button button;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);
        textView=(TextView)findViewById(R.id.e2);
        textView2=(TextView)findViewById(R.id.e3);
        textView3=(TextView)findViewById(R.id.button1);
        button=(Button)findViewById(R.id.button2);
        final PlaceAutocompleteFragment placeAutocompleteFragment=(PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.fragemn1);
        placeAutocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        ((EditText)placeAutocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input))
                .setHint("Enter Village Name");
        ((EditText)placeAutocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input))
                .setTextSize(18);
        final  ShowFragment tv=new ShowFragment();
        final FragmentManager fm=getFragmentManager();

        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv.show(fm,"TV_tag");



            }
        });
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place)
            {
                shipplace=place;
                textView.setTextSize(17);
                textView.setText("Address: "+shipplace.getAddress().toString());
                name=shipplace.getName().toString();
                textView2.setText("Name: "+name);
//                if(movie.getName().equals(name))
//                    Toast.makeText(Checkout.this, "jnjnc", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(Status status) {
                Log.e("Error",status.getStatusMessage());
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name!= null && !TextUtils.isEmpty(name)&&!name.equals("null"))
                    new SigninActivity(Checkout.this).execute(name);
                else
                    Toast.makeText(Checkout.this, "Please Enter Village name", Toast.LENGTH_SHORT).show();
            }
        });

    }

private class SigninActivity  extends AsyncTask<String,Void,String> {
    private Context context;
    public static final String REQUEST_METHOD = "GET";

    //flag 0 means get and 1 means post.(By default it is get.)
    public SigninActivity(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... params) {
        String name = params[0];
        String result;
        String inputLine;

        try {
            Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
            String link = "https://vikasbajpayee.000webhostapp.com/villagesapi.php?name=" + name;
            URL myUrl = new URL(link);
            //Create a connection
            HttpURLConnection connection = (HttpURLConnection)
                    myUrl.openConnection();
            //Set methods and timeouts
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            //Connect to our url
            connection.connect();
            //Create a new InputStreamReader
            InputStreamReader streamReader = new
                    InputStreamReader(connection.getInputStream());
            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            //Check if the line we are reading is not null
            while ((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();
            //Set our result equal to our stringBuilder
            result = stringBuilder.toString();
        }catch(Exception e){
                 Log.d("hello","hlo");
                return "error";
            }
        return result;
//        URL url = new URL(link);
//                HttpClient client = new DefaultHttpClient();
//                HttpGet request = new HttpGet();
//                request.setURI(new URI(link));
//                HttpResponse response = client.execute(request);
//                BufferedReader in = new BufferedReader(new
//                        InputStreamReader(response.getEntity().getContent()));
//
//                StringBuffer sb = new StringBuffer("");
//                String line="";
//
//                while ((line = in.readLine()) != null)
//                {
//                    sb.append(line);
//                    break;
//                }
//                in.close();
//                return sb.toString();
//            } catch(Exception e){
//                 Log.d("hello","hlo");
//                return error;
//            }

    }

    @Override
    protected void onPostExecute(String result) {
        if (!TextUtils.isEmpty(result)&&!result.equals("null")) {


            if (result.equals("Exc")) {
                Toast.makeText(context, "Village doesn't exists", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(Checkout.this, FinalCheckout.class);
                intent.putExtra("namedata", name);
                startActivity(intent);

            }
        }
        else
        {
            Toast.makeText(context, "Network Error!!", Toast.LENGTH_SHORT).show();
        }
    }
}
}