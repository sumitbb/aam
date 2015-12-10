package com.example.aam1.myapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class registration extends AppCompatActivity implements View.OnClickListener {

    Button btnSignUp;
    Toolbar toolbar;
    EditText etSellerName, etShopName, etContactNo, etEmail, etPassword, etCity, etOtherArea, etStartTime, etEndTime;
    TextView tvCountryCode, tvRegError, tvCategory;
    Spinner areaSpinner;
    private String category;
    private LinearLayout linearLayoutCategory;
    Boolean isInternetPresent = false;
    private String[] shopEmailStringArray, passwordStringArray, areaStringArray;
    JSONObject params;
    // Connection detector class
    ConnectionDetector cd;
    ProgressBar progressBar;
    Bundle bundle;
    String city;
    ImageView imgEyeClosed, imgEyeOpen;
    private LinearLayout linearLayout;

    String urlRegistration = "http://www.apneareamein.com/abhi/resgistration.php";
    String urlGetArea = "http://www.apneareamein.com/abhi/CityArea.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

//        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
//        setSupportActionBar(toolbar);
//
////        getSupportActionBar().setHomeButtonEnabled(true);
////        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        progressBar = (ProgressBar) findViewById(R.id.toolbar_progress_bar);
        //here we get values from bundle its coming from AddNewProduct.java class
        bundle = getIntent().getExtras();
        //city = bundle.getString("city");
        city="other";
        //initialization
        etSellerName = (EditText) findViewById(R.id.editSellerName);
        etShopName = (EditText) findViewById(R.id.editShopName);
        tvCountryCode = (TextView) findViewById(R.id.txtCountryCode);
        tvRegError = (TextView) findViewById(R.id.txtRegErrorMessage);
        etContactNo = (EditText) findViewById(R.id.editContact);
        etEmail = (EditText) findViewById(R.id.editEmail);

        etPassword = (EditText) findViewById(R.id.editPassword);
        etPassword.setTypeface(Typeface.DEFAULT);
        etPassword.setTransformationMethod(new PasswordTransformationMethod());

        etCity = (EditText) findViewById(R.id.editCity);
        tvCategory = (TextView) findViewById(R.id.txtCategory);
        etOtherArea = (EditText) findViewById(R.id.editOtherArea);
        linearLayoutCategory = (LinearLayout) findViewById(R.id.linearLayoutCategory);
        etCity.setText(city);
        areaSpinner = (Spinner) findViewById(R.id.area_spinner);
        etStartTime = (EditText) findViewById(R.id.editStartTime);
        etEndTime = (EditText) findViewById(R.id.editEndTime);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        imgEyeClosed = (ImageView) findViewById(R.id.imgEyeClosed);
        imgEyeOpen = (ImageView) findViewById(R.id.imgEyeOpen);

        imgEyeClosed.setOnClickListener(this);
        imgEyeOpen.setOnClickListener(this);
//        imgEyeClosed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//                etPassword.setSelection(etPassword.length());
//                etPassword.setVisibility(View.VISIBLE);
//            }
//        });
//        imgEyeOpen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                etPassword.setSelection(etPassword.length());
//                etPassword.setVisibility(View.GONE);
//            }
//        });



        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());

        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {
            String city = etCity.getText().toString();
            getArea(city);
        } else {
            // Internet connection is not present
            // Ask user to connect to Internet
            showAlertDialog(registration.this, "No Internet Connection",
                    "Please try again later.", false);
        }

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
            }
        });
        linearLayoutCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(registration.this, Category.class);
                startActivityForResult(intent, 1);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == 1) {
                category = data.getStringExtra("items");
                String[] Category = category.split("\\[");
                String[] finalCategory = Category[1].split("\\]");
                tvCategory.setText(finalCategory[0]);
            }  else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void getArea(String city) {
        RequestQueue queue = Volley.newRequestQueue(registration.this);

        JSONObject params = new JSONObject();
        try {
            Log.d("abhi", "	city::" + city);

            params.put("city", city);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, urlGetArea, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("abhi", "	incoming response in requestDataFromServer fn:: " + response.toString());

                //here the initialization of arrayList
                ArrayList<String> area = new ArrayList<String>();

                try {
                    JSONArray mainClassificationJsonArray = response.getJSONArray("posts");

                    for (int i = 0; i < mainClassificationJsonArray.length(); i++) {
                        JSONObject jSonClassificationData = mainClassificationJsonArray.getJSONObject(i);

                        //here we have take data from jSonObject
                        area.add(jSonClassificationData.getString("area")); //add to arraylist
                        Collections.sort(area);

                        areaStringArray = new String[area.size()];
                        areaStringArray = area.toArray(areaStringArray);

                    }
                } catch (JSONException e) {
                    Log.d("abhi", "	Error in the requestDataFromServer jsonObject:: " + e.toString());
                }
                if (areaStringArray != null) {
                    //this is for area_spinner
                    ArrayAdapter<String> spinnerAdapterArea = new ArrayAdapter<String>(registration.this, android.R.layout.simple_spinner_item, area); //selected item will look like a spinner set from XML
                    spinnerAdapterArea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    areaSpinner.setAdapter(spinnerAdapterArea);
                    spinnerAdapterArea.add("Other");
                    spinnerAdapterArea.notifyDataSetChanged();
                }
                areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String otherArea = areaSpinner.getSelectedItem().toString();
                        if (otherArea.equalsIgnoreCase("Other")) {
                            etOtherArea.setVisibility(View.VISIBLE);
                        } else {
                            etOtherArea.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.d("abhi", "	Error in the web Services at requestDataFromServer:: " + error.toString());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(registration.this, "TimeOutError," + error.toString(), Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    //TODO
                    Toast.makeText(registration.this, "AuthFailureError," + error.toString(), Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    //TODO
                    Toast.makeText(registration.this, "ServerError," + error.toString(), Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    //TODO
                    Toast.makeText(registration.this, "NetworkError," + error.toString(), Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    //TODO
                    Toast.makeText(registration.this, "ParseError," + error.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
        queue.add(request);

    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void validation() {
        params = new JSONObject();
        try {
            String seller_name = etSellerName.getText().toString();
            String shop_name = etShopName.getText().toString();
            String contact_no = etContactNo.getText().toString();
            String city = etCity.getText().toString();
            String area = areaSpinner.getSelectedItem().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String startTime = etStartTime.getText().toString();
            String endTime = etEndTime.getText().toString();
            String strCategory = tvCategory.getText().toString();
            String finalTime = startTime + " AM" + " - " + endTime + " PM";
            int length = contact_no.length();

            Log.d("abhi", "  final time::" + finalTime);
            if (seller_name.matches("")) {
                etSellerName.setError("You did not enter a seller name");
            } else if (shop_name.matches("")) {
                etShopName.setError("You did not enter a shop name");
            } else if (contact_no.matches("")) {
                etContactNo.setError("You did not enter a contact no");
            } else if (city.matches("")) {
                etCity.setError("You did not enter a city");
            } else if (startTime.matches("")) {
                etStartTime.setError("You did not enter start time");
            } else if (endTime.matches("")) {
                etEndTime.setError("You did not enter end time");
            } else if (email.matches("")) {
                etEmail.setError("You did not enter a email");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("You did not enter valid web address");
            } else if (password.matches("")) {
                etPassword.setError("You did not enter a password");
            } else if (strCategory.matches("")) {
                tvCategory.setError("You did not select a category");
            } else if (length < 10) {
                etContactNo.setError("Please enter valid mobile number");
            }else if (isInternetPresent) {
                params.put("sellername", seller_name);
                params.put("shop_name", shop_name);
                params.put("shop_time", finalTime);
                params.put("contact", contact_no);
                params.put("city", city);
                if (area.equalsIgnoreCase("Other")) {
                    String otherArea = etOtherArea.getText().toString();
                    Log.d("abhi", "	otherArea::" + otherArea);
                    params.put("area", otherArea);
                } else {
                    Log.d("abhi", "	area::" + area);
                    params.put("area", area);
                }
                params.put("category", tvCategory.getText().toString());
                params.put("email", email);
                params.put("password", password);
                sendDataToServer();
            } else {
                showAlertDialog(registration.this, "Oops! Something went wrong",
                        "Please check your internet connection and try again", false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showAlertDialog(Context context, String title, String message, Boolean status) {

        //android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(context).create();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        /*//Setting Dialog icon Image
        alertDialog.setIcon(R.mipmap.ic_no_network_found);*/

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting OK Button
        alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                registration.this.finish();
            }
        });

        // Showing Alert Message
        AlertDialog dialog = alertDialog.show();
        TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
        dialog.show();
    }

    private void sendDataToServer() {
        showPDialog();
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, urlRegistration, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // TODO Auto-generated method stub
                Log.d("abhi", "	incoming response in sendDataToServer registration.java fn:: " + response.toString());
                ArrayList<String> email = new ArrayList<String>();
                ArrayList<String> password = new ArrayList<String>();
                try {
                    JSONObject mainLoginJsonArray = response.getJSONObject("posts");
                    for (int i = 0; i < mainLoginJsonArray.length(); i++) {

                        email.add(mainLoginJsonArray.getString("email"));
                        password.add(mainLoginJsonArray.getString("password"));

                        shopEmailStringArray = new String[email.size()];
                        shopEmailStringArray = email.toArray(shopEmailStringArray);
                        passwordStringArray = new String[password.size()];
                        passwordStringArray = password.toArray(passwordStringArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (shopEmailStringArray == null) {
                    tvRegError.setVisibility(View.VISIBLE);
                } else {
                    tvRegError.setVisibility(View.GONE);
                    /*Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putInt("success", 1);
                    intent.putExtras(bundle);
                    setResult(1, intent);
                    finish();*/
                    showAlertDialogForApproval(registration.this, "Success",
                            "You can login after Approval.", false);
                }
                hidePDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.d("abhi", "	Error in the web Services at sendDataToServer Registration.java:: " + error.toString());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(registration.this, "TimeOutError, Please check internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    //TODO
                    Toast.makeText(registration.this, "AuthFailureError, Please re login", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    //TODO
                    Toast.makeText(registration.this, "ServerError, server is down now", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    //TODO
                    Toast.makeText(registration.this, "NetworkError, not fount Network", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    //TODO
                    Toast.makeText(registration.this, "ParseError, not parse jsonObject", Toast.LENGTH_LONG).show();
                }
                hidePDialog();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-type", "application/json");
                return headers;
            }
        };
        queue.add(request);
    }

    public void showAlertDialogForApproval(Context context, String title, String message, Boolean status) {

        //android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(context).create();
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);

        //Setting Dialog icon Image
        alertDialog.setIcon(R.mipmap.ic_no_network_found);

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting OK Button
        alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        // Showing Alert Message
        android.app.AlertDialog dialog = alertDialog.show();
        TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
        dialog.show();
    }

    private void showPDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hidePDialog() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case  R.id.imgEyeClosed:
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    etPassword.setSelection(etPassword.length());
                    imgEyeClosed.setVisibility(View.GONE);
                    imgEyeOpen.setVisibility(View.VISIBLE);
                break;

            case R.id.imgEyeOpen:
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etPassword.setSelection(etPassword.length());
                    imgEyeOpen.setVisibility(View.GONE);
                    imgEyeClosed.setVisibility(View.VISIBLE);
                break;
        }
    }
}
