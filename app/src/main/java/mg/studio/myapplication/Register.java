package mg.studio.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


public class Register extends AppCompatActivity {
    private static final String TAG = Register.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private SessionManager session;
    private ProgressDialog pDialog;
    private String name;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        inputFullName = findViewById(R.id.name);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnRegister = findViewById(R.id.btnRegister);
        btnLinkToLogin = findViewById(R.id.btnLinkToLoginScreen);


        // Preparing the Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        // Session manager
        session = new SessionManager(getApplicationContext());
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    // Avoid repeated clicks by disabling the button
                    btnRegister.setClickable(false);
                    //Register the user
                    registerUser(name, email, password);


                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        Login.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Register a new user to the server database
     * @param name     username
     * @param email    email address, which should be unique to the user
     * @param password length should be < 50 characters
     */
    private void registerUser(final String name, final String email,
                              final String password) {

        pDialog.setMessage("Registering ...");
        if (!pDialog.isShowing()) pDialog.show();
        //Todo: Need to check Internet connection
        new DownloadData().execute(name, email, password);


    }


    class DownloadData extends AsyncTask<String, Void, Integer> {


        @Override
        protected Integer doInBackground(String... strings) {


            String NAME = strings[0];
            String EMAIL = strings[1];
            String PASSWORD = strings[2];
            final String PARAMS = NAME + "=" + strings[0] + "&" + EMAIL + "=" + strings[1] + "&" + PASSWORD + "=" + strings[2];


            SharedPreferences sharedPreferences = getSharedPreferences("UsersInfo", MODE_APPEND);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Log.w("TAG", "log" + EMAIL + "=" + strings[0] + "&" + PASSWORD + "=" + strings[1]);
            editor.putString("username", NAME);
            editor.putString("email",EMAIL);
            editor.putString("password", PASSWORD);
            editor.commit();
            return 0;
        }


        @Override
        protected void onPostExecute(Integer mFeedback) {
            super.onPostExecute(mFeedback);
            if (pDialog.isShowing()) pDialog.dismiss();
            Toast.makeText(getApplication(), "your userinfo has been saved with sharedPreferences.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
            finish();
        }

    }


}

