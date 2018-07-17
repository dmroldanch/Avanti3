package mx.intelisis.maserp.avanti;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * A login screen that offers login via email/password.
 */
public class InicioSesion extends AppCompatActivity implements LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.

    listaSucursal[] datoSucursal;
    private Spinner sucuarslesSpiner;
    ArrayAdapter<String> adapterspin;
    private TextView fechaView;
    int sucursal;
    String usuario;
    private AutoCompleteTextView usuarioView;
    private EditText passwordView;
    private View mProgressView;
    private View mLoginFormView;
    String ip ="187.163.97.114:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);


        // Set up the login form.
        usuarioView = (AutoCompleteTextView) findViewById(R.id.textView_Usuario);
        populateAutoComplete();

        passwordView = (EditText) findViewById(R.id.textView_Password);


        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               usuario = usuarioView.getText().toString();
                autentificar();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        fechaView = (TextView)findViewById(R.id.sesion_fecha);

        InicializarFecha();
        llenarSucursales();
    }

    private void InicializarFecha() {
        int year_x, day_x, month_x;
        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);
        String fecha = day_x  + "/" +(month_x + 1) + "/" + year_x;
        fechaView.setText(fecha);

    }

    //llenar espiner Sucursales
    public void llenarSucursales() {
        SQLHelper BH = new SQLHelper(this, "Avanti", null, 1);
        SQLiteDatabase db = BH.getReadableDatabase();
        if (db != null) {
            Cursor c = db.rawQuery("select * from Sucursal", null);
            int cantidad = c.getCount();
            datoSucursal = new listaSucursal[cantidad];
            String[] arreglo = new String[cantidad];
            int i = 0;
            if (c.moveToFirst()) {
                do {

                    datoSucursal[i] = new listaSucursal(c.getInt(0),c.getString(1));
                    arreglo[i] = c.getString(1);

                    i++;
                } while (c.moveToNext());

            }
            adapterspin = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arreglo);
            sucuarslesSpiner = (Spinner) findViewById(R.id.spinner_Sucursal);
            sucuarslesSpiner.setAdapter(adapterspin);
            sucuarslesSpiner.setOnItemSelectedListener(InicioSesion.this);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
           sucursal = datoSucursal[position].getSucursal();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Se envian Datos para autentificar
    public void autentificar() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams parametros = new RequestParams();
        String url_v = "http://187.163.97.114:8080/inicioSesion.php";
        JSONObject data = new JSONObject();
        try {
            data.put("usuario", usuario);
            data.put("empresa","RTM" );
            data.put("sucursal",sucursal);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Falta ingresar Datos", Toast.LENGTH_SHORT).show();
        }
        parametros.put("data", data);
        client.post(url_v, parametros, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    //  llamar funcion ..
                    autentificarRespuesta(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "Sin Respuesta con el Servidor", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void autentificarRespuesta(String response) {

        Toast.makeText(this,"Se envio...esperando respuesta",Toast.LENGTH_SHORT).show();

        try {
            String mensaje;
            JSONArray jsonArray = new JSONArray(response);

            mensaje = jsonArray.getJSONObject(0).getString("Mensaje") ;

            if (mensaje.equals("Ok")){
                terminarActividad();
            }else{
                Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {

        }

    }

    public void terminarActividad(){
        Intent nuevaActivity = new Intent(InicioSesion.this,ProductoN.class);
        nuevaActivity.putExtra("usuario", usuario);
        nuevaActivity.putExtra("sucursal", sucursal);
        startActivity(nuevaActivity);
        finish();
    }



    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(InicioSesion.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        usuarioView.setAdapter(adapter);
    }



    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

