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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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


public class Login extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private ProgressDialog progressDialog;
    private SessionManager session;
    private Button loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        /**
         * If the user just registered an account from Register.class,
         * the parcelable should be retrieved
         */

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.btnLogin);


        /**
         * Prepare the dialog to display when the login button is pressed
         */
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);


        /**
         * Use the SessionManager class to check whether
         * the user already logged in, is yest  then go to the MainActivity
         */
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }


    }

    /**
     * Process the user input and log in if credentials are correct
     * Disable the button while login is processing
     *
     * @param view from activity_login.xml
     */
    public void btnLogin(View view) {


        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        // Check for empty data in the form
        if (!email.isEmpty() && !password.isEmpty()) {

            // Avoid multiple clicks on the button
            loginButton.setClickable(false);

            //Todo : ensure the user has Internet connection

            // Display the progress Dialog
            progressDialog.setMessage("Logging in ...");
            if (!progressDialog.isShowing())
                progressDialog.show();

            //Todo: need to check weather the user has Internet before attempting checking the data
            // Start fetching the data from the Internet
            new OnlineCredentialValidation().execute(email, password);


        } else {
            // Prompt user to enter credentials
            Toast.makeText(getApplicationContext(),
                    R.string.enter_credentials, Toast.LENGTH_LONG)
                    .show();
        }
    }


    /**
     * Press the button register, go to Registration form
     *
     * @param view from the activity_login.xml
     */
    public void btnRegister(View view) {
        startActivity(new Intent(getApplicationContext(), Register.class));
        finish();
    }


    /**
     * Use the email and password provided to log the user in if the credentials are valid
     */


    class OnlineCredentialValidation extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            Log.w("TAG", "Email and Pass - "  );
            final String EMAIL = strings[0];
            final String PASSWORD = strings[1];
            final String PARAMS = EMAIL + "=" + strings[0] + "&" + PASSWORD + "=" + strings[1];
            String account = "";
            String password = "";
            Log.w("TAG", "Email and Pass - " + EMAIL + "=" + strings[0] + "&" + PASSWORD + "=" + strings[1]);
            SharedPreferences sharedPreferences = getSharedPreferences("UsersInfo",MODE_PRIVATE);
            account = sharedPreferences.getString("email","");
            password = sharedPreferences.getString("password","");
            Log.w("TAG", "2Email and Pass - " + account + "=" + strings[0] + "&" + password + "=" + strings[1]);
            if(EMAIL.equals(account)&&PASSWORD.equals(password))
                return 1;
            else
                return 0;
        }

        @Override
        protected void onPostExecute(Integer mFeedback) {
            super.onPostExecute(mFeedback);

            if (progressDialog.isShowing()) progressDialog.dismiss();
            if(mFeedback==1) {
                session.setLogin(true);
                Log.w("TAG", "main "  );
                Toast.makeText(getApplication(), "login success.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
            else {
                loginButton.setClickable(true);
                Toast.makeText(getApplication(), "Please check your email and password.", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Converts the contents of an InputStream to a String.
         */

    }

}
