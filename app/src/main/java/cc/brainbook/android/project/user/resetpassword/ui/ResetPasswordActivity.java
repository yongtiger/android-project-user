package cc.brainbook.android.project.user.resetpassword.ui;

import android.os.Bundle;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.widget.Toast;

import cc.brainbook.android.project.user.R;

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reset_password);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            showResetPasswordStep1Fragment();
        }
    }

    public void showResetPasswordStep1Fragment() {
        final ResetPasswordStep1Fragment resetPasswordStep1Fragment = ResetPasswordStep1Fragment.newInstance();

        // Execute a transaction, replacing any existing fragment
        // with this one inside the frame.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, resetPasswordStep1Fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public void showResetPasswordStep2Fragment() {
        final ResetPasswordStep2Fragment resetPasswordStep2Fragment = ResetPasswordStep2Fragment.newInstance();

        // Execute a transaction, replacing any existing fragment
        // with this one inside the frame.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, resetPasswordStep2Fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public void showResetPasswordStep3Fragment() {
        final ResetPasswordStep3Fragment resetPasswordStep3Fragment = ResetPasswordStep3Fragment.newInstance();

        // Execute a transaction, replacing any existing fragment
        // with this one inside the frame.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, resetPasswordStep3Fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public void showResetPasswordStep4Fragment() {
        final ResetPasswordStep4Fragment resetPasswordStep4Fragment = ResetPasswordStep4Fragment.newInstance();

        // Execute a transaction, replacing any existing fragment
        // with this one inside the frame.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, resetPasswordStep4Fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public void updateUI(@StringRes Integer successString) {
        Toast.makeText(getApplicationContext(), successString, Toast.LENGTH_LONG).show();
    }

    public void showFailedMessage(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
